package com.devvikram.solaceinbox.activities.addcompose

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddComposeMailViewmodelFactory(
    private val repository: AddComposeMailRepository,
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddComposeMailViewmodel::class.java)) {
            return AddComposeMailViewmodel(
                repository,
                application
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }

}