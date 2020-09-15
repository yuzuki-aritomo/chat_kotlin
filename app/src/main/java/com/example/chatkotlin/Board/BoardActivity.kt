package com.example.chatkotlin.Board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.chatkotlin.R
import kotlinx.android.synthetic.main.activity_board.*

class BoardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        val button: Button = findViewById(R.id.btn_to_another_board_activity)
        button.setOnClickListener {
            val intent = Intent(this, AnotherBoardActivity::class.java)
            startActivity(intent)
        }
        // 実行ボタンタップ時
//        fun onButtonTapped(view: View?){
//            val intent = Intent(this, AnotherBoardActivity::class.java)
//            startActivity(intent)
//        }

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

