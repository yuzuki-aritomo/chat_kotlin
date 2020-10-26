package com.example.chatkotlin.Board

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import com.google.firebase.database.FirebaseDatabase
import java.util.*


class CustomSurfaceView: SurfaceHolder.Callback{
    //private
    private var surfaceHolder: SurfaceHolder? = null
    private var paint: Paint? = null
    private var path: Path? = null

    var room_id = "room_extra"

    var color: Int? = null
    var prevBitmap: Bitmap? = null
    var anotherBitmap: Bitmap? = null

    //private
    private var prevCanvas: Canvas? = null
    private var canvas: Canvas? = null

    var width: Int? = null
    var height: Int? = null

    constructor(context: Context, surfaceView: SurfaceView){
        surfaceHolder = surfaceView.holder

        Log.d("room","massage from constructor")

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
//        surfaceHolder!!.setFormat(PixelFormat.TRANSPARENT)
//        surfaceView.setZOrderOnTop(true)

        /// コールバック
        surfaceHolder!!.addCallback(this)

        /// ペイント関連の設定
        paint = Paint()
        color = Color.BLACK
        paint!!.color = color as Int
        paint!!.style = Paint.Style.STROKE
        paint!!.strokeCap = Paint.Cap.ROUND
        paint!!.isAntiAlias = true
        paint!!.strokeWidth = width!!*0.03.toFloat()

    }
    /// surfaceViewが作られたとき
    override fun surfaceCreated(holder: SurfaceHolder?) {
        Log.d("room","surfaceCreated")
        /// bitmap,canvas初期化
        initializeBitmap()
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?,) {
        Log.d("room","destroyed")
        /// bitmapをリサイクル
        prevBitmap!!.recycle()
//        canvas!!.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//        surfaceHolder = null
//        paint = null
//        path = null
//        color = null
//        prevBitmap = null
//        prevCanvas = null
//        canvas = null
//        width = null
//        height = null
    }
    fun set_room_id(room: String){
        room_id = room
    }
    fun unlockCanvasAndPost(){
        canvas = Canvas()
        /// ロックしてキャンバスを取得
        canvas = surfaceHolder!!.lockCanvas()
        //ロックを解除しないとエラーになる
        surfaceHolder!!.unlockCanvasAndPost(canvas)
    }
    /// bitmapとcanvasの初期化
    private fun initializeBitmap() {
        Log.d("test","-------------")
        if (prevBitmap == null) {
            //bitmapがない時作る
            prevBitmap = Bitmap.createBitmap(width!!, height!!, Bitmap.Config.ARGB_8888)
            anotherBitmap = Bitmap.createBitmap(width!!, height!!, Bitmap.Config.ARGB_8888)
            Log.d("room","massage from prevBitmap")
        }

        if (prevCanvas == null) {
            //canvasが情報ない時bimapで作る
            prevCanvas = Canvas(prevBitmap!!)
            //Log.d("board", prevBitmap!!)
            Log.d("room","massage from prevCanvas")
        }

        prevCanvas!!.drawColor(Color.WHITE)
        val canvas = surfaceHolder!!.lockCanvas()
        canvas!!.drawColor(Color.WHITE)
        surfaceHolder!!.unlockCanvasAndPost(canvas)
    }
    private fun draw(pathInfo: pathInfo) {
        canvas = Canvas()

//        if(surfaceHolder!!.lockCanvas() == null){
//            return
//        }

        /// ロックしてキャンバスを取得
        canvas = surfaceHolder!!.lockCanvas()

        //// キャンバスのクリア
        canvas!!.drawColor(Color.WHITE)

        /// 前回のビットマップをキャンバスに描画
        canvas!!.drawBitmap(prevBitmap!!, 0F, 0F, null)

        // pathを描画
        paint!!.color = pathInfo.color
        canvas!!.drawPath(pathInfo.path, paint!!)

        /// ロックを解除
        surfaceHolder!!.unlockCanvasAndPost(canvas)
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
        val database = FirebaseDatabase.getInstance().getReference("Room/$room_id/draw")

        val x_width = x/width!!
        val y_height = y/height!!
//        val x_data = x_width.toString()
//        val y_data = y_height.toString()

        val xyDataUpdate: MutableMap<String, Any> = HashMap()
        xyDataUpdate["x"] = x_width
        xyDataUpdate["y"] = y_height
        database.child("draw_down").updateChildren(xyDataUpdate)
//        database.child("draw_move").child("x").setValue(x_width)
//        database.child("draw_move").child("y").setValue(y_height)

        path = Path()
        path!!.moveTo(x, y)
    }

