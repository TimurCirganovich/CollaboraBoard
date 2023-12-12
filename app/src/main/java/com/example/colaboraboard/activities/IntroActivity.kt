package com.example.colaboraboard.activities

import android.content.Intent
import android.os.Bundle
import com.example.colaboraboard.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity() {
    private lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_intro)
        setContentView(binding.root)


        makeFullScreen()

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