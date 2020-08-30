package com.example.chatkotlin

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //register
        start_btn_register.setOnClickListener{
            performRegister()
        }
        //login
        btn_login.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        //select image
        register_img.setOnClickListener {
            Log.d("Main","select photo")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }
    var selevtedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d("Main","photo was selected")

            selevtedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selevtedPhotoUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            register_img.setBackgroundDrawable(bitmapDrawable)
        }
    }
    //register
    private fun performRegister(){
        val email = email_register.text.toString()
        val password = password_register.text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,"Please enter text in email/pw", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("Main","btn pushed ${email}")
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener
                Log.d("Main","Successfully created user with uid:")
                uploadImageToFirebaseStorage()
            }
    }

    private fun uploadImageToFirebaseStorage(){
        if(selevtedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selevtedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("Register", "image upload")
                ref.downloadUrl.addOnSuccessListener {
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
    }
    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, username_register.text.toString(),profileImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("Main", "user to firebase database")
                val intent = Intent(this, LatestMassageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
    }
}

class User(val uid: String, val username: String, val profileImageUrl: String)