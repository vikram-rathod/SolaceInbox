package com.devvikram.solaceinbox.activities.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvikram.solaceinbox.constant.SharedPreference
import com.devvikram.solaceinbox.model.UserModel
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository, context: Context) :
    ViewModel() {

    private val sharedPreference = SharedPreference(context)
    val currentUser = sharedPreference.getUser()

    //    register state
    private val _registerState = MutableLiveData<AuthState>()
    val registerState: LiveData<AuthState> = _registerState

    //    login state
    private val _loginState = MutableLiveData<AuthState>()
    val loginState: LiveData<AuthState> = _loginState

    //    logout state
    private val _logoutState = MutableLiveData<AuthState>()
    val logoutState: LiveData<AuthState> = _logoutState

    fun loginUser(userModel: UserModel) {
        viewModelScope.launch {
            _loginState.value = AuthState(isLoading = true)
            authRepository.loginUser(userModel) { status: Boolean, message: String, userModel: UserModel ->
                if (status) {
                    saveUser(userModel)
                    _loginState.value =
                        AuthState(
                            status = true,
                            isLoggedIn = true,
                            user = userModel,
                            isSuccessful = true
                        )

                } else {
                    _loginState.value =
                        AuthState(status = false, message = message, isFailure = true)
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

    fun isLoggedIn(): Boolean {
        return sharedPreference.isLoggedIn()
    }

    fun registerUser(userModel: UserModel) {
        viewModelScope.launch {
            _registerState.value = AuthState(isLoading = true)
            authRepository.registerUser(userModel) { status: Boolean, message: String ->
                if (status) {
                    _registerState.value =
                        AuthState(status = true, message = message, isSuccessful = true)
                } else {
                    _registerState.value =
                        AuthState(status = false, message = message, isFailure = true)
                }
            }
        }

    }

    fun logoutUser() {
        viewModelScope.launch {
            authRepository.logoutUser { status: Boolean, message: String ->
                sharedPreference.clear()
                _logoutState.value = AuthState(isLoggedIn = false, isSuccessful = true)
            }
        }
    }


    data class AuthState(
        val isLoading: Boolean = false,
        val status: Boolean = false,
        val message: String = "",
        val isSuccessful: Boolean = false,
        val isFailure: Boolean = false,
        val user: UserModel? = UserModel(),
        val isLoggedIn: Boolean = false
    )
}