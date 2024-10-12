package com.devvikram.solaceinbox.MyFirebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseInstance {
    private val auth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()

    companion object {
        private var instance: FirebaseInstance? = null
        private fun getInstance(): FirebaseInstance {
            if (instance == null) {
                instance = FirebaseInstance()
            }
            return instance!!

        }

        fun getFirebaseAuth(): FirebaseAuth {
            return getInstance().auth
        }

        fun getFirebaseFireStore(): FirebaseFirestore {
            return getInstance().fireStore
        }


    }

}