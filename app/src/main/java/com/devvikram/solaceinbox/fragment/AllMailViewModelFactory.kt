package com.devvikram.solaceinbox.fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AllMailViewModelFactory(): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllMailViewModel::class.java)) {
            return AllMailViewModel(AllmailRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}