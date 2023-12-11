package com.example.colaboraboard.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.example.colaboraboard.R
import com.example.colaboraboard.databinding.ActivityMainBinding
import com.example.colaboraboard.databinding.NavHeaderMainBinding
import com.example.colaboraboard.firebase.FirestoreClass
import com.example.colaboraboard.models.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var mainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        //setContentView(R.layout.activity_main)

        setUpActionBar()

        mainBinding.navView.setNavigationItemSelectedListener(this)
        /*
        val navView = findViewById<NavigationView>(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)
        */

        FirestoreClass().signInUser(this)

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if(mainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                    mainBinding.drawerLayout.closeDrawer(GravityCompat.START)
                }else{
                    doubleBackToExit()
                }
                /*
                val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
                if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START)
                }else{
                    doubleBackToExit()
                }
                */
            }
        })
    }

    private fun setUpActionBar(){
        setSupportActionBar(mainBinding.appBarMain.toolbarMainActivity)
        mainBinding.appBarMain.toolbarMainActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        mainBinding.appBarMain.toolbarMainActivity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer(){

        if(mainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            mainBinding.drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            mainBinding.drawerLayout.openDrawer(GravityCompat.START)
        }
        /*
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            drawerLayout.openDrawer(GravityCompat.START)
        }
        */
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile ->{
                Toast.makeText(
                    this@MainActivity,
                    "My profile",
                    Toast.LENGTH_SHORT
                ).show()
            }
            R.id.nav_sign_out ->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        mainBinding.drawerLayout.closeDrawer(GravityCompat.START)
        //val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        //drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun updateNavigationUserDetails(user : User){
        // The instance of the header view of the navigation view.
        val viewHeader = mainBinding.navView.getHeaderView(0)
        val headerBinding = viewHeader?.let { NavHeaderMainBinding.bind(it) }
        headerBinding?.navUserImage?.let {
            Glide
                .with(this)
                .load(user.image) // URL of the image
                .centerCrop() // Scale type of the image.
                .placeholder(R.drawable.ic_user_place_holder) // A default place holder
                .into(it)
        } // the view in which the image will be loaded.

        headerBinding?.tvUsername?.text = user.name
    }
}