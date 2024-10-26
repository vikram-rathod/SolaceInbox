package com.devvikram.solaceinbox.activities.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.devvikram.solaceinbox.MainActivity
import com.devvikram.solaceinbox.constant.MyApplication
import com.devvikram.solaceinbox.constant.SharedPreference
import com.devvikram.solaceinbox.databinding.ActivityLoginBinding
import com.devvikram.solaceinbox.model.UserModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by lazy {
        (application as MyApplication).authViewModel
    }
    private lateinit var sharePreferences: SharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharePreferences = SharedPreference(this)
        Log.d("TAG", "onCreate: logged in status ${sharePreferences.isLoggedIn()}")
       if(sharePreferences.isLoggedIn()){
           startActivity(Intent(this, MainActivity::class.java))
           finishAffinity()
           return
       }
        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                showErrorMessage("Please enter both email and password")
                return@setOnClickListener
            }
            val userModel = UserModel(email = email, password = password)
            authViewModel.loginUser(userModel)
        }

        authViewModel.loginState.observe(this) {
            when (it) {
                is AuthViewModel.AuthState.Loading -> {
                    showProgress()
                }
                is AuthViewModel.AuthState.Success -> {
                    hideProgress()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()

                }
                is AuthViewModel.AuthState.Error -> {
                    hideProgress()
                    showErrorMessage(it.message)
                }
                null -> {
                    hideProgress()
                }
            }
        }

        binding.signupLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
    private fun showProgress(){
        binding.progressBar.visibility = View.VISIBLE
        binding.errorMessageTextview.visibility = View.GONE
    }
    private fun hideProgress(){
        binding.progressBar.visibility = View.GONE
        binding.errorMessageTextview.visibility = View.GONE
    }
    private fun showErrorMessage(message: String){
        binding.progressBar.visibility = View.GONE
        binding.errorMessageTextview.visibility = View.VISIBLE
        binding.errorMessageTextview.text = message
    }
}