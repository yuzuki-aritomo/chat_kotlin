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
import java.io.ByteArrayOutputStream


class HomeActivity : AppCompatActivity() {
    var userBitmap :Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val userdb = UserDB(applicationContext)
        val name :String = userdb.getname()
        val user_id = userdb.getuser_id()
        val userimage :Bitmap = userdb.getUserImage()

        homeUserImage.setImageBitmap(userimage)
        home_user_name.setText(name)

        //---------ボタンの処理-------------
        homeUserImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
        home_save.setOnClickListener{
            saveUserName()
            saveUserImage()
        }
    }

    //画像の保存
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d("home", "selected image")
            val uri = data.data
            userBitmap = MediaStore.Images.Media.getBitmap(contentResolver,uri)
            homeUserImage.setImageBitmap(userBitmap)
        }
    }

    fun saveUserName(){
        val newname :String = home_user_name.text.toString()
        if( newname != ""){
            val userdb = UserDB(applicationContext)
            userdb.updateUserName(newname)
        }
    }
    fun saveUserImage(){
        //user imageに変更があった場合のみ
        if (userBitmap != null){
            val userdb = UserDB(applicationContext)
            userdb.updateUserImage(userBitmap!!)
        }
    }

}