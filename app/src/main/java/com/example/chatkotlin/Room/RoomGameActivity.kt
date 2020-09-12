package com.example.chatkotlin.Room

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.chatkotlin.Board.*
import com.example.chatkotlin.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_another_board.*
import kotlinx.android.synthetic.main.activity_board.*
import kotlinx.android.synthetic.main.activity_board.blackBtn
import kotlinx.android.synthetic.main.activity_board.greenBtn
import kotlinx.android.synthetic.main.activity_board.redBtn
import kotlinx.android.synthetic.main.activity_board.resetBtn


class RoomGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_game)

        val room_id = intent.getStringExtra("room_id")

        val intent = Intent(this, BoardActivity::class.java)
        intent.putExtra("room_id", room_id)
        startActivity(intent)
        //room_id/IfGameOrNot を変更


        //ボードに書く関数
        setScreenWrite()
    }
    //ボードに書く人
    private fun setScreenWrite() {
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
    //ボードを見る人
    private fun setScreenWatch() {

        setContentView(R.layout.activity_another_board)
        /// CustomSurfaceViewのインスタンスを生成しonTouchリスナーをセット
        val customSurfaceView = CustomSurfaceView_read(this, surfaceView_read)

        val pass_down = FirebaseDatabase.getInstance().getReference("draw/draw_down")
        pass_down.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val draw_down =  snapshot.getValue(Draw_data::class.java)
                val x_string : String = draw_down?.x.toString()
                val y_string = draw_down?.y.toString()

                //string からfloatに変換
                val x : Float = x_string.toFloat()
                val y : Float = y_string.toFloat()

                Log.d("firebase", "down")
                customSurfaceView.touchDown(x,y)
            }

            override fun onCancelled(error: DatabaseError) {
                //エラー処理
            }
        })

        val pass_move = FirebaseDatabase.getInstance().getReference("/draw/draw_move")
        pass_move.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val draw_down =  snapshot.getValue(Draw_data::class.java)
                val x_string : String = draw_down?.x.toString()
                val y_string = draw_down?.y.toString()

                if( x_string != "" && y_string != ""){
                    //string からfloatに変換
                    val x : Float = x_string.toFloat()
                    val y : Float = y_string.toFloat()
                    customSurfaceView.touchMove(x,y)
                }

                Log.d("firebase", "move")
            }

            override fun onCancelled(error: DatabaseError) {
                //エラー処理
            }
        })

        val pass_up = FirebaseDatabase.getInstance().getReference("/draw/draw_up")
        pass_up.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val draw_down =  snapshot.getValue(Draw_data::class.java)
                val x_string : String = draw_down?.x.toString()
                val y_string = draw_down?.y.toString()

                //string からfloatに変換
                val x : Float = x_string.toFloat()
                val y : Float = y_string.toFloat()

                Log.d("firebase", "up")
                customSurfaceView.touchUp(x,y)
            }

            override fun onCancelled(error: DatabaseError) {
                //エラー処理
            }
        })

        // 色を変えた場合の処理
        val color_ref = FirebaseDatabase.getInstance().getReference("/draw/btn")
        color_ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val btn_ref =  snapshot.getValue(Button_board::class.java)
                val selectedcolor = btn_ref?.color
                customSurfaceView.changeColor(selectedcolor!!)
            }
            override fun onCancelled(error: DatabaseError) {
                //エラー処理
            }
        })

        // リセットボタンの処理
        val reset_ref = FirebaseDatabase.getInstance().getReference("/draw/btn")
        reset_ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val btn_ref =  snapshot.getValue(Button_board::class.java)
                if(btn_ref?.reset == "reset"){
                    customSurfaceView.reset()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                //エラー処理
            }
        })
    }
}