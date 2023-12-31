package com.example.colaboraboard.activities

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.colaboraboard.R
import com.example.colaboraboard.databinding.ActivityCardDetailsBinding
import com.example.colaboraboard.dialogs.LabelColorListDialog
import com.example.colaboraboard.firebase.FirestoreClass
import com.example.colaboraboard.models.Board
import com.example.colaboraboard.models.Card
import com.example.colaboraboard.models.Task
import com.example.colaboraboard.utils.Constants

class CardDetailsActivity : BaseActivity() {
    private lateinit var cardDetailsBinding: ActivityCardDetailsBinding

    private lateinit var mBoardDetails: Board
    private var mTaskListPosition = -1
    private var mCardPosition = -1

    private var mSelectedColor: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardDetailsBinding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(cardDetailsBinding.root)

        getIntentData()
        setUpActionBar()

        cardDetailsBinding.etNameCardDetails.setText(
            mBoardDetails
            .taskList[mTaskListPosition]
            .cards[mCardPosition]
            .name
        )

        //set pointer to the end of name of card inside EditText field
        cardDetailsBinding.etNameCardDetails.setSelection(
            cardDetailsBinding.etNameCardDetails.text.toString().length
        )

        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor
        if (mSelectedColor.isNotEmpty()){
            setColor()
        }

        cardDetailsBinding.btnUpdateCardDetails.setOnClickListener {
            if (cardDetailsBinding.etNameCardDetails.text.toString().isNotEmpty()){
                updateCardDetails()
            }else{
                Toast.makeText(
                    this@CardDetailsActivity,
                    "Please Enter A Card Name",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        cardDetailsBinding.tvSelectLabelColor.setOnClickListener {
            labelColorsListDialog()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete_card ->{
                alertDialogForDeleteCard(
                    mBoardDetails
                    .taskList[mTaskListPosition]
                    .cards[mCardPosition]
                    .name
                )
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpActionBar(){
        setSupportActionBar(cardDetailsBinding.toolbarCardDetailsActivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = mBoardDetails
                .taskList[mTaskListPosition]
                .cards[mCardPosition]
                .name
        }

        cardDetailsBinding.toolbarCardDetailsActivity.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun getIntentData(){
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(Constants.BOARD_DETAIL, Board::class.java)!!
            } else {
                intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
            }
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }
        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun updateCardDetails(){
        val card = Card(
            cardDetailsBinding.etNameCardDetails.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,
            mSelectedColor
        )
        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition] = card
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    private fun deleteCard(){
        val cardsList: ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards
        cardsList.removeAt(mCardPosition)
        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)

        taskList[mTaskListPosition].cards = cardsList

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    private fun alertDialogForDeleteCard(cardName: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.alert))
        builder.setMessage(resources.getString(R.string.confirmation_message_to_delete, cardName))
        builder.setIcon(R.drawable.ic_dialog_alert)
        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->
            dialogInterface.dismiss()
            deleteCard()
        }
        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create() //creating a dialog
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun colorsList(): ArrayList<String>{
        val colorsList: ArrayList<String> = ArrayList()
        colorsList.add("#0C90F1")
        colorsList.add("#FF3747")
        colorsList.add("#4FCBBB")
        colorsList.add("#EF39A7")
        colorsList.add("#FFAE90")
        colorsList.add("#FFD600")
        //TODO: functional to add custom colors
        return colorsList
    }

    private fun setColor(){
        cardDetailsBinding.tvSelectLabelColor.text = ""
        cardDetailsBinding.tvSelectLabelColor.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    private fun labelColorsListDialog(){
        val colorsList: ArrayList<String> = colorsList()
        val listDialog = object : LabelColorListDialog(
            this,
            colorsList,
            resources.getString(R.string.select_label_color),
            mSelectedColor
        ){
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }

        }
        listDialog.show()
    }
}