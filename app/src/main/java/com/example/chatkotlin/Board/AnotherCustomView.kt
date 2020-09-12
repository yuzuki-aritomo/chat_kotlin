package com.example.chatkotlin.Board

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_board.view.*

@SuppressLint("ViewConstructor")
class CustomSurfaceView_read: SurfaceView, SurfaceHolder.Callback{
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

        Log.d("const", "===============")
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
        paint!!.setPathEffect(CornerPathEffect(10f))
        paint!!.strokeWidth = width!!*0.03.toFloat()

        Log.d("room1","massage from constructor")
        initializeBitmap()
    }
    /// surfaceViewが作られたとき
    override fun surfaceCreated(holder: SurfaceHolder?) {
        /// bitmap,canvas初期化
        initializeBitmap()
        Log.d("room1","massage from surfaceCreated")
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        /// bitmapをリサイクル
        prevBitmap!!.recycle()
        Log.d("room1","massage from surfaceDestroyed")
    }


    /// bitmapとcanvasの初期化
    private fun initializeBitmap() {
        if (prevBitmap == null) {
            //bitmapがない時作る
            Log.d("room1","prevBitmap")
            prevBitmap = Bitmap.createBitmap(width!!, height!!, Bitmap.Config.ARGB_8888)
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

        if(canvas == null){
            return
        }
        //// キャンバスのクリア
        canvas!!.drawColor(0, PorterDuff.Mode.CLEAR)

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

    public fun touchDown(x: Float, y: Float) {

        val x_width = x*width!!
        val y_width = y*height!!
        path = Path()
        path!!.moveTo(x_width, y_width)
    }

    ///    ACTION_MOVE 時の処理
    public fun touchMove(x: Float, y: Float) {
        val x_width = x*width!!
        val y_width = y*height!!
        /// pathクラスとdrawメソッドで線を書く
        path!!.lineTo(x_width, y_width)
        Log.d("test","dd" )
        draw(pathInfo(path!!, color!!))
    }

    ///    ACTION_UP 時の処理
    public fun touchUp(x: Float, y: Float) {

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