package com.example.colaboraboard

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)


        //Hides the status bar on different android versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        //Opens the SignIn screen
        val btnSignIn = findViewById<AppCompatButton>(R.id.btn_sign_in_intro)
        btnSignIn.setOnClickListener{
            startActivity(Intent(this, SignInActivity::class.java))
        }

        //Opens the SignUp screen
        val btnSignUp = findViewById<AppCompatButton>(R.id.btn_sign_up_intro)
        btnSignUp.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}