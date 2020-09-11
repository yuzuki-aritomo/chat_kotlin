package com.example.chatkotlin

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Base64.encodeToString
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.*



class CustomSurfaceView: SurfaceView, SurfaceHolder.Callback{
    private var surfaceHolder: SurfaceHolder? = null
    private var paint: Paint? = null
    private var path: Path? = null
    var color: Int? = null
    var prevBitmap: Bitmap? = null
    var anotherBitmap: Bitmap? = null
    private var prevCanvas: Canvas? = null
    private var canvas: Canvas? = null

    var width: Int? = null
    var height: Int? = null

    constructor(context: Context, surfaceView: SurfaceView) : super(context) {
        surfaceHolder = surfaceView.holder

        /// display の情報（高さ 横）を取得
        val size = Point().also {
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.apply {
                getSize(
                    it
                )
            }
        }

        /// surfaceViewのサイズ
        width = size.x
        height = size.y

        /// 背景を透過させ、一番上に表示
        surfaceHolder!!.setFormat(PixelFormat.TRANSPARENT)
        surfaceView.setZOrderOnTop(true)

        /// コールバック
        surfaceHolder!!.addCallback(this)

        /// ペイント関連の設定
        paint = Paint()
        color = Color.BLACK
        paint!!.color = color as Int
        paint!!.style = Paint.Style.STROKE
        paint!!.strokeCap = Paint.Cap.ROUND
        paint!!.isAntiAlias = true
        paint!!.strokeWidth = 15F
    }
    /// surfaceViewが作られたとき
    override fun surfaceCreated(holder: SurfaceHolder?) {
        /// bitmap,canvas初期化
        initializeBitmap()
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        /// bitmapをリサイクル
        prevBitmap!!.recycle()
        anotherBitmap!!.recycle()
    }


    /// bitmapとcanvasの初期化
    private fun initializeBitmap() {
        if (prevBitmap == null) {
            //bitmapがない時作る
            prevBitmap = Bitmap.createBitmap(width!!, height!!, Bitmap.Config.ARGB_8888)
            anotherBitmap = Bitmap.createBitmap(width!!, height!!, Bitmap.Config.ARGB_8888)
        }

        if (prevCanvas == null) {
            //canvasが情報ない時bimapで作る
            prevCanvas = Canvas(prevBitmap!!)
            //Log.d("board", prevBitmap!!)
        }

        prevCanvas!!.drawColor(0, PorterDuff.Mode.CLEAR)
    }
    private fun draw(pathInfo: pathInfo) {
        canvas = Canvas()

        /// ロックしてキャンバスを取得
        canvas = surfaceHolder!!.lockCanvas()

        //// キャンバスのクリア
        canvas!!.drawColor(0, PorterDuff.Mode.CLEAR)

        /// 前回のビットマップをキャンバスに描画
        canvas!!.drawBitmap(prevBitmap!!, 0F, 0F, null)

        //var a = getStringFromBitmap(prevBitmap!!)
//        var a = BitMapToString(prevBitmap!!)
//        Log.d("board", a)
//        anotherBitmap = a?.let { getBitmapFromString(it) }



        //Bitmap → byte配列(bmp形式)
//        val byteBuffer: ByteBuffer = ByteBuffer.allocate(prevBitmap!!.getByteCount())
//        prevBitmap!!.copyPixelsToBuffer(byteBuffer)
//        val bmparr: ByteArray = byteBuffer.array()
//        //byte配列(bmp) → Bitmap
//        prevBitmap!!.copyPixelsFromBuffer(ByteBuffer.wrap(bmparr));

        //canvas!!.drawBitmap(prevBitmap!!, 0F, 0F, null)

        //// pathを描画
        paint!!.color = pathInfo.color
        canvas!!.drawPath(pathInfo.path, paint!!)

        /// ロックを解除
        surfaceHolder!!.unlockCanvasAndPost(canvas)
    }

