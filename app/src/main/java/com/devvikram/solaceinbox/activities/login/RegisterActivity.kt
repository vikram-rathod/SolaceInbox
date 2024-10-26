package com.devvikram.solaceinbox.activities.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
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


        binding.passwordInput.addTextChangedListener {
            if (it.toString().length < 6) {
                binding.passwordInputLayout.error =
                    "Password should be greater then equal to 6 digits"
            } else {
                binding.passwordInputLayout.error = null
            }
        }
        binding.emailInput.addTextChangedListener {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(it.toString()).matches()) {
                binding.emailInputLayout.error = "Email address is not valid"
            } else {
                binding.emailInputLayout.error = null
            }
        }

        binding.signupButton.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            val user = UserModel(name = name, email = email, password = password)

            if (name.isEmpty()) {
                binding.nameInputLayout.error = "Name cannot be empty"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.emailInputLayout.error = "Email cannot be empty"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.passwordInputLayout.error = "Password cannot be empty"
                return@setOnClickListener
            }

            authViewModel.registerUser(user)
        }
        authViewModel.registerState.observe(this) {
            when(it){
                is AuthViewModel.AuthState.Loading -> {
                    showProgressBar()
                }
                is AuthViewModel.AuthState.Success -> {
                    hideProgressBar()
                    showSuccession()
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
        binding.loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun showSuccession() {
        //full screen
        val dialog = AlertDialog.Builder(this)
        dialog.setCancelable(false)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialog.setTitle("Registration Successful")
        dialog.setMessage("You have successfully registered. Please login.")
        dialog.setPositiveButton("Login to account") { _, _ ->
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
        dialog.show()

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