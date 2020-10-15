package com.example.chatkotlin.Default

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.chatkotlin.R
import com.example.chatkotlin.data.UserDB


class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val userdb = UserDB(applicationContext)
        userdb.dataeExist()
        var name = userdb.getname()
        var user_id = userdb.getuser_id()
        Log.d("database",name)
        Log.d("database",user_id)

//        userdb.delete()
        Log.d("database",name)
        Log.d("database",user_id)
    }
}