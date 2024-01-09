package com.example.collaboraboard.activities

import android.app.Dialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.collaboraboard.R
import com.example.collaboraboard.databinding.DialogProgressBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

//BaseActivity contain functions which are used in multiple activities

open class BaseActivity : AppCompatActivity() {

    private lateinit var binding: DialogProgressBinding //A global variable for dialog UI element access

    //This is a progress dialog instance which we will initialize later
    private lateinit var mProgressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    //Function to make a fullscreen window
    fun makeFullScreen(){
        //Hides the status bar on different android versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    //Function to show progress dialog to user
    fun showProgressDialog(text: String){
        mProgressDialog = Dialog(this)

        /*
        Set the screen content from a layout resource
        The resource will be inflated, adding all top-level views to the screen
        */

        binding = DialogProgressBinding.inflate(layoutInflater)
        mProgressDialog.setContentView(binding.root)
        //Set dialog's text
        binding.tvProgressText.text = text

        //Start the dialog and display it on screen
        mProgressDialog.show()
    }

    //Function is used to dismiss the progress dialog if it is visible to user
    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }

    fun getCurrentUserID(): String{
        //Get active user ID from Firebase Authentication
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    //Function show the error message as SnackBar
    fun showErrorSnackBar(message: String){
        //create SnackBar
        val snackBar = Snackbar.make(
            findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_LONG
            )
        //set background color
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.snacbar_error))
        snackBar.show()
    }

    //Function return a file extension of selected image
    fun getFileExtension(uri: Uri?): String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(
            contentResolver.getType(uri!!)
        )
    }
}