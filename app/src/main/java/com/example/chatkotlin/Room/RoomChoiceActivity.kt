package com.example.chatkotlin.Room

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.chatkotlin.Board.AnotherBoardActivity
import com.example.chatkotlin.R

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
            //firebaseにユーザーの追加

        }
        //Room1btn
        val room2: Button = findViewById(R.id.room_start_2)
        room2.setOnClickListener {
            val intent = Intent(this, RoomStartActivity::class.java)
            intent.putExtra("room_id", "room_2")
            startActivity(intent)
        }

    }
    //firebaseからユーザー数の確認する関数(引数：room_id  戻り値：bool)

    //room_id/IfGameOrNot を確認する関数(引数：room_id  戻り値：bool)

    //firebaseにユーザーの追加する関数(引数：room_id)

}