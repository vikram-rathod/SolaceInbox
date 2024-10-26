package com.devvikram.solaceinbox.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devvikram.solaceinbox.activities.addcompose.AddComposeMailRepository
import com.devvikram.solaceinbox.fragment.AllmailRepository

class SharedViewModel(
):ViewModel(){

    private val _shouldRefreshEmails = MutableLiveData<Boolean>(false)
    val shouldRefreshEmails: LiveData<Boolean> = _shouldRefreshEmails

    fun setRefreshFlag(value: Boolean) {
        _shouldRefreshEmails.value = value
    }


}