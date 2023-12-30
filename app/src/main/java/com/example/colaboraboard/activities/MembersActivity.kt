package com.example.colaboraboard.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.colaboraboard.databinding.ActivityMembersBinding

class MembersActivity : AppCompatActivity() {
    private lateinit var membersBinding: ActivityMembersBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        membersBinding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(membersBinding.root)
    }
}