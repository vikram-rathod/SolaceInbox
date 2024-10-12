package com.devvikram.solaceinbox.fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvikram.solaceinbox.MyFirebase.FirebaseInstance
import com.devvikram.solaceinbox.model.Mail
import kotlinx.coroutines.launch

class AllMailViewModel(
    private val repository: AllmailRepository
) : ViewModel() {
    private val currentUserId = FirebaseInstance.getFirebaseAuth().currentUser?.uid ?: ""

    private val _allMails = MutableLiveData<MailState>()
    val allMails: LiveData<MailState> = _allMails


    init {
        fetchAllMails()
    }

    fun fetchAllMails() {
        viewModelScope.launch {
            repository.getAllMails() { mailArrayList: ArrayList<Mail>, message: String ->
                if (mailArrayList.isNotEmpty()) {
                    val filteredMails = mailArrayList.filter { mail ->
                        mail.recipients.any { recipient -> recipient.userId == currentUserId }
                    }
                    Log.d("TAG", "fetchAllMails: list are  $mailArrayList")
                    _allMails.value =
                        MailState(mails = filteredMails as ArrayList<Mail>, message = message, isSuccessful = true)
                } else {
                    _allMails.value = MailState(message = message, isFailure = true)
                }
            }
        }
    }
    fun resetState() {
        _allMails.value = MailState()
    }
    data class MailState(
        val isLoading: Boolean = false,
        val status: Boolean = false,
        val message: String = "",
        val mails: ArrayList<Mail> = ArrayList(),
        val isSuccessful: Boolean = false,
        val isFailure: Boolean = false,
    )

}