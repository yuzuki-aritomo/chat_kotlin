package com.example.chatkotlin.Room

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.chatkotlin.Board.CustomSurfaceView
import com.example.chatkotlin.Board.Draw_data
import com.example.chatkotlin.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_board.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset


class RoomMainActivity : AppCompatActivity() {
    var i: Int = 0
    var room_id: String= "room_extra"
    var user_id: String= "room_extra"
    var user_count: Int = 0
    var game_set: Int = 1
    var game_set_max: Int = 0
    var answer = "aaa" //お題の答え
    var user_list = arrayOf<String>("aa","aa","aa","aa","aa")
    val hand0= Handler()

    private var questionItem: List<*> = ArrayList<Any?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //room_idの設定
        room_id = intent.getStringExtra("room_id").toString()//room_id: room_1
        user_id = intent.getStringExtra("user_id").toString() //room_id: room_1
        user_count = intent.getStringExtra("user_count").toInt() //room_id: room_1

//        room_id = "room_1"
//        user_id = "-MHUSFkLXeAht73QVyuz"
        user_count = 5

        //gameのセット数
        when(user_count){
            2 -> game_set_max = 6
            3 -> game_set_max = 6
            4 -> game_set_max = 4
            5 -> game_set_max = 5
        }

//        val ref_gameset = FirebaseDatabase.getInstance().getReference("Room/$room_id/Game")
//        ref_gameset.child("game_set_max").setValue(game_set_max)

        setContentView(R.layout.activity_board)
        layout_construct()

        val customSurfaceView = CustomSurfaceView(this, surfaceView_write)
        //初期設定
        //customSurfaceViewにroom_idを渡す
        customSurfaceView.set_room_id(room_id)
        //firebaseに初期値を入れる
        set_firebase_construct()
        val btn: Button = findViewById(R.id.btn_to_another_board_activity)

        i = 0
        firebase_watch(customSurfaceView)
        surface_write_fun(customSurfaceView)


        //user情報を排列に代入
//        user_list = arrayOf(user_count)//user_idを排列に入れている
        var abc = 0
        //user情報を排列に代入
        val ref = FirebaseDatabase.getInstance().getReference("Room/$room_id/user")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    if(abc<user_count){
                        Log.d("NewMassage", it.toString())
                        val user_id_num = it.child("user_id").getValue()
                        user_list[abc] = user_id_num.toString()
                        abc++
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })


        game_set = 1

        //データベースを削除しなければエラー（古い順化から取得するため）
        //代入の前に読み込んでしまうため遅らせる
