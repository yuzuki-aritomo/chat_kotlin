package com.example.chatkotlin

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d("Main","photo was selected")
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
            }
    }
}