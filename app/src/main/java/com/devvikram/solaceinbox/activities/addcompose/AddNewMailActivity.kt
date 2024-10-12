package com.devvikram.solaceinbox.activities.addcompose

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.devvikram.solaceinbox.common.SharedViewModel
import com.devvikram.solaceinbox.constant.MyApplication
import com.devvikram.solaceinbox.databinding.ActivityAddNewMailBinding
import com.devvikram.solaceinbox.model.Mail
import com.devvikram.solaceinbox.model.UserModel
import com.google.android.material.chip.Chip

class AddNewMailActivity : AppCompatActivity() {
    private lateinit var  binding : ActivityAddNewMailBinding

    private val addComposeMailViewModel: AddComposeMailViewmodel by lazy {
        (application as MyApplication).addComposeMailViewModel
    }
    private val sharedViewModel: SharedViewModel by viewModels()


    private var userList: ArrayList<UserModel> = ArrayList()
    private var selectedUsers: ArrayList<UserModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewMailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backIcon.setOnClickListener {
            finish()
        }
        addComposeMailViewModel.userState.observe(this) {
            userList = it.usersList
            if (it.isLoading) {
                Toast.makeText(this, "Loading Users...", Toast.LENGTH_SHORT).show()
            } else if (it.isSuccessful) {
                Toast.makeText(this, "Users fetched successfully", Toast.LENGTH_SHORT).show()

                binding.toInput.addTextChangedListener(
                    afterTextChanged = {},
                    beforeTextChanged = { text, start, count, after ->
                    },
                    onTextChanged = { text, start, before, count ->
                        val userNames = text.toString().split(" ")
                        userNames.forEach { userName ->
                            val user = userList.find { it.email == userName }
                            if (user != null) {
                                if (!selectedUsers.contains(user)) {
                                    selectedUsers.add(user)
                                    val chip = Chip(this)
                                    chip.text = user.email
                                    chip.isCheckable = true
                                    chip.isCloseIconVisible = true
                                    chip.setOnCloseIconClickListener {
                                        selectedUsers.remove(user)
                                        binding.recipientChipGroup.removeView(chip)
                                    }
                                    binding.recipientChipGroup.addView(chip)
                                }
                            }
                        }
                        if (selectedUsers.isEmpty()) {
                            binding.toInput.hint = "Select recipients"
                        } else {
                            binding.toInput.hint = ""
                        }

                    }


                )
            } else if (it.isFailure) {
                Toast.makeText(this, "Error: ${it.errorMessage}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.sendButton.setOnClickListener {
            val subject = binding.subjectInput.text.toString()
            val body = binding.messageInput.text.toString()

            val mail = Mail(
                subject = subject,
                body = body,
                recipients = selectedUsers
            )
            Log.d("AllNewMainActivityLog", "onCreate: $mail")
            addComposeMailViewModel.sendComposeMail(mail)
            addComposeMailViewModel.composeMailState.observe(this) { state ->
                if (state.isLoading) {
                    Toast.makeText(this, "Sending Mail...", Toast.LENGTH_SHORT).show()
                } else if (state.isSuccessful) {
                    Toast.makeText(this, "Mail Sent Successfully", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    sharedViewModel.setRefreshFlag(true)
                    finish()
                } else if (state.isFailure) {
                    Toast.makeText(this, "Error: ${state.errorMessage}", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }
}
