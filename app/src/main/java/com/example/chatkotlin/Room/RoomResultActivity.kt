package com.example.chatkotlin.Room

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import com.example.chatkotlin.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RoomResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_result)
        //room_idの設定
        val room_id = intent.getStringExtra("room_id").toString()//room_id: room_1
        val user_id = intent.getStringExtra("user_id").toString() //room_id: room_1

        val ref = FirebaseDatabase.getInstance().getReference("Room/$room_id/user").orderByChild("score")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                Log.d("finish",p0.toString())
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
        val ref_finish = FirebaseDatabase.getInstance().getReference("Room/$room_id/finish")
        ref_finish.setValue(user_id)

    }
}