package com.example.colaboraboard.activities

import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.colaboraboard.R
import com.example.colaboraboard.databinding.ActivityMyProfileBinding
import com.example.colaboraboard.firebase.FirestoreClass
import com.example.colaboraboard.models.User

class MyProfileActivity : BaseActivity() {
    private lateinit var profileBinding: ActivityMyProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileBinding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(profileBinding.root)

        setUpActionBar()
        FirestoreClass().loadUserData(this)
    }

    private fun setUpActionBar(){
        setSupportActionBar(profileBinding.toolbarMyProfileActivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile_title)
        }

        profileBinding.toolbarMyProfileActivity.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    fun setUserDataInUI(user: User){
        Glide
            .with(this@MyProfileActivity)
            .load(user.image) // URL of the image
            .centerCrop() // Scale type of the image.
            .placeholder(R.drawable.ic_user_place_holder) // A default place holder
            .into(profileBinding.ivUserImage)

        profileBinding.etProfileName.setText(user.name)
        profileBinding.etProfileEmail.setText(user.email)
        if(user.mobile != 0L){
            profileBinding.etProfileMobile.setText(user.mobile.toString())
        }
    }
}