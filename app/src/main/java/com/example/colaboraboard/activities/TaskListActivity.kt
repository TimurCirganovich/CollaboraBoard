package com.example.colaboraboard.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.colaboraboard.R
import com.example.colaboraboard.adapters.TaskListItemsAdapter
import com.example.colaboraboard.databinding.ActivityTaskListBinding
import com.example.colaboraboard.firebase.FirestoreClass
import com.example.colaboraboard.models.Board
import com.example.colaboraboard.models.Task
import com.example.colaboraboard.utils.Constants

class TaskListActivity : BaseActivity() {

    private lateinit var taskListBinding: ActivityTaskListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskListBinding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(taskListBinding.root)

        var boardDocumentId = ""
        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getBoardDetails(this, boardDocumentId)
    }

    private fun setUpActionBar(title: String){
        setSupportActionBar(taskListBinding.toolbarTaskListActivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = title
        }

        taskListBinding.toolbarTaskListActivity.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    fun boardDetails(board: Board){
        hideProgressDialog()
        setUpActionBar(board.name)

        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)

        taskListBinding.rvTaskList.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        taskListBinding.rvTaskList.setHasFixedSize(true)
        val adapter = TaskListItemsAdapter(this, board.taskList)
        taskListBinding.rvTaskList.adapter = adapter
    }
}