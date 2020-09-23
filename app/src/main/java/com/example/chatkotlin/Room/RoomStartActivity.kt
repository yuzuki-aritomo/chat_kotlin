package com.example.chatkotlin.Room

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.chatkotlin.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_room_choice.textView
import kotlinx.android.synthetic.main.activity_room_start.*

class RoomStartActivity : AppCompatActivity() {

    var user_count = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_start)

        // 初期のroom情報
        val room_id = intent.getStringExtra("room_id")
        var user_id = "user_id"


        textView.text = room_id

        //user_id の登録
        val ref = FirebaseDatabase.getInstance().getReference("Room/$room_id/user").push()
        ref.child("user_id").setValue(ref.key)
        Log.d("key",ref.key)
        ref.child("draw_or_watch").setValue("watch")
        ref.child("score").setValue("0")
        user_id = ref.key.toString()

        //firebase情報
        val ref_user = FirebaseDatabase.getInstance().getReference("Room/$room_id/user")
        //接続人数を表示(引数：room_id 戻り値：数字)
        ref_user.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user_count = snapshot.childrenCount.toString()
                val ready_count = snapshot.child("ready").childrenCount.toString()
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
                user_count = snapshot.childrenCount.toString()
                val ready_count = snapshot.childrenCount.toString()
                if(user_count.toInt() == ready_count.toInt() && user_count.toInt()>1){
                    startGame(room_id,user_id,user_count)
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
            finish()
        }

        //準備完了ボタン
        val game_start : Button = findViewById(R.id.start_from_start)
        val ref_ready = FirebaseDatabase.getInstance().getReference("Room/$room_id/ready")
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
    fun startGame(room_id: String, user_id: String, user_count: String){
        val intent = Intent(this, RoomMainActivity::class.java)
        intent.putExtra("room_id", room_id)//room_id: room_1
        intent.putExtra("user_id", user_id)
        intent.putExtra("user_count", user_count)
        startActivity(intent)
    }
}