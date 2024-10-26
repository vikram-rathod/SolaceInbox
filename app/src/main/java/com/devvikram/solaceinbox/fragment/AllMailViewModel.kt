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
                // Log the complete list of emails before filtering
                Log.d("TAG", "fetchAllMails: Unfiltered mail list: $mailArrayList")
                Log.d("TAG", "fetchAllMails: Unfiltered mail count: ${mailArrayList.size}")

                if (mailArrayList.isNotEmpty()) {
                    // Log the current user ID
                    Log.d("TAG", "Current User ID: $currentUserId")

                    val filteredMails = mailArrayList.filter { mail ->
                        // Log the mail ID and its recipients
                        Log.d("TAG", "Checking Mail ID: ${mail.id} with Recipients: ${mail.recipients.joinToString { it.userId }}")

                        // Check if any recipient matches the current user ID
                        mail.recipients.any { recipient ->
                            val isRecipient = recipient.userId == currentUserId
                            Log.d("FilteredMailsLog", "Mail ID: ${mail.id}, Recipient ID: ${recipient.userId}, Is Match: $isRecipient")
                            isRecipient
                        }
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