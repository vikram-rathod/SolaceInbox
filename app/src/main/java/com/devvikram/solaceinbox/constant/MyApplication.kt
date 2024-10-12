package com.devvikram.solaceinbox.constant

import android.app.Application
import com.devvikram.solaceinbox.activities.addcompose.AddComposeMailRepository
import com.devvikram.solaceinbox.activities.addcompose.AddComposeMailViewmodel
import com.devvikram.solaceinbox.activities.login.AuthRepository
import com.devvikram.solaceinbox.activities.login.AuthViewModel
import com.devvikram.solaceinbox.common.SharedViewModel
import com.devvikram.solaceinbox.fragment.AllMailViewModel
import com.devvikram.solaceinbox.fragment.AllmailRepository

class MyApplication : Application() {
    private val authRepository by lazy { AuthRepository() }
    private val allMailRepository by lazy { AllmailRepository() }
    private val addComposeMailRepository by lazy { AddComposeMailRepository() }

    val allMailsViewModel by lazy { AllMailViewModel(allMailRepository) }
    val authViewModel by lazy { AuthViewModel(authRepository, this) }
    val addComposeMailViewModel by lazy { AddComposeMailViewmodel(addComposeMailRepository, this) }


    val sharedViewModel: SharedViewModel by lazy { SharedViewModel() }


    override fun onCreate() {
        super.onCreate()
        // Initialize any necessary libraries or components here
        appContext = this

    }

    // Other application-level initialization code

    companion object {
        lateinit var appContext: MyApplication

    }

}