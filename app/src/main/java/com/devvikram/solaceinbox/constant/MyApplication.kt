package com.devvikram.solaceinbox.constant

import android.app.Application
import com.devvikram.solaceinbox.activities.login.AuthRepository
import com.devvikram.solaceinbox.activities.login.AuthViewModel

class MyApplication : Application() {
    private val authRepository by lazy { AuthRepository() }
    val authViewModel by lazy { AuthViewModel(authRepository, this) }

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