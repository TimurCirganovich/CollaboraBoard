package com.example.colaboraboard.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatButton
import com.example.colaboraboard.R
import com.example.colaboraboard.databinding.ActivityIntroBinding
import com.example.colaboraboard.databinding.DialogProgressBinding

class IntroActivity : BaseActivity() {
    private lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_intro)
        setContentView(binding.root)


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
        //val btnSignIn = findViewById<AppCompatButton>(R.id.btn_sign_in_intro)
        binding.btnSignInIntro.setOnClickListener{
            startActivity(Intent(this, SignInActivity::class.java))
        }

        //Opens the SignUp screen
        //val btnSignUp = findViewById<AppCompatButton>(R.id.btn_sign_up_intro)
        binding.btnSignUpIntro.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}