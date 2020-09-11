package com.example.chatkotlin.User

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatkotlin.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.btn_login

class LoginActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_login.setOnClickListener {
            val email = login_email.text.toString()
            val password = login_password.text.toString()

            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this,"Please enter text in email/pw", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if(!it.isSuccessful) return@addOnCompleteListener
                    Toast.makeText(this,"Login!", Toast.LENGTH_SHORT).show()
                    Log.d("Main","Successfully created user with uid:")
                }
                .addOnFailureListener {
                    Toast.makeText(this,"Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                    Log.d("Main","${it.message}",)
                }
        }

    }
}