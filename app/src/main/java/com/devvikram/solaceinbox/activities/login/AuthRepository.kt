package com.devvikram.solaceinbox.activities.login

import android.util.Log
import com.devvikram.solaceinbox.MyFirebase.FirebaseInstance
import com.devvikram.solaceinbox.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {

    private val firebaseAuth = FirebaseInstance.getFirebaseAuth()
    private val firestore = FirebaseInstance.getFirebaseFireStore()

    fun registerUser(userModel: UserModel,
                     callback: (Boolean, String) -> Unit) {
        Log.d("TAG", "registerUser: $userModel")
        firebaseAuth.createUserWithEmailAndPassword(userModel.email, userModel.password)
           .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = firebaseAuth.currentUser?.uid?: ""
                    val user = UserModel(
                        userId,
                        userModel.name,
                        userModel.email,
                        userModel.password
                    )
                    firestore.collection("users").document(userId).set(user)
                    callback(true,"Account Created Successfully")
                } else {
                    callback(false,task.exception?.message.toString())
                }
            }


    }

    fun loginUser(userModel: UserModel, function: (Boolean, String, UserModel) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(userModel.email, userModel.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var userId = firebaseAuth.currentUser?.uid
                    userId?.let {
                        val userRef = FirebaseFirestore.getInstance().collection("users").document(it)
                        userRef.get().addOnSuccessListener { document ->
                            if (document != null && document.exists()) {
                                val user = document.toObject(UserModel::class.java)?.apply {
                                    userId= it
                                }
                                if (user != null) {
                                    function(true, "Logged In Successfully", user)
                                }
                            } else {
                                function(false, "User data not found", UserModel())
                            }
                        }.addOnFailureListener { exception ->
                            function(false, "Failed to fetch user data: ${exception.message}", UserModel())
                        }
                    } ?: function(false, "User ID is null", UserModel())
                } else {
                    // Handle failed login
                    function(false, task.exception?.message.toString(), UserModel())
                }
            }
    }


    fun logoutUser(function: (Boolean, String) -> Unit) {
        firebaseAuth.signOut()
        function(true, "Logged Out Successfully")
    }
}