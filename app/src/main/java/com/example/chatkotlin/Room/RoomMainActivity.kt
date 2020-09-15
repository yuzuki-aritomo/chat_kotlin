package com.example.chatkotlin.Room

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.chatkotlin.Board.AnotherBoardActivity
import com.example.chatkotlin.Board.BoardActivity
import com.example.chatkotlin.R
import kotlinx.android.synthetic.main.activity_room_main.*

class RoomMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_main)

        val room_id = intent.getStringExtra("room_id")//room_id: room_1



        val btn_write: Button = findViewById(R.id.btn_main_to_write)
        btn_write.setOnClickListener {
            val intent_write = Intent(this, BoardActivity::class.java)
            intent_write.putExtra("room_id", room_id)//room_id: room_1
            startActivity(intent_write)
        }

        val btn_watch: Button = findViewById(R.id.btn_main_to_watch)
        btn_watch.setOnClickListener {
            val intent_watch = Intent(this, AnotherBoardActivity::class.java)
            intent_watch.putExtra("room_id", room_id)//room_id: room_1
            startActivity(intent_watch)
        }




    }



}