//        hand0.postDelayed(Runnable {
//            if(user_id == user_list[game_set % user_count]){
//                //writeの関数
//                //お題の選定とfirebaseに保存
//
//                //writeのレイアウト
//                i = 2
//                layout_write("お題")
//                surface_write_fun(customSurfaceView)
//            }else{
//                //watchの関数
//                //watchのレイアウト
//                i = 1
//                layout_watch()
//                surface_watch_fun(customSurfaceView)
//            }
//            game_set++
//        },500)
        //game_set=1


        //game_setが終わったかどうかを取ってくる(firebaseに変更があったら) 1回目以降
        val ref_set = FirebaseDatabase.getInstance().getReference("Room/$room_id/game/game_set")
        ref_set.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                Log.d("user_list",user_list[0])
                Log.d("user_list",user_list[1])
                Log.d("user_list","user_id:$user_id")
                Log.d("user_list","user_count:$user_count")
                Log.d("user_list",user_list[game_set % user_count])
                //gameが終了するかどうか
                if(game_set+1 == game_set_max){
                    //ゲームの終了画面に移行
                    Log.d("test","end game")
                    //Firebaseの初期化
                    FirebaseDatabase.getInstance().getReference("Room/$room_id/game/game_set")
                }
                //game_set % user_count の値の人が書く人
                if(user_id == user_list[game_set % user_count]){
                    //お題の選定とfirebaseに保存

                    //writeの関数
                    i = 2
                    layout_write("お題")
                    surface_write_fun(customSurfaceView)
                }else{
                    //watchの関数
                    //watchのレイアウト
                    i = 1
                    layout_watch()
                    surface_watch_fun(customSurfaceView)
                }
                //game_setが終われば
                game_set++
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })

        //答えの表示






        //それぞれのviewでの切り替え
        btn.setOnClickListener{
//            if(i%2==1){
//                i = i + 1
////                layout_write()
////                surface_write_fun(customSurfaceView)
//
//            }else if(i%2==0){
//                i = i + 1
//                //surfaceviewの無効化
////                customSurfaceView.setOnTouchListener { v, event ->
////                    Log.d("event", "not write")
////                    customSurfaceView.onTouch_watch(event)
////                }
////                layout_watch()
////                surface_watch_fun(customSurfaceView)
//            }
            Log.d("user_",user_list[game_set % user_count])
//            game_set++
            val ref__ = FirebaseDatabase.getInstance().getReference("Room/$room_id/game")
            ref__.child("game_set").setValue(game_set)
        }


        readQuestionData()
        val a = RandomChoice()
        val b = RandomChoice()
        Log.d("read", "$a")
        Log.d("read", "$b")


    }


    fun surface_watch_fun(customSurfaceView: CustomSurfaceView){

        //messageの送信
        room_message_btn.setOnClickListener {
            if(room_message_text.text.toString() != "null"){
                val text: String = room_message_text.text.toString()
                if(text == answer){
                    //firebaseに保存
                    val refe = FirebaseDatabase.getInstance().getReference("Room/$room_id/game/game_set")
                    refe.setValue(game_set)
                }
                val ref = FirebaseDatabase.getInstance().getReference("Room/$room_id/Message")
                ref.child("text").setValue(text)
                ref.child("from_user_id").setValue(user_id)
                //データベースの取得でメッセージの表示
                room_message_text.text = null
            }
        }
    }
    
    var aa = 0
    fun set_answer(answer_text: String){
        when(aa%3){
            0 -> {
                room_answer_text_1.text = answer_text
                room_answer_text_1.setVisibility(View.VISIBLE)
                hand0.postDelayed(Runnable {
                    room_answer_text_1.setVisibility(View.INVISIBLE)
                },3000)
            }
            1 -> {
                room_answer_text_2.text = answer_text
                room_answer_text_2.setVisibility(View.VISIBLE)
                hand0.postDelayed(Runnable {
                    room_answer_text_2.setVisibility(View.INVISIBLE)
                },3000)
            }
            2 -> {
                room_answer_text_3.text = answer_text
                room_answer_text_3.setVisibility(View.VISIBLE)
                hand0.postDelayed(Runnable {
                    room_answer_text_3.setVisibility(View.INVISIBLE)
                },3000)
            }
        }
        aa++
    }

    fun layout_construct(){
        //answer message
        room_answer_text_1.setVisibility(View.GONE)
        room_answer_text_2.setVisibility(View.GONE)
        room_answer_text_3.setVisibility(View.GONE)
        textview_announce_write.setVisibility(View.GONE)
        textview_announce_watch.setVisibility(View.GONE)
        textView_odai.setVisibility(View.GONE)

        blackBtn.setVisibility(View.INVISIBLE)
        redBtn.setVisibility(View.INVISIBLE)
        greenBtn.setVisibility(View.INVISIBLE)
    }
    fun layout_write(view_odai: String){
        //announce
        textview_announce_write.setVisibility(View.VISIBLE)
        hand0.postDelayed(Runnable {
            textview_announce_write.setVisibility(View.INVISIBLE)
        },3000)
        textView_odai.text = "お題は「$view_odai」です"
        textView_odai.setVisibility(View.VISIBLE)

        //write button
        btn_board_reset.setVisibility(View.VISIBLE)
        room_btn_change_color.setVisibility(View.VISIBLE)
        whiteBtn.setVisibility(View.VISIBLE)
        blackBtn.setVisibility(View.INVISIBLE)
        redBtn.setVisibility(View.INVISIBLE)
        greenBtn.setVisibility(View.INVISIBLE)

        //watch button
        room_message_btn.setVisibility(View.INVISIBLE)
        room_message_text.setVisibility(View.INVISIBLE)
    }
    fun layout_watch(){
        //announce
        textview_announce_watch.setVisibility(View.VISIBLE)
        hand0.postDelayed(Runnable {
            textview_announce_watch.setVisibility(View.INVISIBLE)
        },3000)

        textView_odai.setVisibility(View.GONE)

        //write button
        btn_board_reset.setVisibility(View.INVISIBLE)
        room_btn_change_color.setVisibility(View.INVISIBLE)
        whiteBtn.setVisibility(View.INVISIBLE)
        blackBtn.setVisibility(View.INVISIBLE)
        redBtn.setVisibility(View.INVISIBLE)
        greenBtn.setVisibility(View.INVISIBLE)

        //watch button
        room_message_btn.setVisibility(View.VISIBLE)
        room_message_text.setVisibility(View.VISIBLE)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun surface_write_fun(customSurfaceView_write: CustomSurfaceView){

        /// CustomSurfaceViewのインスタンスを生成しonTouchリスナーをセット
        surfaceView_write.setOnTouchListener { v, event ->
            if(i%2 == 0){
                customSurfaceView_write.onTouch(event)
            }
            Log.d("event", "writing")
            customSurfaceView_write.onTouch_watch(event)
        }
        /// カラーチェンジボタンにリスナーをセット
        /// CustomSurfaceViewのchangeColorメソッドを呼び出す
        blackBtn.setOnClickListener {
            customSurfaceView_write.changeColor("black")
            close_color_btn()
        }
        redBtn.setOnClickListener {
            customSurfaceView_write.changeColor("red")
            close_color_btn()
        }
        greenBtn.setOnClickListener {
            customSurfaceView_write.changeColor("green")
            close_color_btn()
        }
        //消しゴム
        whiteBtn.setOnClickListener {
            customSurfaceView_write.changeColor("white")
        }
        room_btn_change_color.setOnClickListener {
            if(blackBtn.getVisibility() == View.INVISIBLE){
                blackBtn.setVisibility(View.VISIBLE)
                redBtn.setVisibility(View.VISIBLE)
                greenBtn.setVisibility(View.VISIBLE)
            }else{
                close_color_btn()
            }
        }
        /// リセットボタン
        btn_board_reset.setOnClickListener {
            customSurfaceView_write.reset()
        }

    }
    //firebaseに初期値を代入
    fun set_firebase_construct(){
        val pass_down = FirebaseDatabase.getInstance().getReference("Room/$room_id")
        pass_down.child("draw/draw_up/x").setValue("0")
        pass_down.child("draw/draw_up/y").setValue("0")
        pass_down.child("draw/draw_move/x").setValue("")
        pass_down.child("draw/draw_move/y").setValue("")
        pass_down.child("draw/draw_down/x").setValue("0")
        pass_down.child("draw/draw_down/y").setValue("0")
        pass_down.child("draw/btn/color").setValue("black")
        pass_down.child("draw/btn/reset").setValue("a")
        pass_down.child("game/answer").setValue("aaa")
        pass_down.child("Message/text").setValue("aaa")
        pass_down.child("game/from_user_id").setValue("bbb")
        pass_down.child("game/game_set").setValue("1")
    }

    //色変更ボタンを閉じる
    fun close_color_btn(){
        blackBtn.setVisibility(View.INVISIBLE)
        redBtn.setVisibility(View.INVISIBLE)
        greenBtn.setVisibility(View.INVISIBLE)
    }

    //-------------------------------------------
    //firbaseからデータの読み取りの処理
    //-------------------------------------------
    fun firebase_watch(customSurfaceView_read: CustomSurfaceView){
        Log.d("watch", "massage")

//        描画の停止
//        surfaceView_write.setOnTouchListener { v, event ->
//            customSurfaceView_read.onTouch_watch(event)
//        }

        val pass_down = FirebaseDatabase.getInstance().getReference("Room/$room_id/draw/draw_down")
        pass_down.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val draw_down = snapshot.getValue(Draw_data::class.java)

                val x_string: String = draw_down?.x.toString() ?: "-1"
                val y_string: String = draw_down?.y.toString() ?: "-1"

                //string からfloatに変換
                if (x_string != "" && y_string != "") {
                    val x: Float = x_string.toFloat()
                    val y: Float = y_string.toFloat()

                    if (i % 2 == 1) {
                        customSurfaceView_read.touchDown_watch(x, y)
                        Log.d("bbb", i.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //エラー処理
            }
        })

        val pass_move = FirebaseDatabase.getInstance().getReference("Room/$room_id/draw/draw_move")
        pass_move.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val draw_down = snapshot.getValue(Draw_data::class.java)
                val x_string: String = draw_down?.x.toString()
                val y_string: String = draw_down?.y.toString()

                if (x_string != "" && y_string != "") {
                    //string からfloatに変換
                    val x: Float = x_string.toFloat()
                    val y: Float = y_string.toFloat()
                    if (i % 2 == 1) {
                        customSurfaceView_read.touchMove_watch(x, y)
                    }
                }
                Log.d("firebase", "move")
            }

            override fun onCancelled(error: DatabaseError) {
                //エラー処理
            }
        })

        val pass_up = FirebaseDatabase.getInstance().getReference("Room/$room_id/draw/draw_up")
        pass_up.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val draw_down = snapshot.getValue(Draw_data::class.java)
                val x_string: String = draw_down?.x.toString() ?: "-1"
                val y_string: String = draw_down?.y.toString() ?: "-1"

                //string からfloatに変換
                val x: Float = x_string.toFloat()
                val y: Float = y_string.toFloat()

                if (i % 2 == 1) {
                    customSurfaceView_read.touchUp_watch(x, y)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                //エラー処理
            }
        })

        // 色を変えた場合の処理
        val color_ref = FirebaseDatabase.getInstance().getReference("Room/$room_id/draw/btn")
        color_ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val btn_ref = snapshot.getValue(Button_board::class.java)
                val selectedcolor = btn_ref?.color
                customSurfaceView_read.changeColor_watch(selectedcolor!!)
            }

            override fun onCancelled(error: DatabaseError) {
                //エラー処理
            }
        })

        // リセットボタンの処理
        val reset_ref = FirebaseDatabase.getInstance().getReference("Room/$room_id/draw/btn")
        reset_ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val btn_ref = snapshot.getValue(Button_board::class.java)
                if (btn_ref?.reset == "reset") {
                    customSurfaceView_read.reset_watch()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        //message情報の取得
        var aa = 0
        val ref_message = FirebaseDatabase.getInstance().getReference("Room/$room_id/Message/text")
        ref_message.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val answer_text = snapshot.getValue()
//                val answer_user_id = snapshot.child("from_user_id").getValue()
                if(aa>0){
                    set_answer(answer_text.toString())
                    Log.d("message","------")
                }
                aa++
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        //answerを取ってくる関数
        val ref_answer = FirebaseDatabase.getInstance().getReference("Room/$room_id/game")
        ref_answer.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val answer_text = snapshot.child("answer").getValue().toString()
                answer = answer_text
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }



    fun readQuestionData() {
        val `is` = resources.openRawResource(R.raw.test_01)
        val `is2` = resources.openRawResource(R.raw.test_01)

        val reader = BufferedReader(
            InputStreamReader(
                `is`,
                Charset.forName("UTF-8")
            )
        )

        val reader2 = BufferedReader(
            InputStreamReader(
                `is2`,
                Charset.forName("UTF-8")
            )
        )

        var line = ""
        try {
            var a =0
            while (reader2.readLine() != null ) {
                line = reader.readLine()

                val abc = line.split(",")


                val item = QuestionItem()
//                item.question_id(tokens[0])
//                item.setQuestion_item(tokens[1])
                questionItem+=abc[1]

                Log.d("read", "Just created: ${questionItem[a]}")
                a++
            }
        } catch (e: IOException) {
            Log.wtf("read", "Error reading data file on line $line", e)
            e.printStackTrace()
        }
        Log.d("read", "$questionItem")
    }
    fun RandomChoice(): String{
        val r = (0..9).shuffled().first()
        Log.d("read", "Just created: ${questionItem[r]}")
        return questionItem[r].toString()
    }

}
data class Button_board(
    var reset: String? = "a",
    var color: String? = "black",
)