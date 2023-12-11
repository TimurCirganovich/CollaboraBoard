package com.example.colaboraboard.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.colaboraboard.R
import com.example.colaboraboard.databinding.ActivityMyProfileBinding
import com.example.colaboraboard.firebase.FirestoreClass
import com.example.colaboraboard.models.User
import java.io.IOException

class MyProfileActivity : BaseActivity() {
    private lateinit var profileBinding: ActivityMyProfileBinding

    companion object{
        private const val READ_STORAGE_PERMISSION_CODE = 1
        //private const val PICK_IMAGE_REQUEST_CODE = 2

    }

    private var mSelectedImageFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileBinding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(profileBinding.root)

        setUpActionBar()
        FirestoreClass().loadUserData(this)

        profileBinding.ivProfileUserImage.setOnClickListener {
            if(ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED){
                showImageChooser()
            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showImageChooser()
            }
        }else{
            Toast.makeText(
                this,
                "Permission denied.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showImageChooser(){
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(galleryIntent)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            val data: Intent? = result.data
            mSelectedImageFileUri = data?.data

            try {
                Glide
                    .with(this@MyProfileActivity)
                    .load(mSelectedImageFileUri) // URL of the image
                    .centerCrop() // Scale type of the image.
                    .placeholder(R.drawable.ic_user_place_holder) // A default place holder
                    .into(profileBinding.ivProfileUserImage)
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
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
            .into(profileBinding.ivProfileUserImage)

        profileBinding.etProfileName.setText(user.name)
        profileBinding.etProfileEmail.setText(user.email)
        if(user.mobile != 0L){
            profileBinding.etProfileMobile.setText(user.mobile.toString())
        }
    }
}