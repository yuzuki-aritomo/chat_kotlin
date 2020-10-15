package com.example.chatkotlin.Room

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.chatkotlin.R
import com.example.chatkotlin.data.UserDB
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_room_choice.textView
import kotlinx.android.synthetic.main.activity_room_start.*

class RoomStartActivity : AppCompatActivity() {

    var user_count = "0"
    var user_id: String = ""
    var room_id = "room_1"
    var user_image = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_start)

        // 初期のroom情報
        room_id = intent.getStringExtra("room_id")

        val userdb = UserDB(applicationContext)
        user_id = userdb.getuser_id()

        //user_id の登録
        val ref = FirebaseDatabase.getInstance().getReference("Room/$room_id/user/$user_id")
        Log.d("vvv",user_id)
        ref.child("user_id").setValue(user_id)
        ref.child("draw_or_watch").setValue("watch")
        ref.child("score").setValue(0)
        ref.child("user_name").setValue("ゲスト")
        ref.child("user_image").setValue(0)

        //userの画像の選定と表示
        val ref_img = FirebaseDatabase.getInstance().getReference("Room/$room_id/user")
        ref_img.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user_image = snapshot.childrenCount.toInt()
                when(user_image){
                    1 -> room_start_image.setImageResource(R.drawable.user_image_1)
                    2 -> room_start_image.setImageResource(R.drawable.user_image_2)
                    3 -> room_start_image.setImageResource(R.drawable.user_image_3)
                    4 -> room_start_image.setImageResource(R.drawable.user_image_4)
                    5 -> room_start_image.setImageResource(R.drawable.user_image_5)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                //エラー処理
            }
        })

        //firebase情報
        val ref_user = FirebaseDatabase.getInstance().getReference("Room/$room_id/user")
        val ref_ready = FirebaseDatabase.getInstance().getReference("Room/$room_id/ready")
        //接続人数を表示(引数：room_id 戻り値：数字)
        ref_user.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user_count = snapshot.childrenCount.toString()
                text_user_count.text = user_count
            }
            override fun onCancelled(error: DatabaseError) {
                //エラー処理
            }
        })

        //準備完了の読み取り
        val ref_ready_count = FirebaseDatabase.getInstance().getReference("Room/$room_id/ready")
        ref_ready_count.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ready_count = snapshot.childrenCount.toInt()
                text_ready_count.text = ready_count.toString()
                if(user_count.toInt() == snapshot.childrenCount.toInt() ){
                    Log.d("start",user_count)
                    Log.d("start",ready_count.toString())
                    if(user_count.toInt()>1){
                        Log.d("vvv",user_id)
                        startGame(room_id.toString(),user_count)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                //エラー処理
            }
        })

        //退出ボタン
        val btn_finish: Button = findViewById(R.id.exit_from_start)
        btn_finish.setOnClickListener {
            //user情報の削除
            ref_user.child(user_id).removeValue()
            ref_ready.child(user_id).removeValue()
            finish()
        }

        //準備完了ボタン
        val game_start : Button = findViewById(R.id.start_from_start)
//        val ref_ready = FirebaseDatabase.getInstance().getReference("Room/$room_id/ready")
        var i = 0
        game_start.setOnClickListener {
            if(i%2==0){
                ref_ready.child(user_id).setValue("ready")
                game_start.text = "OK"
                i++
            }else{
                ref_ready.child(user_id).removeValue()
                game_start.text = "準備完了"
                i++
            }
        }
    }
    //戻るボタンでユーザーの情報を削除
    override fun onBackPressed() {
        //user情報の削除
        val ref_delete = FirebaseDatabase.getInstance().getReference("Room/$room_id")
        ref_delete.child("user/$user_id").removeValue()
        ref_delete.child("ready/$user_id").removeValue()
        finish()
    }
    //ゲームスタート
    fun startGame(room_id: String,user_count: String){
        Log.d("vvv",user_id)
        val user_name = editText_user_name.text.toString()
        val ref = FirebaseDatabase.getInstance().getReference("Room/$room_id/user/$user_id")
        ref.child("user_name").setValue(user_name)
        ref.child("user_image").setValue(user_image)
        Log.d("vvv",user_id)

        val intent = Intent(this, RoomMainActivity::class.java)
        intent.putExtra("room_id", room_id)//room_id: room_1
        intent.putExtra("user_id", user_id)
        intent.putExtra("user_name", user_name)
        intent.putExtra("user_count", user_count)
        startActivity(intent)
    }
}