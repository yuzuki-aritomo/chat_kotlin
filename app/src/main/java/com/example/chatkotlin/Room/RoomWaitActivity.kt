package com.example.chatkotlin.Room

import android.content.Intent
import android.graphics.Bitmap
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
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_room_start.*
import kotlinx.android.synthetic.main.activity_room_start.text_ready_count
import kotlinx.android.synthetic.main.activity_room_start.text_user_count
import kotlinx.android.synthetic.main.activity_room_wait.*
import java.io.ByteArrayOutputStream

class RoomWaitActivity : AppCompatActivity() {

    var user_count = "0"
    var user_id: String = ""
    var room_id = "null"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_wait)

        // 初期のroom情報
        room_id = intent.getStringExtra("room_id")
        val user_type  = intent.getStringExtra("user")
        val room_name = intent.getStringExtra("room_name")

        //dbの情報
        val userdb = UserDB(applicationContext)
        user_id = userdb.getuser_id()
        val user_name = userdb.getname()
        val user_image = userdb.getUserImage()

        //レイアウトの情報
        wait_room_name.setText(room_name)
        room_wait_image.setImageBitmap(user_image)
        wait_user_name.setText(user_name)

        //user_imageをstorageに保存
        val storage_ref = FirebaseStorage.getInstance().getReference("$room_id/$user_id")
        val baos = ByteArrayOutputStream()
        user_image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        storage_ref.putBytes(data)
            .addOnSuccessListener {
                val image_url = storage_ref.downloadUrl
                FirebaseDatabase.getInstance().getReference("Room/$room_id/user/$user_id/user_image").setValue(image_url)
            }

        //user_id の登録
        val ref = FirebaseDatabase.getInstance().getReference("Room/$room_id/user/$user_id")
        ref.child("user_id").setValue(user_id)
        ref.child("draw_or_watch").setValue("watch")
        ref.child("score").setValue(0)
        ref.child("user_name").setValue(user_name)

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
        Log.d("vvv",user_id)

        val intent = Intent(this, RoomMainActivity::class.java)
        intent.putExtra("room_id", room_id)//room_id: room_1
        intent.putExtra("user_id", user_id)
        intent.putExtra("user_name", user_name)
        intent.putExtra("user_count", user_count)
        startActivity(intent)
    }
}