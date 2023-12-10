package com.example.colaboraboard.firebase

import android.util.Log
import com.example.colaboraboard.activities.SignInActivity
import com.example.colaboraboard.activities.SignUpActivity
import com.example.colaboraboard.models.User
import com.example.colaboraboard.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener {
                e->
                Log.e(activity.javaClass.simpleName, "Error writing document", e)
            }
    }

    fun signInUser(activity: SignInActivity){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)
                if(loggedInUser!=null){
                    activity.signInSuccess(loggedInUser)
                }
            }.addOnFailureListener {
                    e->
                Log.e(activity.javaClass.simpleName, "Error while getting loggedIn user details", e)
            }
    }
    fun getCurrentUserId(): String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
}