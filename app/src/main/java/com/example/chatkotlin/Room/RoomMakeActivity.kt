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
        val user_image = userdb.getuser_image_url()
        val userimage : Bitmap = userdb.getUserImage()

        room_male_image.setImageBitmap(userimage)

        //room作成時
        room_make_make.setOnClickListener {
            val room_name :String = room_make_name.text.toString()
            if(room_name==""){
                return@setOnClickListener
            }
            val room_id = UUID.randomUUID().toString()
            val url = FirebaseDatabase.getInstance().getReference("RoomMake/$room_id")

            val DataUpdate: MutableMap<String, Any> = HashMap()
            DataUpdate["room_name"] = room_name
            DataUpdate["room_id"] = room_id
            DataUpdate["user_name"] = name
            DataUpdate["user_id"] = user_id
            DataUpdate["user_image"] = user_image
            url.updateChildren(DataUpdate)

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