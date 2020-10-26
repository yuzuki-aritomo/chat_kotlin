package com.example.chatkotlin.Room

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.chatkotlin.Board.CustomSurfaceView
import com.example.chatkotlin.Board.Draw_data
import com.example.chatkotlin.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_board.*
import kotlinx.android.synthetic.main.activity_room_start.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset


class RoomMainActivity : AppCompatActivity() {
    var i: Int = 0
    var room_id: String= "room_extra"
    var user_id: String= "room_extra"
    var user_count: Int = 0
    var user_score: Int = 0
    var game_set: Int = 1
    var game_set_max: Int = 0
    var answer = "aaa" //お題の答え
    var user_list = arrayOf<String>("aa", "aa", "aa", "aa", "aa")
    var user_name_list = arrayOf<String>("ゲスト", "ゲスト", "ゲスト", "ゲスト", "ゲスト")
    val hand0= Handler()
    var a = "おだい"

    private var questionItem: List<*> = ArrayList<Any?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //room_idの設定
        room_id = intent.getStringExtra("room_id").toString()//room_id: room_1
        user_id = intent.getStringExtra("user_id").toString() //room_id: room_1
        user_count = intent.getStringExtra("user_count").toInt() //room_id: room_1

        //お題のcsvファイルの読み込み
        readQuestionData()

        //gameのセット数
        when(user_count){
            2 -> game_set_max = 6
            3 -> game_set_max = 3
            4 -> game_set_max = 4
            5 -> game_set_max = 5
        }
        //layoutの初期化
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
        var abc = 0
        //user情報を排列に代入
        val ref = FirebaseDatabase.getInstance().getReference("Room/$room_id/user")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val layout_name = findViewById<LinearLayout>(R.id.linearLayout_name)
                val layout_image = findViewById<LinearLayout>(R.id.linearLayout_image)
                p0.children.forEach {
                    if (abc < user_count) {
                        Log.d("NewMassage", it.toString())
                        val user_id_num = it.child("user_id").getValue()
                        val user_name = it.child("user_name").getValue()
                        user_name_list[abc] = user_name.toString()
                        user_list[abc] = user_id_num.toString()

                        val textView_name: TextView = layout_name.getChildAt(abc) as TextView
                        textView_name.text = user_name.toString()

                        val image_view: ImageView = layout_image.getChildAt(abc) as ImageView
                        when(abc+1){
                            1 -> image_view.setImageResource(R.drawable.user_image_1)
                            2 -> image_view.setImageResource(R.drawable.user_image_2)
                            3 -> image_view.setImageResource(R.drawable.user_image_3)
                            4 -> image_view.setImageResource(R.drawable.user_image_4)
                            5 -> image_view.setImageResource(R.drawable.user_image_5)
                        }
                        image_view.setVisibility(View.VISIBLE)
                        abc++
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
        game_set = 1

        //game_setが終わったかどうかを取ってくる(firebaseに変更があったら) 1回目以降
        var game_set_num = 0
        val ref_set = FirebaseDatabase.getInstance().getReference("Room/$room_id/game/game_set")
        ref_set.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == null) {
                    return
                }
                //ゲームスタートと答えの表示
                if (game_set_num == 0) {
                    //スタートの表示
                    textView_answer_result.text = "ゲームスタート"
                    textView_answer_result.setVisibility(View.VISIBLE)
                } else {
                    //答えの表示
                    textView_answer_result.text = "答え\n「$answer」"
                    textView_answer_result.setVisibility(View.VISIBLE)
                }
                game_set_num++
                hand0.postDelayed(Runnable {
                    textView_answer_result.setVisibility(View.GONE)
                    customSurfaceView.reset()

                    //gameが終了するかどうか
                    if (game_set - 1 == game_set_max) {
                        //ゲームの終了画面に移行
                        Log.d("test", "end game")
                        val intent = Intent(applicationContext, RoomResultActivity::class.java)
                        intent.putExtra("room_id", room_id)//room_id: room_1
                        intent.putExtra("user_id", user_id)
                        startActivity(intent)
                    }
                    //game_set % user_count の値の人が書く人
                    if (user_id == user_list[game_set % user_count]) {
                        //お題の選定とfirebaseに保存
//                        val a = RandomChoice()
                        when(game_set_num){
                            1-> a = "こおり"
                            2-> a = "すまほ"
                            3-> a = "さかな"
                        }
                        FirebaseDatabase.getInstance().getReference("Room/$room_id/game/answer")
                            .setValue(
                                a
                            )
                        //writeの関数
                        i = 2
                        layout_write(a)
                        surface_write_fun(customSurfaceView)
                    } else {
                        //watchの関数
                        //watchのレイアウト
                        i = 1
                        layout_watch()
                        surface_watch_fun(customSurfaceView)
                    }
                    //game_setが終われば
                    game_set++
                }, 3000)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        //それぞれのviewでの切り替え
        btn.setOnClickListener{
            Log.d("user_", user_list[game_set % user_count])
            val ref__ = FirebaseDatabase.getInstance().getReference("Room/$room_id/game")
            ref__.child("game_set").setValue(game_set)
        }
    }
    //戻るボタンの無効化
    override fun onBackPressed() {

    }

