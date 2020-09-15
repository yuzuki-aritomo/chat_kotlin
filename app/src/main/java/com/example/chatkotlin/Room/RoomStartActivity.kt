package com.example.chatkotlin.Room

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.chatkotlin.R
import kotlinx.android.synthetic.main.activity_room_choice.*
import kotlinx.android.synthetic.main.activity_room_game.*

class RoomStartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_start)

        // room_idを取得
        val room_id = intent.getStringExtra("room_id")
        textView.text = room_id

        //ゲームスタートボタン
        val game_start : Button = findViewById(R.id.start_from_start)
        game_start.setOnClickListener {
            val intent = Intent(this, RoomGameActivity::class.java)
            intent.putExtra("room_id", room_id)
            startActivity(intent)
            //room_id/IfGameOrNot を変更

        }
        //接続人数を表示(引数：room_id 戻り値：数字)

        //退出ボタン
        val btn_finish: Button = findViewById(R.id.exit_from_start)
        btn_finish.setOnClickListener {
            finish()
        }
    }
}