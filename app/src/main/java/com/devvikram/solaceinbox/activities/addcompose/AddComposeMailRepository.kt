package com.devvikram.solaceinbox.activities.addcompose

import com.devvikram.solaceinbox.MyFirebase.FirebaseInstance
import com.devvikram.solaceinbox.model.Mail
import com.devvikram.solaceinbox.model.UserModel
import java.util.Date

class AddComposeMailRepository {
    private val firebaseAuth = FirebaseInstance.getFirebaseAuth()
    private val firestore = FirebaseInstance.getFirebaseFireStore()


    fun addComposeMail(mail: Mail, senderUsername: String, callback: (Boolean, String?) -> Unit) {
        val docRef = firestore.collection("mails").document()
        mail.id = docRef.id
        mail.senderName = senderUsername
        mail.senderId = firebaseAuth.currentUser?.uid?: ""
        mail.cDate = Date().toString()

        docRef.set(mail)
            .addOnSuccessListener {
                callback(true, "Mail Sent successfully")
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
                e.printStackTrace()
            }
    }

    fun getUsers(function: (Boolean, String?, ArrayList<UserModel>?) -> Unit) {
        firestore.collection("users")
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val users = ArrayList<UserModel>()
                    for (document in result) {
                        val user = document.toObject(UserModel::class.java)
                        users.add(user)
                    }
                    function(true, "Users fetched successfully", users)
                } else {
                    function(false, "No users found", null)
                }
            }
            .addOnFailureListener { e ->
                function(false, e.message, null)
            }
    }

}