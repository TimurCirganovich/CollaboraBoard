package com.example.colaboraboard.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.colaboraboard.R
import com.example.colaboraboard.adapters.BoardItemsAdapter
import com.example.colaboraboard.databinding.ActivityMainBinding
import com.example.colaboraboard.databinding.NavHeaderMainBinding
import com.example.colaboraboard.firebase.FirestoreClass
import com.example.colaboraboard.models.Board
import com.example.colaboraboard.models.User
import com.example.colaboraboard.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var mUserName: String

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

        mainBinding.appBarMain.fabCreateBoard.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            boardLauncher.launch(intent)

        }

        FirestoreClass().loadUserData(this, true)

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

    fun populateBoardListToUI(boardsList: ArrayList<Board>){
        hideProgressDialog()

        if(boardsList.size > 0){
            mainBinding.appBarMain.mainContent.rvBoardsList.visibility = View.VISIBLE
            mainBinding.appBarMain.mainContent.tvNoBoardsAvailable.visibility = View.GONE

            mainBinding.appBarMain.mainContent.rvBoardsList.layoutManager = LinearLayoutManager(this)
            mainBinding.appBarMain.mainContent.rvBoardsList.setHasFixedSize(true)

            val adapter = BoardItemsAdapter(this, boardsList)
            mainBinding.appBarMain.mainContent.rvBoardsList.adapter = adapter

            adapter.setOnClickListener(object: BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentID)
                    startActivity(intent)
                }
            })

        }else{
            mainBinding.appBarMain.mainContent.rvBoardsList.visibility = View.GONE
            mainBinding.appBarMain.mainContent.tvNoBoardsAvailable.visibility = View.VISIBLE
        }
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
                startUpdateActivityAndGetResult.launch(
                    Intent(this, MyProfileActivity::class.java)
                )
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

    private val startUpdateActivityAndGetResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                FirestoreClass().loadUserData(this)
            } else {
                Log.e("onActivityResult()", "Profile update cancelled by user")
            }
        }

    private val boardLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            FirestoreClass().getBoardsList(this)
        }else {
            Log.e("onActivityResult()", "Creating board cancelled by user")
        }
    }

    fun updateNavigationUserDetails(user : User, readBoardsList: Boolean){
        mUserName = user.name
        // The instance of the header view of the navigation view.
        val viewHeader = mainBinding.navView.getHeaderView(0)
        val headerBinding = viewHeader?.let { NavHeaderMainBinding.bind(it) }
        headerBinding?.navUserImage?.let {
            Glide
                .with(this@MainActivity)
                .load(user.image) // URL of the image
                .centerCrop() // Scale type of the image.
                .placeholder(R.drawable.ic_user_place_holder) // A default place holder
                .into(it)
        } // the view in which the image will be loaded.

        headerBinding?.tvUsername?.text = user.name

        if(readBoardsList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsList(this)
        }
    }
}