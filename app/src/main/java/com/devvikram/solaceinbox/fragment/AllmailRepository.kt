package com.devvikram.solaceinbox.fragment

import com.devvikram.solaceinbox.MyFirebase.FirebaseInstance
import com.devvikram.solaceinbox.model.Mail

class AllmailRepository {

    private val firebaseAuth = FirebaseInstance.getFirebaseAuth()
    private val firestore = FirebaseInstance.getFirebaseFireStore()

    fun getAllMails(callback: (ArrayList<Mail>, String) -> Unit) {
        val query = firestore.collection("mails")
        query.addSnapshotListener { value, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (value != null && !value.isEmpty) {
                val mails = ArrayList<Mail>()
                for (document in value) {

                    val mail = document.toObject(Mail::class.java)
                    mails.add(mail)
                }
                callback(mails, "All mails fetched successfully")
            } else {
                val mails = ArrayList<Mail>()
                callback(mails, "No mails found")
            }
        }
    }
}