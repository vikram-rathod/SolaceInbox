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

    private fun fetchAllMails() {
        viewModelScope.launch {
            repository.getAllMails() { mailArrayList: ArrayList<Mail>, message: String ->
                // Log the complete list of emails before filtering
                Log.d("TAG", "fetchAllMails: Unfiltered mail list: $mailArrayList")
                Log.d("TAG", "fetchAllMails: Unfiltered mail count: ${mailArrayList.size}")

                if (mailArrayList.isNotEmpty()) {
                    val filteredMails = mailArrayList.filter { mail ->
                        mail.recipients.any { recipient -> recipient.userId == currentUserId }
                    }

                    // Log the filtered list of emails
                    Log.d("TAG", "fetchAllMails: Filtered mail list: $filteredMails")
                    Log.d("TAG", "fetchAllMails: Filtered mail count: ${filteredMails.size}")

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