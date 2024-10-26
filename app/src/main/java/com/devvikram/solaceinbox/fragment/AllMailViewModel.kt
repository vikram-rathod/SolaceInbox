package com.devvikram.solaceinbox.fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvikram.solaceinbox.MyFirebase.FirebaseInstance
import com.devvikram.solaceinbox.constant.MyApplication
import com.devvikram.solaceinbox.constant.SharedPreference
import com.devvikram.solaceinbox.model.Mail
import kotlinx.coroutines.launch

class AllMailViewModel(
    private val repository: AllmailRepository,
    private val application: MyApplication,
) : ViewModel() {
    private val sharedPreference = SharedPreference(application)
    private val currentUserId = FirebaseInstance.getFirebaseAuth().currentUser?.uid

    private val _allMails = MutableLiveData<MailState>()
    val allMails: LiveData<MailState> = _allMails


     fun fetchAllMails() {
        viewModelScope.launch {
            repository.getAllMails { mailArrayList: ArrayList<Mail>, message: String ->

                if (mailArrayList.isNotEmpty()) {
                    val filteredMails = mailArrayList.filter { mail ->
                        mail.recipients.any { recipient ->
                            val isRecipient = recipient.userId == currentUserId
                            Log.d("FilteredMailsLog", "Mail ID: ${mail.id}, Recipient ID: ${recipient.userId}, Is Match: $isRecipient")
                            isRecipient
                        }
                    }
                    _allMails.value =
                        MailState(
                            mails = filteredMails as ArrayList<Mail>,
                            message = message,
                            isSuccessful = true
                        )
                } else {
                    Log.d("TAG", "fetchAllMails: No mails found.")
                    _allMails.value = MailState(message = message, isFailure = true)
                }
            }
        }
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