    /*
 * This functions converts Bitmap picture to a string which can be
 * JSONified.
 * */
    @SuppressLint("WrongThread")
    private fun getStringFromBitmap(bitmapPicture: Bitmap): String? {
        val COMPRESSION_QUALITY = 100
        val encodedImage: String
        val byteArrayBitmapStream = ByteArrayOutputStream()
        bitmapPicture.compress(
            Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
            byteArrayBitmapStream
        )
        val b: ByteArray = byteArrayBitmapStream.toByteArray()
        //encodedImage = Base64.encodeToString(b, android.util.Base64.DEFAULT)
        encodedImage = Base64.getEncoder().encodeToString(b)
        return encodedImage
    }
    @SuppressLint("WrongThread")
    private fun encodeFromImage(bitmap: Bitmap): String? {
        var encode: String? = null
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        //encode = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
        //String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,"Title",null);
        encode = Base64.getEncoder().encodeToString(stream.toByteArray())
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Base64.getEncoder().encodeToString(stream.toByteArray())
//        } else {
//            encodeToString(stream.toByteArray(), android.util.Base64.DEFAULT)
//            }
        return encode
    }
    @SuppressLint("WrongThread")
    fun BitMapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
        return android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT)
    }

    /// 画面をタッチしたときにアクションごとに関数を呼び出す
    fun onTouch(event: MotionEvent) : Boolean{
        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchDown(event.x, event.y)
            MotionEvent.ACTION_MOVE -> touchMove(event.x, event.y)
            MotionEvent.ACTION_UP -> touchUp(event.x, event.y)
        }
        return true
    }

    ///// path クラスで描画するポイントを保持
    ///    ACTION_DOWN 時の処理
    private fun touchDown(x: Float, y: Float) {
        val database = FirebaseDatabase.getInstance().getReference("/draw")
        val x_data = x.toString()
        val y_data = y.toString()
        database.child("draw_down").child("x").setValue(x_data)
        database.child("draw_down").child("y").setValue(y_data)

//        val pass = FirebaseDatabase.getInstance().getReference("/draw/draw_down")
//        database.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val draw_down =  snapshot.getValue(Draw_data::class.java)
//                val x_string : String = draw_down?.x.toString()
//                val y_string = draw_down?.y.toString()
//
//                //string からfloatに変換
//                val x : Float = x_string.toFloat()
//                val y : Float = y_string.toFloat()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                //エラー処理
//            }
//        })

        Log.d("draw", x.toString() )
        path = Path()
        path!!.moveTo(x, y)
    }

    ///    ACTION_MOVE 時の処理
    private fun touchMove(x: Float, y: Float) {
        val database = FirebaseDatabase.getInstance().getReference("/draw")
        val x_data = x.toString()
        val y_data = y.toString()
        database.child("draw_move").child("x").setValue(x_data)
        database.child("draw_move").child("y").setValue(y_data)

//        val pass = FirebaseDatabase.getInstance().getReference("/draw/draw_move")
//        database.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val draw_down =  snapshot.getValue(Draw_data::class.java)
//                val x_string : String = draw_down?.x.toString()
//                val y_string = draw_down?.y.toString()
//
//                //string からfloatに変換
//                val x : Float = x_string.toFloat()
//                val y : Float = y_string.toFloat()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                //エラー処理
//            }
//        })
        /// pathクラスとdrawメソッドで線を書く
        path!!.lineTo(x, y)
        draw(pathInfo(path!!, color!!))
    }

    ///    ACTION_UP 時の処理
    private fun touchUp(x: Float, y: Float) {
        val database = FirebaseDatabase.getInstance().getReference("/draw")
        val x_data = x.toString()
        val y_data = y.toString()
        database.child("draw_up").child("x").setValue(x_data)
        database.child("draw_up").child("y").setValue(y_data)
//
//        val pass = FirebaseDatabase.getInstance().getReference("/draw/draw_up")
//        database.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val draw_down =  snapshot.getValue(Draw_data::class.java)
//                val x_string : String = draw_down?.x.toString()
//                val y_string = draw_down?.y.toString()
//
//                //string からfloatに変換
//                val x : Float = x_string.toFloat()
//                val y : Float = y_string.toFloat()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                //エラー処理
//            }
//        })
        /// pathクラスとdrawメソッドで線を書く
        path!!.lineTo(x, y)
        draw(pathInfo(path!!, color!!))
        /// 前回のキャンバスを描画
        prevCanvas!!.drawPath(path!!, paint!!)
    }
    /// resetメソッド
    fun reset() {
        ///初期化とキャンバスクリア
        initializeBitmap()
        canvas = surfaceHolder!!.lockCanvas()
        canvas?.drawColor(0, PorterDuff.Mode.CLEAR)
        surfaceHolder!!.unlockCanvasAndPost(canvas)
    }

    /// color チェンジメソッド
    fun changeColor(colorSelected: String) {
        when (colorSelected) {
            "black" -> color = Color.BLACK
            "red" -> color = Color.RED
            "green" -> color = Color.GREEN
        }
        paint!!.color = color as Int
    }
}

//// pathクラスの情報とそのpathの色情報を保存する
data class pathInfo(
    var path: Path,
    var color: Int
)

data class Draw_data(
    var x: String? = "",
    var y: String? = "",
)