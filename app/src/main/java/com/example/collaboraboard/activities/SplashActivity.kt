package com.example.collaboraboard.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.collaboraboard.databinding.ActivitySplashBinding
import com.example.collaboraboard.firebase.FirestoreClass

class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashBinding     //A global variable for UI element access
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding init for UI elements access
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        makeFullScreen()

        //Applying custom font from assets to TextView on the splashscreen
        val typeface: Typeface = Typeface.createFromAsset(assets, "carbon_bl.otf")
        binding.tvAppName.typeface = typeface

        //Move from SplashScreen to Intro screen or Main screen (if user was signed in before) after 2.5 seconds delay
        Handler(Looper.getMainLooper()).postDelayed({
            val currentUserID = FirestoreClass().getCurrentUserId()
            if(currentUserID.isNotEmpty()){
                startActivity(Intent(this, MainActivity::class.java))
            }else{
                startActivity(Intent(this, IntroActivity::class.java))
            }

            finish()    //finish SplashScreen
        }, 2500)
    }
}