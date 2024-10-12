package com.devvikram.solaceinbox.activities.addcompose

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvikram.solaceinbox.constant.SharedPreference
import com.devvikram.solaceinbox.model.Mail
import com.devvikram.solaceinbox.model.UserModel
import kotlinx.coroutines.launch

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
  viewModelScope.launch {
   _composeMailState.value = ComposeMailState(isLoading = true)

   repository.addComposeMail(mail,sharedPreference.getUserName().toString()) { status: Boolean, message: String? ->
    if (status) {
     _composeMailState.value = ComposeMailState(isSuccessful = true)
    } else {
     _composeMailState.value = ComposeMailState(isFailure = true, errorMessage = message)
    }
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
