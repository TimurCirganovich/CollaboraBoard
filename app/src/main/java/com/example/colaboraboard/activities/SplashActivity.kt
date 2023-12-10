package com.example.colaboraboard.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import com.example.colaboraboard.R
import com.example.colaboraboard.databinding.ActivityIntroBinding
import com.example.colaboraboard.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_splash)
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

        //Applying custom font to title on the splashscreen
        val typeface: Typeface = Typeface.createFromAsset(assets, "carbon_bl.otf")

        //val title = findViewById<TextView>(R.id.tvAppName)

        //title.typeface = typeface
        binding.tvAppName.typeface = typeface

        //Move from SplashScreen to Intro screen
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, IntroActivity::class.java))
            finish()
        }, 2500)
    }
}