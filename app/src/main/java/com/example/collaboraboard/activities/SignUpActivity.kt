package com.example.collaboraboard.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.example.collaboraboard.R
import com.example.collaboraboard.databinding.ActivitySignUpBinding
import com.example.collaboraboard.firebase.FirestoreClass
import com.example.collaboraboard.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding     //A global variable for UI element access

    private lateinit var auth: FirebaseAuth     //A global variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding init for UI elements access
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialize the Firebase variable
        auth = FirebaseAuth.getInstance()

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

    //Function to setup action bar
    //Set the button to go back to the Intro screen
    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarSignUpActivity)      //set Toolbar as ActionBar
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)       //param for button to return back
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)     //button icon
        }

        binding.toolbarSignUpActivity.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.btnSignUp.setOnClickListener {
            registerUser()
        }
    }

    //Function to register user in Firebase Authentication
    private fun registerUser(){
        //get user data from EditText fields
        val name: String = binding.etName.text.toString().trim { it <= ' ' }
        val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
        val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

        if(validateForm(name, email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))       //show progress dialog to user
            FirebaseAuth.getInstance()      //Firebase Authentication instance
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //user is created in Firebase Authentication
                        val firebaseUser: FirebaseUser = task.result!!.user!!   //Firebase Authentication user data
                        val registeredEmail = firebaseUser.email!!
                        val user = User(firebaseUser.uid, name, registeredEmail)     //user model
                        //function call to save user data in the Firebase Firestore database
                        FirestoreClass().registerUser(this, user)
                    } else {
                        //user is not created in Firebase Authentication
                        //show error message to user
                        hideProgressDialog()
                        Toast.makeText(
                            this@SignUpActivity,
                            task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    //Function to check if all form fields are filled
    private fun validateForm(name: String, email: String, password: String) : Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please enter a name")
                false
            }
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter an email address")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please enter a password")
                false
            } else ->{
                true
            }
        }
    }

    //Function to notify about successful registration
    fun userRegisteredSuccess(){
        Toast.makeText(
            this,
            "You have successfully registered!",
            Toast.LENGTH_LONG
        ).show()        //show user confirmation message
        hideProgressDialog()
        auth.signOut()      //Firebase Authentication sign out for further actions
        autoSignIn()
    }

    private fun autoSignIn(){
        val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
        val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

        showProgressDialog(resources.getString(R.string.please_wait))
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                hideProgressDialog()
                if (task.isSuccessful) {
                    // Sign in success
                    Log.d("SignUp", "signInWithEmail:success")
                    FirestoreClass().loadUserData(this)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("SignUp", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        this@SignUpActivity,
                        task.exception!!.message,
                        Toast.LENGTH_SHORT,
                    ).show()
                    hideProgressDialog()
                }
            }
    }

    fun autoSignInSuccess(){
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}