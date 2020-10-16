package com.example.chatkotlin.Default

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.chatkotlin.R
import com.example.chatkotlin.data.UserDB
import kotlinx.android.synthetic.main.activity_home.*


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


        homeUserImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }


    }

    //画像の保存
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d("home", "selected image")
            val uri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,uri)

            val bitmapDrawable = BitmapDrawable(bitmap)
//            homeUserImage.setBackgroundDrawable(bitmapDrawable)
            homeUserImage.setImageBitmap(bitmap)
        }
    }
}