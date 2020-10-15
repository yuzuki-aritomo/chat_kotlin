package com.example.chatkotlin.Default

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chatkotlin.R
import com.example.chatkotlin.Room.RoomChoiceActivity
import kotlinx.android.synthetic.main.activity_startpage.*


class StartpageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startpage)

        start_start_button.setOnClickListener() {
            val intent = Intent(this, RoomChoiceActivity::class.java)
            startActivity(intent)
        }
    }
}