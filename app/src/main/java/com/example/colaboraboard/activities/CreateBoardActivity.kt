package com.example.colaboraboard.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.colaboraboard.R
import com.example.colaboraboard.databinding.ActivityCreateBoardBinding

class CreateBoardActivity : AppCompatActivity() {
    private lateinit var createBoardBinding: ActivityCreateBoardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createBoardBinding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(createBoardBinding.root)

        setUpActionBar()
    }

    private fun setUpActionBar(){
        setSupportActionBar(createBoardBinding.toolbarCreateBoardActivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.create_board_title)
        }

        createBoardBinding.toolbarCreateBoardActivity.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
}