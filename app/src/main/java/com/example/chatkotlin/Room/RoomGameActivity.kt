//package com.example.chatkotlin.Room
//
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import androidx.appcompat.app.AppCompatActivity
//import com.example.chatkotlin.Board.Button_board
//import com.example.chatkotlin.Board.CustomSurfaceView
//import com.example.chatkotlin.Board.CustomSurfaceView_read
//import com.example.chatkotlin.Board.Draw_data
//import com.example.chatkotlin.R
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//import kotlinx.android.synthetic.main.activity_another_board.*
//import kotlinx.android.synthetic.main.activity_board.*
//
//
//class RoomGameActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_board)
//
//        val room_id = intent.getStringExtra("room_id")
//
////        setContentView(R.layout.activity_room_game)
//
//        setScreenWrite()
//    }
//
//
//    /// CustomSurfaceViewのインスタンスを生成しonTouchリスナーをセット
//
//
//    //---------------------------------
//    //ボードに書くレイアウトのスタート
//    // --------------------------------
//    private fun setScreenWrite() {
//        setContentView(R.layout.activity_board)
//
//        val customSurfaceView_write = CustomSurfaceView(this, surfaceView_write)
//        Log.d("type", customSurfaceView_write.javaClass.kotlin.toString())
//
//        /// CustomSurfaceViewのインスタンスを生成しonTouchリスナーをセット
//        surfaceView_write.setOnTouchListener { v, event ->
//            customSurfaceView_write.onTouch(event)
//        }
//
//        /// カラーチェンジボタンにリスナーをセット
//        /// CustomSurfaceViewのchangeColorメソッドを呼び出す
//        blackBtn.setOnClickListener {
//            customSurfaceView_write.changeColor("black")
//        }
//        redBtn.setOnClickListener {
//            customSurfaceView_write.changeColor("red")
//        }
//        greenBtn.setOnClickListener {
//            customSurfaceView_write.changeColor("green")
//        }
//
//        /// リセットボタン
//        btn_board_reset.setOnClickListener {
//            customSurfaceView_write.reset()
//        }
//        //見る画面に移動
//        val button: Button = findViewById(R.id.btn_to_another_board_activity)
//        button.setOnClickListener {
////            surfaceView_write.setZOrderOnTop(false)
//            setScreenWatch()
////            customSurfaceView_write = null
//        }
//    }
//
//    //---------------------------------
//    // ボードを見る人のレイアウト表示
//    //--------------------------------
//    private fun setScreenWatch() {
//        setContentView(R.layout.activity_another_board)
//        val customSurfaceView_read  = CustomSurfaceView_read(this, surfaceView_read)
//
//        //書く画面に移動
//        val button: Button = findViewById(R.id.btn_to_write_board)
//        button.setOnClickListener {
////            surfaceView_read.setZOrderOnTop(false)
//            setScreenWrite()
//        }
//
//        val pass_down = FirebaseDatabase.getInstance().getReference("draw/draw_down")
//        pass_down.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val draw_down = snapshot.getValue(Draw_data::class.java)
//                val x_string: String = draw_down?.x.toString()
//                val y_string = draw_down?.y.toString()
//
//                //string からfloatに変換
//                val x: Float = x_string.toFloat()
//                val y: Float = y_string.toFloat()
//
//                Log.d("firebase", "down")
//                customSurfaceView_read.touchDown(x, y)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                //エラー処理
//            }
//        })
//
//        val pass_move = FirebaseDatabase.getInstance().getReference("/draw/draw_move")
//        pass_move.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val draw_down = snapshot.getValue(Draw_data::class.java)
//                val x_string: String = draw_down?.x.toString()
//                val y_string = draw_down?.y.toString()
//
//                if (x_string != "" && y_string != "") {
//                    //string からfloatに変換
//                    val x: Float = x_string.toFloat()
//                    val y: Float = y_string.toFloat()
//                    customSurfaceView_read.touchMove(x, y)
//                }
//
//                Log.d("firebase", "move")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                //エラー処理
//            }
//        })
//
//        val pass_up = FirebaseDatabase.getInstance().getReference("/draw/draw_up")
//        pass_up.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val draw_down = snapshot.getValue(Draw_data::class.java)
//                val x_string: String = draw_down?.x.toString()
//                val y_string = draw_down?.y.toString()
//
//                //string からfloatに変換
//                val x: Float = x_string.toFloat()
//                val y: Float = y_string.toFloat()
//
//                Log.d("firebase", "up")
//                customSurfaceView_read.touchUp(x, y)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                //エラー処理
//            }
//        })
//
//        // 色を変えた場合の処理
//        val color_ref = FirebaseDatabase.getInstance().getReference("/draw/btn")
//        color_ref.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val btn_ref = snapshot.getValue(Button_board::class.java)
//                val selectedcolor = btn_ref?.color
//                customSurfaceView_read.changeColor(selectedcolor!!)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                //エラー処理
//            }
//        })
//
//        // リセットボタンの処理
//        val reset_ref = FirebaseDatabase.getInstance().getReference("/draw/btn")
//        reset_ref.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val btn_ref = snapshot.getValue(Button_board::class.java)
//                if (btn_ref?.reset == "reset") {
//                    customSurfaceView_read.reset()
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                //エラー処理
//            }
//        })
//    }
//}