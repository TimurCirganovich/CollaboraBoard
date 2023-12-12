package com.example.colaboraboard.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.colaboraboard.databinding.ActivitySplashBinding
import com.example.colaboraboard.firebase.FirestoreClass

class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_splash)
        setContentView(binding.root)

        makeFullScreen()

        //Applying custom font to title on the splashscreen
        val typeface: Typeface = Typeface.createFromAsset(assets, "carbon_bl.otf")

        //val title = findViewById<TextView>(R.id.tvAppName)

        //title.typeface = typeface
        binding.tvAppName.typeface = typeface

        //Move from SplashScreen to Intro screen
        Handler(Looper.getMainLooper()).postDelayed({
            val currentUserID = FirestoreClass().getCurrentUserId()
            if(currentUserID.isNotEmpty()){
                startActivity(Intent(this, MainActivity::class.java))
            }else{
                startActivity(Intent(this, IntroActivity::class.java))
            }

            finish()
        }, 2500)
    }
}