package com.devvikram.solaceinbox.activities.addcompose

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvikram.solaceinbox.constant.SharedPreference
import com.devvikram.solaceinbox.model.Mail
import com.devvikram.solaceinbox.model.UserModel
import kotlinx.coroutines.launch
import java.util.Date

class AddComposeMailViewmodel(
    private val repository: AddComposeMailRepository,
    private val application: Application
) : ViewModel() {

    private val sharedPreference = SharedPreference(application)

    private val _composeMailState = MutableLiveData<ComposeMailState>()
    val composeMailState: LiveData<ComposeMailState> = _composeMailState


    private val _userState = MutableLiveData<UserState>()
    val userState: LiveData<UserState> = _userState


    init {
        getUsers()
    }

    private fun getUsers() {
        viewModelScope.launch {
            _userState.value = UserState(isLoading = true)
            repository.getUsers { status: Boolean, message: String?, users: ArrayList<UserModel>? ->
                if (status) {
                    _userState.value = users?.let { UserState(isSuccessful = true, usersList = it) }
                } else {
                    _userState.value = UserState(isFailure = true, errorMessage = message)
                }
            }

        }
    }

    fun sendComposeMail(mail: Mail) {
        mail.cDate = Date().toString()
        mail.senderName = sharedPreference.getUserName()
        mail.senderEmail = sharedPreference.getUserEmail()
        viewModelScope.launch {
            _composeMailState.value = ComposeMailState(isLoading = true)

            val attachmentUrls = mutableListOf<String>()
            for (attachment in mail.attachments) {
                val downloadUrl = repository.uploadAttachment(Uri.parse(attachment.filePath), attachment.fileName)
                if (downloadUrl != null) {
                    attachmentUrls.add(downloadUrl)
                }
            }

            // Update the mail object with the attachment URLs
            mail.attachments.forEachIndexed { index, attachment ->
                attachment.downloadUrl = attachmentUrls.getOrNull(index) ?: ""
            }

            // Now add the mail to Firestore
            val status = repository.addComposeMail(mail)
            if (status) {
                _composeMailState.value = ComposeMailState(isSuccessful = true)
            } else {
                _composeMailState.value = ComposeMailState(isFailure = true, errorMessage = "Failed to send mail")
            }

        }

    }

    data class ComposeMailState(
        val isLoading: Boolean = false,
        val isSuccessful: Boolean = false,
        val isFailure: Boolean = false,
        val errorMessage: String? = ""
    )


    data class UserState(
        val isLoading: Boolean = false,
        val isSuccessful: Boolean = false,
        val isFailure: Boolean = false,
        val errorMessage: String? = "",
        val usersList: ArrayList<UserModel> = ArrayList()
    )


}
