package com.example.colaboraboard.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import com.example.colaboraboard.R
import com.example.colaboraboard.databinding.ActivityMainBinding
import com.example.colaboraboard.databinding.AppBarMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var barBinding: AppBarMainBinding
    private lateinit var drawerBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        drawerBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(drawerBinding.root)
        //setContentView(binding.root)

        setUpActionBar()
        drawerBinding.navView.setNavigationItemSelectedListener(this)
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(drawerBinding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerBinding.drawerLayout.closeDrawer(GravityCompat.START)
                }else{
                    doubleBackToExit()
                }
            }
        })
    }

    private fun setUpActionBar(){
        barBinding = AppBarMainBinding.inflate(layoutInflater)
        setSupportActionBar(barBinding.toolbarMainActivity)
        barBinding.toolbarMainActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        barBinding.toolbarMainActivity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer(){
        if(drawerBinding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerBinding.drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            drawerBinding.drawerLayout.openDrawer(GravityCompat.START)
        }
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
        drawerBinding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}