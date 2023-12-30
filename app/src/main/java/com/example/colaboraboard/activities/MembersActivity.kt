package com.example.colaboraboard.activities

import android.os.Build
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.colaboraboard.R
import com.example.colaboraboard.adapters.MemberListItemsAdapter
import com.example.colaboraboard.databinding.ActivityMembersBinding
import com.example.colaboraboard.firebase.FirestoreClass
import com.example.colaboraboard.models.Board
import com.example.colaboraboard.models.User
import com.example.colaboraboard.utils.Constants

class MembersActivity : BaseActivity() {
    private lateinit var membersBinding: ActivityMembersBinding

    private lateinit var mBoardDetails: Board
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        membersBinding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(membersBinding.root)

        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(Constants.BOARD_DETAIL, Board::class.java)!!
            } else {
                intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
            }
        }

        setUpActionBar()

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersListDetails(this, mBoardDetails.assignedTo)
    }

    fun setupMembersList(list: ArrayList<User>){
        hideProgressDialog()

        membersBinding.rvMembersList.layoutManager = LinearLayoutManager(this)
        membersBinding.rvMembersList.setHasFixedSize(true)

        val adapter = MemberListItemsAdapter(this, list)
        membersBinding.rvMembersList.adapter = adapter
    }

    private fun setUpActionBar(){
        setSupportActionBar(membersBinding.toolbarMembersActivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.members)
        }

        membersBinding.toolbarMembersActivity .setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

}