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
    val hand0= Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bitmap :Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ranking1)
        val userdb = UserDB(applicationContext)
//        userdb.insertData("1", "yuzuki", "abcd", bitmap)
        hand0.postDelayed(Runnable {
            val name = userdb.selectData()
            Log.d("database",name)
        }, 3000)
    }
}