package com.example.chatkotlin.Default

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.chatkotlin.R
import com.example.chatkotlin.Room.RoomChoiceActivity
import com.example.chatkotlin.Room.RoomSelectActivity
import com.example.chatkotlin.data.UserDB
import kotlinx.android.synthetic.main.activity_startpage.*


class StartpageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startpage)

        //databaseの初期化
        val userdb = UserDB(applicationContext)
        userdb.dataeExist()
        val user_name = userdb.getname()
        val user_image = userdb.getUserImage()

        val url = userdb.getuser_image_url()

        Log.d("nnn",url)

        //レイアウトの初期値
        startpage_user_image.setImageBitmap(user_image)
        startpage_user_name.setText(user_name)

        //---------ボタンの処理-------------
        startpage_user_image.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        startpage_setting.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        start_start_button.setOnClickListener {
//            val intent = Intent(this, RoomChoiceActivity::class.java)
//            startActivity(intent)
        }
        startpage_room.setOnClickListener {
//            val intent = Intent(this, RoomChoiceActivity::class.java)
//            startActivity(intent)
            val intent = Intent(this, RoomSelectActivity::class.java)
            startActivity(intent)
        }
    }
}