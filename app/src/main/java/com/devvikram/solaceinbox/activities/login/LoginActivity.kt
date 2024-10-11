package com.devvikram.solaceinbox.activities.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.devvikram.solaceinbox.MainActivity
import com.devvikram.solaceinbox.constant.MyApplication
import com.devvikram.solaceinbox.databinding.ActivityLoginBinding
import com.devvikram.solaceinbox.model.UserModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by lazy {
        (application as MyApplication).authViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("TAG", "onCreate: ${authViewModel.isLoggedIn()}")
       if(authViewModel.isLoggedIn()){
           startActivity(Intent(this, MainActivity::class.java))
           finish()
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
            if (it.isLoading) {
                showProgress()
            } else if (it.isSuccessful) {
               hideProgress()
                startActivity(Intent(this, MainActivity::class.java))
            } else if (it.isFailure) {
                showErrorMessage(it.message)
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