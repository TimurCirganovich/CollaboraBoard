package com.example.collaboraboard.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.example.collaboraboard.R
import com.example.collaboraboard.adapters.CardMemberListItemsAdapter
import com.example.collaboraboard.databinding.ActivityCardDetailsBinding
import com.example.collaboraboard.dialogs.LabelColorListDialog
import com.example.collaboraboard.dialogs.MembersListDialog
import com.example.collaboraboard.firebase.FirestoreClass
import com.example.collaboraboard.models.Board
import com.example.collaboraboard.models.Card
import com.example.collaboraboard.models.SelectedMembers
import com.example.collaboraboard.models.Task
import com.example.collaboraboard.models.User
import com.example.collaboraboard.utils.Constants
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CardDetailsActivity : BaseActivity() {
    private lateinit var cardDetailsBinding: ActivityCardDetailsBinding     //A global variable for UI element access

    private lateinit var mBoardDetails: Board   //A global variable for board details
    private var mTaskListPosition = -1          //A global variable for task item position
    private var mCardPosition = -1              //A global variable for card item position

    private var mSelectedColor: String = ""     //A global variable for selected label color

    private lateinit var mMembersDetailList: ArrayList<User>    //A global variable for Assigned Members List.

    private var mSelectedDueDateMilliseconds: Long = 0      //A global variable for selected due date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding init for UI elements access
        cardDetailsBinding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(cardDetailsBinding.root)

        getIntentData()
        setUpActionBar()

        //set text of EditText field as card name
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

        //read the labelColor of a card
        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor
        if (mSelectedColor.isNotEmpty()){
            setColor()
        }

        //UPDATE button
        cardDetailsBinding.btnUpdateCardDetails.setOnClickListener {
            if (cardDetailsBinding.etNameCardDetails.text.toString().isNotEmpty()){     //if card name is not empty
                updateCardDetails()
            }else{
                Toast.makeText(
                    this@CardDetailsActivity,
                    "Please Enter A Card Name",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        //Colors selection field
        cardDetailsBinding.tvSelectLabelColor.setOnClickListener {
            labelColorsListDialog()
        }

        //Members selection field
        cardDetailsBinding.tvSelectMembers.setOnClickListener {
            membersListDialog()
        }

        setUpSelectedMembersList()

        //read the dueDate of a card
        mSelectedDueDateMilliseconds = mBoardDetails
            .taskList[mTaskListPosition]
            .cards[mCardPosition]
            .dueDate
        if (mSelectedDueDateMilliseconds > 0){      //if dueDate exist
            val simpleDateFormat = SimpleDateFormat("dd/mm/yyyy", Locale.ENGLISH)   //set date format
            val selectedDate = simpleDateFormat.format(Date(mSelectedDueDateMilliseconds))  //convert date from long to date format
            cardDetailsBinding.tvSelectDueDate.text = selectedDate  //set date as visible text
        }

        //Due date selection field
        cardDetailsBinding.tvSelectDueDate.setOnClickListener {
            showDatePicker()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //Inflate the menu to use in the action bar
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Handle presses on the action bar menu items
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

    //Function to setup action bar
    private fun setUpActionBar(){
        setSupportActionBar(cardDetailsBinding.toolbarCardDetailsActivity)  //set Toolbar as ActionBar
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

    //Function to get all the data that is sent through intent.
    private fun getIntentData(){
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            //should check the Android version because of deprecated method usage
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
        if(intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
            //should check the Android version because of deprecated method usage
            mMembersDetailList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST, User::class.java)!!
            } else {
                intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
            }
        }
    }

    //Function to get the result of add or updating the task list.
    fun addUpdateTaskListSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    //Function to update card details.
    private fun updateCardDetails(){
        val card = Card(
            cardDetailsBinding.etNameCardDetails.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,
            mSelectedColor,
            mSelectedDueDateMilliseconds
        )

        //assign the update card details to the task list using the card position
        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition] = card
        //otherwise will be created another empty task list (users don't want it to happen)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)


        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    //Function to delete the card from the task list.
    private fun deleteCard(){
        //get the card list of task list by it's position
        val cardsList: ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards
        cardsList.removeAt(mCardPosition)   //remove card by it's position
        val taskList: ArrayList<Task> = mBoardDetails.taskList
        //otherwise will be created another empty task list (users don't want it to happen)
        taskList.removeAt(taskList.size-1)

        taskList[mTaskListPosition].cards = cardsList   //update data about cards

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    //Function to show an alert dialog for the confirmation to delete the card
    private fun alertDialogForDeleteCard(cardName: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.alert))   //set title for alert dialog
        builder.setMessage(resources.getString(R.string.confirmation_message_to_delete, cardName))  //set message for alert dialog
        builder.setIcon(R.drawable.ic_dialog_alert)     //set icon for alert dialog
        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->
            dialogInterface.dismiss()   //dialog will be dismissed
            deleteCard()
        }
        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->
            dialogInterface.dismiss()   //dialog will be dismissed
        }

        val alertDialog: AlertDialog = builder.create() //creating a dialog
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    //Function to add some static label colors in the list
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

    //Function to remove the text and set the label color to the TextView
    private fun setColor(){
        cardDetailsBinding.tvSelectLabelColor.text = ""
        cardDetailsBinding.tvSelectLabelColor.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    //Function to launch the label color list dialog.
    private fun labelColorsListDialog(){
        val colorsList: ArrayList<String> = colorsList()
        val listDialog = object : LabelColorListDialog(
            this,
            colorsList,
            resources.getString(R.string.select_label_color),
            mSelectedColor
        ){
            //when user press on a color
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }

        }
        listDialog.show()
    }

    //Function to setup the recyclerView for card assigned members
    private fun setUpSelectedMembersList(){
        val cardAssignedMembersList = mBoardDetails
            .taskList[mTaskListPosition]
            .cards[mCardPosition]
            .assignedTo
        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

        for (i in mMembersDetailList.indices){          //each user assigned to the board
            for (j in cardAssignedMembersList){     //each user assigned to the card
                if (mMembersDetailList[i].id == j){
                    val selectedMember = SelectedMembers(
                        mMembersDetailList[i].id,
                        mMembersDetailList[i].image
                    )
                    selectedMembersList.add(selectedMember)     //fill selected user list
                }
            }
        }
        if (selectedMembersList.size > 0){  //if any other user was selected
            selectedMembersList.add(SelectedMembers("",""))
            cardDetailsBinding.tvSelectMembers.visibility = View.GONE
            cardDetailsBinding.rvSelectedMembersList.visibility = View.VISIBLE
            cardDetailsBinding.rvSelectedMembersList.layoutManager = GridLayoutManager(
                this,
                5
            )
            val adapter = CardMemberListItemsAdapter(this, selectedMembersList, true)
            cardDetailsBinding.rvSelectedMembersList.adapter = adapter
            adapter.setOnClickListener(
                object: CardMemberListItemsAdapter.OnClickListener{
                    override fun onClick() {
                        membersListDialog()
                    }
                }
            )
        }else{
            cardDetailsBinding.tvSelectMembers.visibility = View.VISIBLE
            cardDetailsBinding.rvSelectedMembersList.visibility = View.GONE
        }
    }

    private fun membersListDialog(){
        val cardAssignedMembersList = mBoardDetails
            .taskList[mTaskListPosition]
            .cards[mCardPosition]
            .assignedTo

        if(cardAssignedMembersList.size > 0){                       //check if any member is in the list(assigned to card)
            for (i in mMembersDetailList.indices){             //go trough all members in the list(assigned to board)
                for (j in cardAssignedMembersList){         //go trough all members in the list(assigned to card)
                    if (mMembersDetailList[i].id == j){           //check for ID's of those who are on board & on card
                        mMembersDetailList[i].selected = true    //if member assigned to board & card, make the member selected
                    }
                }
            }
        }else{                                                      //if no members assigned to card
            for (i in mMembersDetailList.indices){             //go trough all members in the list(assigned to board)
                mMembersDetailList[i].selected = false             //make the member unselected
            }
        }

        val listDialog = object : MembersListDialog(
            this,
            mMembersDetailList,
            resources.getString(R.string.select_members)
        ){
            override fun onItemSelected(user: User, action: String) {
                if (action == Constants.SELECT){
                    if (!mBoardDetails
                        .taskList[mTaskListPosition]
                        .cards[mCardPosition]
                        .assignedTo
                        .contains(user.id)
                        ){
                        mBoardDetails
                            .taskList[mTaskListPosition]
                            .cards[mCardPosition]
                            .assignedTo
                            .add(user.id)
                    }
                }else{
                    mBoardDetails
                        .taskList[mTaskListPosition]
                        .cards[mCardPosition]
                        .assignedTo
                        .remove(user.id)

                    for (i in mMembersDetailList.indices){
                        if (mMembersDetailList[i].id == user.id){
                            mMembersDetailList[i].selected = false
                        }
                    }
                }
                setUpSelectedMembersList()
            }
        }
        listDialog.show()
    }

    private fun showDatePicker(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, sYear, monthOfYear, dayOfMonth ->
                val sDayOfMonth = if(dayOfMonth < 10){
                    "0$dayOfMonth"
                }else{
                    "$dayOfMonth"
                }

                val sMonthOfYear = if((monthOfYear+1) < 10){
                    "0${monthOfYear + 1}"
                }else{
                    "${monthOfYear + 1}"
                }

                val selectedDate = "$sDayOfMonth/$sMonthOfYear/$sYear"
                cardDetailsBinding.tvSelectDueDate.text = selectedDate

                val simpleDateFormat = SimpleDateFormat("dd/mm/yyyy", Locale.ENGLISH)
                val theDate = simpleDateFormat.parse(selectedDate)
                mSelectedDueDateMilliseconds = theDate!!.time
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}