package com.example.chatkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_board.*

class BoardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        /// CustomSurfaceViewのインスタンスを生成しonTouchリスナーをセット
        val customSurfaceView = CustomSurfaceView(this, surfaceView)
        surfaceView.setOnTouchListener { v, event ->
            customSurfaceView.onTouch(event)
        }
        /// カラーチェンジボタンにリスナーをセット
        /// CustomSurfaceViewのchangeColorメソッドを呼び出す
        blackBtn.setOnClickListener {
            customSurfaceView.changeColor("black")
        }
        redBtn.setOnClickListener {
            customSurfaceView.changeColor("red")
        }
        greenBtn.setOnClickListener {
            customSurfaceView.changeColor("green")
        }

        /// リセットボタン
        resetBtn.setOnClickListener {
            customSurfaceView.reset()
        }
    }

}

