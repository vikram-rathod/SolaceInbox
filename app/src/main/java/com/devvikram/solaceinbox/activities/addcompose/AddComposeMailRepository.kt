package com.devvikram.solaceinbox.activities.addcompose

import android.net.Uri
import com.devvikram.solaceinbox.MyFirebase.FirebaseInstance
import com.devvikram.solaceinbox.model.Mail
import com.devvikram.solaceinbox.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.util.Date

class AddComposeMailRepository {
    private val firebaseAuth = FirebaseInstance.getFirebaseAuth()
    private val firestore = FirebaseInstance.getFirebaseFireStore()
    private val storageRef: StorageReference = FirebaseInstance.getFirebaseStorage().reference



    suspend fun addComposeMail(mail: Mail): Boolean {
        return try {
            val firestore = FirebaseFirestore.getInstance()

            val mailId = firestore.collection("mails").document().id

            val mailToAdd = mail.copy(
                id = mailId,
                senderId = mail.senderId,
                senderName = mail.senderName,
                senderEmail = mail.senderEmail
            )

            firestore.collection("mails").document(mailId).set(mailToAdd).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
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

    suspend fun uploadAttachment(fileUri: Uri, fileName: String): String? {
        return try {
            val fileRef = storageRef.child("attachments/$fileName")
            fileRef.putFile(fileUri).await()
            val downloadUrl = fileRef.downloadUrl.await()
            downloadUrl.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


}