    ///    ACTION_MOVE 時の処理
    private fun touchMove(x: Float, y: Float) {
        val database = FirebaseDatabase.getInstance().getReference("Room/$room_id/draw")
        val x_width = x/width!!
        val y_width = y/height!!

//        val x_data = x_width
//        val y_data = y_width
        database.child("draw_move").child("x").setValue(x_width)
        database.child("draw_move").child("y").setValue(y_width)

        path!!.lineTo(x, y)
        draw(pathInfo(path!!, color!!))
    }

    ///    ACTION_UP 時の処理
    private fun touchUp(x: Float, y: Float) {
        val database = FirebaseDatabase.getInstance().getReference("Room/$room_id//draw")
        val x_width = x/width!!
        val y_width = y/height!!
//        val x_data = x_width.toString()
//        val y_data = y_width.toString()

        //firebaseに同時保存
        val xyDataUpdate: MutableMap<String, Any> = HashMap()
        xyDataUpdate["x"] = x_width
        xyDataUpdate["y"] = y_width
        database.child("draw_up").updateChildren(xyDataUpdate)

        val xyDataUpdate_move: MutableMap<String, Any> = HashMap()
        xyDataUpdate_move["x"] = 0F
        xyDataUpdate_move["y"] = 0F
        database.child("draw_move").updateChildren(xyDataUpdate_move)

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
        canvas?.drawColor(Color.WHITE)
        surfaceHolder!!.unlockCanvasAndPost(canvas)

        val ref = FirebaseDatabase.getInstance().getReference("Room/$room_id/draw/btn/reset")
        ref.setValue("reset")
        ref.setValue("a")
    }

    /// color チェンジメソッド
    fun changeColor(colorSelected: String) {
        when (colorSelected) {
            "black" -> color = Color.BLACK
            "red" -> color = Color.RED
            "green" -> color = Color.GREEN
            "white" -> color = Color.WHITE
        }
        paint!!.color = color as Int

        val ref = FirebaseDatabase.getInstance().getReference("Room/$room_id/draw/btn/color")
        ref.setValue(colorSelected)
    }

    //------------------------------------
    //watchの修正
    //-----------------------------------
    fun onTouch_watch(event: MotionEvent) : Boolean{
        return true
    }
    fun touchDown_watch(x: Float, y: Float) {
        val x_width = x*width!!
        val y_width = y*height!!
        path = Path()
        path!!.moveTo(x_width, y_width)
    }
    fun touchMove_watch(x: Float, y: Float) {
        val x_width = x*width!!
        val y_width = y*height!!
        /// pathクラスとdrawメソッドで線を書く
        path!!.lineTo(x_width, y_width)
        Log.d("test", "dd")
        draw(pathInfo(path!!, color!!))
    }
    fun touchUp_watch(x: Float, y: Float) {
        val x_width = x*width!!
        val y_width = y*height!!
        /// 前回のキャンバスを描画 path がnullの時エラー発生
        if(path == null){
            return
        }
        path!!.lineTo(x_width, y_width)
        draw(pathInfo(path!!, color!!))
        prevCanvas!!.drawPath(path!!, paint!!)
    }
    fun reset_watch(){
        ///初期化とキャンバスクリア
        initializeBitmap()
        canvas = surfaceHolder!!.lockCanvas()
        canvas?.drawColor(Color.WHITE)
        surfaceHolder!!.unlockCanvasAndPost(canvas)
    }
    fun changeColor_watch(colorSelected: String) {
        when (colorSelected) {
            "black" -> color = Color.BLACK
            "red" -> color = Color.RED
            "green" -> color = Color.GREEN
            "white" -> color = Color.WHITE
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
    var x: Float? = null,
    var y: Float? = null,
)