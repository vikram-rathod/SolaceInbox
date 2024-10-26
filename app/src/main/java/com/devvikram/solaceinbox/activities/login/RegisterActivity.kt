package com.devvikram.solaceinbox.activities.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.devvikram.solaceinbox.MainActivity
import com.devvikram.solaceinbox.constant.MyApplication
import com.devvikram.solaceinbox.databinding.ActivityRegisterBinding
import com.devvikram.solaceinbox.model.UserModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val authViewModel: AuthViewModel by lazy {
        (application as MyApplication).authViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.signupButton.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            val user = UserModel(name = name, email = email, password = password)
            authViewModel.registerUser(user)
        }
        authViewModel.registerState.observe(this) {
            when(it){
                is AuthViewModel.AuthState.Loading -> {
                    showProgressBar()
                }
                is AuthViewModel.AuthState.Success -> {
                    hideProgressBar()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is AuthViewModel.AuthState.Error -> {
                    hideProgressBar()
                    showErrorMessage(it.message)
                }
                null -> {
                    hideProgressBar()
                }
            }
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        binding.signupButton.visibility = View.GONE
        binding.errorMessageTextview.visibility = View.GONE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        binding.signupButton.visibility = View.VISIBLE
        binding.errorMessageTextview.visibility = View.GONE
    }

    private fun showErrorMessage(message: String) {
        binding.errorMessageTextview.text = message
        binding.errorMessageTextview.visibility = View.VISIBLE
    }
}