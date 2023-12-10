package com.example.colaboraboard.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.example.colaboraboard.R
import com.example.colaboraboard.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_sign_in)
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

        setupActionBar()
    }

    //Set the button to go bac to the Intro screen
    private fun setupActionBar(){
        //val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_sign_in_activity)
        //setSupportActionBar(toolbar)
        setSupportActionBar(binding.toolbarSignInActivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        //toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.toolbarSignInActivity.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
}