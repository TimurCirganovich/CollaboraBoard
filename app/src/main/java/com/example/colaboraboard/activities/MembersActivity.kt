package com.example.colaboraboard.activities

import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.colaboraboard.R
import com.example.colaboraboard.adapters.MemberListItemsAdapter
import com.example.colaboraboard.databinding.ActivityMembersBinding
import com.example.colaboraboard.databinding.DialogSearchMemberBinding
import com.example.colaboraboard.firebase.FirestoreClass
import com.example.colaboraboard.models.Board
import com.example.colaboraboard.models.User
import com.example.colaboraboard.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MembersActivity : BaseActivity() {
    private lateinit var membersBinding: ActivityMembersBinding

    private lateinit var mBoardDetails: Board
    private lateinit var mAssignedMembersList: ArrayList<User>
    private var anyChangesMade: Boolean = false
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
        mAssignedMembersList = list
        hideProgressDialog()

        membersBinding.rvMembersList.layoutManager = LinearLayoutManager(this)
        membersBinding.rvMembersList.setHasFixedSize(true)

        val adapter = MemberListItemsAdapter(this, list)
        membersBinding.rvMembersList.adapter = adapter
    }

    fun memberDetails(user: User){
        mBoardDetails.assignedTo.add(user.id)
        FirestoreClass().assignMemberToBoard(this, mBoardDetails, user)
    }

    private fun setUpActionBar(){
        setSupportActionBar(membersBinding.toolbarMembersActivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.members)
        }

        membersBinding.toolbarMembersActivity.setNavigationOnClickListener {
            if(anyChangesMade){
                setResult(Activity.RESULT_OK)
            }
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_member ->{
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dialogSearchMember(){
        val dialogBinding = DialogSearchMemberBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.tvAdd.setOnClickListener {
            val email = dialogBinding.etEmailSearchMember.text.toString()
            if (email.isNotEmpty()){
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getMemberDetails(this, email)
            }else{
                Toast.makeText(
                    this@MembersActivity,
                    "Please enter members email.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        dialogBinding.tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun memberAssignSuccess(user: User){    //Update UI with data after adding a member
        hideProgressDialog()
        mAssignedMembersList.add(user)
        anyChangesMade = true   //Marks that changes were made
        setupMembersList(mAssignedMembersList)

        SendNotificationToUser(mBoardDetails.name, user.fcmToken).start()
    }

    private inner class SendNotificationToUser(
        val boardName: String,
        val token: String
        ){
        fun start(){
            showProgressDialog(resources.getString(R.string.please_wait))
            lifecycleScope.launch(Dispatchers.IO) {
                val stringResult=notifyInBackground()
                afterFinish(stringResult)
            }
        }

        fun notifyInBackground(): String {
            var result:String
            var connection: HttpURLConnection? = null
            try {
                val url = URL(Constants.FCM_BASE_URL)
                connection = url.openConnection() as HttpURLConnection
                connection.doOutput = true
                connection.doInput  = true
                connection.instanceFollowRedirects = false
                connection.requestMethod = "POST"

                connection.setRequestProperty("Content-type","application/json")
                connection.setRequestProperty("charset", "utf-8")
                connection.setRequestProperty("Accept","application/json")

                connection.setRequestProperty(
                    Constants.FCM_AUTHORIZATION,
                    "${Constants.FCM_KEY}=${Constants.FCM_SERVER_KEY}"
                )

                connection.useCaches = false

                val writer = DataOutputStream(connection.outputStream)
                val jsonRequest = JSONObject()
                val dataObject = JSONObject()
                dataObject.put(Constants.FCM_KEY_TITLE, "Assigned to the Board $boardName")
                dataObject.put(Constants.FCM_KEY_MESSAGE, "You have been assigned to the Board by ${mAssignedMembersList[0].name}")

                jsonRequest.put(Constants.FCM_KEY_DATA, dataObject)
                jsonRequest.put(Constants.FCM_KEY_TO, token)

                writer.writeBytes(jsonRequest.toString())
                writer.flush()
                writer.close()

                val httpResult: Int = connection.responseCode
                if(httpResult == HttpURLConnection.HTTP_OK){
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))

                    val stringBuilder = StringBuilder()
                    var line: String?
                    try {
                        while (reader.readLine().also { line = it } != null){
                            stringBuilder.append(line+"\n")
                        }
                    }catch(e: IOException){
                        e.printStackTrace()
                    }finally {
                        try {
                            inputStream.close()
                        }catch (e: IOException){
                            e.printStackTrace()
                        }
                    }
                    result = stringBuilder.toString()
                }else{
                    result = connection.responseMessage
                }
            }catch (e: SocketTimeoutException){
                result = "Connection Timeout"
            }catch (e: Exception){
                result = "Error: ${e.message}"
            }finally {
                connection?.disconnect()
            }
            return result
        }

        fun afterFinish(stringResult: String){
            hideProgressDialog()
            Log.i("JSON Response result", stringResult)
        }
    }
}