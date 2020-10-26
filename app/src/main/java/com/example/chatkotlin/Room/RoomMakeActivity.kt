package com.example.chatkotlin.Room

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatkotlin.R
import com.example.chatkotlin.data.UserDB
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_room_make.*
import java.util.*

class RoomMakeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_make)

        val userdb = UserDB(applicationContext)
        val name :String = userdb.getname()
        val user_id = userdb.getuser_id()
        val userimage : Bitmap = userdb.getUserImage()

        room_male_image.setImageBitmap(userimage)

        //room作成時
        room_make_make.setOnClickListener {
            val room_name :String = room_make_name.text.toString()
            val room_id = UUID.randomUUID().toString()
            val url = FirebaseDatabase.getInstance().getReference("RoomMake")
            url.setValue(room_id)
            url.child("$room_id/room_name").setValue(room_name)
            url.child("$room_id/room_id").setValue(room_id)
            url.child("$room_id/user_name").setValue(name)
            url.child("$room_id/user_id").setValue(user_id)

            val intent = Intent(this, RoomWaitActivity::class.java)
            intent.putExtra("room_id", room_id)
            intent.putExtra("user", "master")
            intent.putExtra("room_name", room_name)
            startActivity(intent)
        }
        //戻る
        room_make_return.setOnClickListener {
            val intent = Intent(this, RoomChoiceActivity::class.java)
            startActivity(intent)
        }

    }
}