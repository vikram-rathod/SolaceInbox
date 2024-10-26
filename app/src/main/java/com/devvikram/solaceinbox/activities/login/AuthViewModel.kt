package com.devvikram.solaceinbox.activities.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvikram.solaceinbox.constant.SharedPreference
import com.devvikram.solaceinbox.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository, context: Context) :
    ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val sharedPreference = SharedPreference(context)
    val currentUser = sharedPreference.getUser()

    //    register state
    private val _registerState = MutableLiveData<AuthState?>()
    val registerState: MutableLiveData<AuthState?> = _registerState

    //    login state
    private val _loginState = MutableLiveData<AuthState?>(null)
    val loginState: LiveData<AuthState?> = _loginState

    fun loginUser(userModel: UserModel) {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            authRepository.loginUser(userModel) { status: Boolean, message: String, userModel: UserModel ->
                if(status){
                    saveUser(userModel)
                    _loginState.value = AuthState.Success(userModel)
                }else{
                    _loginState.value = AuthState.Error(message)
                }
            }

        }
    }

    private fun saveUser(userModel: UserModel) {
        sharedPreference.saveData(userModel)
    }

    fun getUser(): UserModel {
        return sharedPreference.getUser()
    }


    fun registerUser(userModel: UserModel) {
        viewModelScope.launch {
            _registerState.value = AuthState.Loading
            authRepository.registerUser(userModel) { status: Boolean, message: String ->
                if (status) {
                    _registerState.value = AuthState.Success(userModel)
                } else {
                    _registerState.value = AuthState.Error(message)
                }
            }
        }

    }

    // Logout user function
    fun logoutUser() {
        firebaseAuth.signOut()
        clearUserData()
    }
    private fun resetState() {
        _registerState.value = null
        _loginState.value = null

    }

    private fun clearUserData() {
        sharedPreference.clearData()
        resetState()
    }


    sealed class AuthState(){
        data object Loading : AuthState()
        data class Success(val user: UserModel) : AuthState()
        data class Error(val message: String) : AuthState()
    }

}