package com.example.chatkotlin.Board

import android.content.Intent
import android.graphics.Path
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.chatkotlin.R
import com.example.chatkotlin.Room.RoomMainActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_another_board.*
import kotlinx.android.synthetic.main.activity_board.*

class AnotherBoardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_another_board)


        /// CustomSurfaceViewのインスタンスを生成しonTouchリスナーをセット
        val customSurfaceView = CustomSurfaceView_read(this, surfaceView_read)
//        surfaceView_read.setOnTouchListener { v, event ->
//            customSurfaceView.onTouch(event)
//        }
        //activityの変更
        val button: Button = findViewById(R.id.btn_to_write_board)
        button.setOnClickListener {
            customSurfaceView.unlockCanvasAndPost()
            val intent = Intent(this, RoomMainActivity::class.java)
            startActivity(intent)
        }
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
data class Button_board(
    var color: String = "black",
    var reset: String = "reset"
)