    fun surface_watch_fun(customSurfaceView: CustomSurfaceView){

        //messageの送信
        room_message_btn.setOnClickListener {
            if(room_message_text.text.toString() != "null"){
                val text: String = room_message_text.text.toString()
                if(text == answer){
                    //firebaseに保存
                    user_score += 10
                    val refe = FirebaseDatabase.getInstance().getReference("Room/$room_id")
                    refe.child("game/game_set").setValue(game_set)
                    refe.child("game/answer").setValue(text)
                    refe.child("user/$user_id/score").setValue(user_score)
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
                }, 3000)
            }
            1 -> {
                room_answer_text_2.text = answer_text
                room_answer_text_2.setVisibility(View.VISIBLE)
                hand0.postDelayed(Runnable {
                    room_answer_text_2.setVisibility(View.INVISIBLE)
                }, 3000)
            }
            2 -> {
                room_answer_text_3.text = answer_text
                room_answer_text_3.setVisibility(View.VISIBLE)
                hand0.postDelayed(Runnable {
                    room_answer_text_3.setVisibility(View.INVISIBLE)
                }, 3000)
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
        textView_answer_result.setVisibility(View.GONE)
        room_user_image_1.setVisibility(View.INVISIBLE)
        room_user_image_2.setVisibility(View.INVISIBLE)
        room_user_image_3.setVisibility(View.INVISIBLE)
        room_user_image_4.setVisibility(View.INVISIBLE)
        room_user_image_5.setVisibility(View.INVISIBLE)
        btn_to_another_board_activity.setVisibility(View.GONE)

        //write button
        btn_board_reset.setVisibility(View.INVISIBLE)
        room_btn_change_color.setVisibility(View.INVISIBLE)
        whiteBtn.setVisibility(View.INVISIBLE)
        blackBtn.setVisibility(View.INVISIBLE)
        redBtn.setVisibility(View.INVISIBLE)
        greenBtn.setVisibility(View.INVISIBLE)
    }
    fun layout_write(view_odai: String){
        //announce
        textview_announce_write.setVisibility(View.VISIBLE)
        hand0.postDelayed(Runnable {
            textview_announce_write.setVisibility(View.INVISIBLE)
        }, 3000)
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
        }, 3000)

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
//        pass_down.child("draw/draw_up/x").setValue(0)
//        pass_down.child("draw/draw_up/y").setValue(0)
        pass_down.child("draw/draw_move/x").setValue(0F)
        pass_down.child("draw/draw_move/y").setValue(0F)
//        pass_down.child("draw/draw_down/x").setValue(0)
//        pass_down.child("draw/draw_down/y").setValue(0)
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
                if (snapshot.value == null) {
                    return
                }
                val draw_down = snapshot.getValue(Draw_data::class.java)

                val x: Float = draw_down?.x!!.toFloat()
                val y: Float = draw_down?.y!!.toFloat()

//                val x: Float = x_string.toFloat()
//                val y: Float = y_string.toFloat()

                if (i % 2 == 1) {
                    customSurfaceView_read.touchDown_watch(x, y)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //エラー処理
            }
        })

        val pass_move = FirebaseDatabase.getInstance().getReference("Room/$room_id/draw/draw_move")
        pass_move.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == null) {
                    return
                }
                val draw_down = snapshot.getValue(Draw_data::class.java)
                val x_string: Float = draw_down?.x!!.toFloat()
                val y_string: Float = draw_down?.y!!.toFloat()

                if (x_string != 0F && y_string != 0F) {
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
                if (snapshot.value == null) {
                    return
                }
                val draw_down = snapshot.getValue(Draw_data::class.java)
//                val x_string: String = draw_down?.x.toString() ?: "-1"
//                val y_string: String = draw_down?.y.toString() ?: "-1"

                //string からfloatに変換
//                val x: Float = x_string.toFloat()
//                val y: Float = y_string.toFloat()
                val x: Float = draw_down?.x!!.toFloat()
                val y: Float = draw_down?.y!!.toFloat()

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
                if (snapshot.value == null) {
                    return
                }
                val btn_ref = snapshot.getValue(Button_board::class.java)
                if (btn_ref == null) {
                    return
                }
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
                if (snapshot.value == null) {
                    return
                }
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
                if (snapshot.value == null) {
                    return
                }
                val answer_text = snapshot.getValue()
//                val answer_user_id = snapshot.child("from_user_id").getValue()
                if (aa > 0) {
                    set_answer(answer_text.toString())
                    Log.d("message", "------")
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
                if (snapshot.value == null) {
                    return
                }
                val answer_text = snapshot.child("answer").getValue().toString()
                answer = answer_text
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    //お題読み込み（CSVファイル読み込み）
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
            var a = 0
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
    //お題ランダム生成
    fun RandomChoice(): String{
        val data_counter = questionItem.size -1
        val r = (0..data_counter).random()
//        val r1 = (0..data_counter).shuffled().second()

        Log.d("read", "Just created: ${questionItem[r]}")


        Log.d("read", "$data_counter")
        return questionItem[r].toString()

    }

}
data class Button_board(
    var reset: String? = "a",
    var color: String? = "black",
)