package com.example.colaboraboard.activities

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.example.colaboraboard.R
import com.example.colaboraboard.databinding.ActivitySignUpBinding
import com.example.colaboraboard.firebase.FirestoreClass
import com.example.colaboraboard.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
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

    fun userRegisteredSuccess(){
        Toast.makeText(
            this,
            "You have successfully registered!",
            Toast.LENGTH_LONG
        ).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    //Set the button to go bac to the Intro screen
    private fun setupActionBar(){
        //val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_sign_up_activity)
        //setSupportActionBar(toolbar)
        setSupportActionBar(binding.toolbarSignUpActivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        //toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.toolbarSignUpActivity.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        /*
        val btnSignUp = findViewById<AppCompatButton>(R.id.btn_sign_up)
        btnSignUp.setOnClickListener {
            registerUser()
        }
        */
        binding.btnSignUp.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser(){
        /*
        val etName = findViewById<EditText>(R.id.et_name)
        val name: String = etName.text.toString().trim { it <= ' ' }
        val etEmail = findViewById<EditText>(R.id.et_email)
        val email: String = etEmail.text.toString().trim { it <= ' ' }
        val etPassword = findViewById<EditText>(R.id.et_password)
        val password: String = etPassword.text.toString().trim { it <= ' ' }
        */
        val name: String = binding.etName.text.toString().trim { it <= ' ' }
        val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
        val password: String = binding.etPassword.text.toString().trim { it <= ' ' }


        if(validateForm(name, email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!
                        val user = User(firebaseUser.uid, name, registeredEmail)
                        FirestoreClass().registerUser(this, user)
                    } else {
                        Toast.makeText(
                            this,
                            task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

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
}