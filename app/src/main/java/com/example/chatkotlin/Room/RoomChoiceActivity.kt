package com.example.chatkotlin.Room

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.chatkotlin.Default.StartpageActivity
import com.example.chatkotlin.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_room_start.*

class RoomChoiceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_choice)

        //Room1btn
        val room1: Button = findViewById(R.id.room_start_1)
        room1.setOnClickListener {
            //firebaseからユーザー数の確認

            //room_id/IfGameOrNot を確認

            val intent = Intent(this, RoomStartActivity::class.java)
            intent.putExtra("room_id", "room_1")
            startActivity(intent)

        }
        //Room2btn
        val room2: Button = findViewById(R.id.room_start_2)
        room2.setOnClickListener {
            val intent = Intent(this, RoomStartActivity::class.java)
            intent.putExtra("room_id", "room_2")
            startActivity(intent)
        }

        //Room3btn
        val room3: Button = findViewById(R.id.room_start_3)
        room3.setOnClickListener {
            val intent = Intent(this, RoomStartActivity::class.java)
            intent.putExtra("room_id", "room_3")
            startActivity(intent)
        }

        val returnstart: Button = findViewById(R.id.return_startpage_button)
        returnstart.setOnClickListener {
            val intent = Intent(this, StartpageActivity::class.java)
            startActivity(intent)
        }

    }
    //firebaseからユーザー数の確認する関数(引数：room_id  戻り値：bool)
    fun user_count(room_id: String){
        val ref_user = FirebaseDatabase.getInstance().getReference("Room/$room_id/user")
        var user_count = "0"
        ref_user.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user_count = snapshot.childrenCount.toString()
            }
            override fun onCancelled(error: DatabaseError) {
                //エラー処理
            }
        })
    }

    //room_id/IfGameOrNot を確認する関数(引数：room_id  戻り値：bool)

}