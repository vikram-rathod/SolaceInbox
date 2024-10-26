package com.devvikram.solaceinbox.activities.addcompose

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.devvikram.solaceinbox.R
import com.devvikram.solaceinbox.adapter.AttachmentAdapter
import com.devvikram.solaceinbox.common.SharedViewModel
import com.devvikram.solaceinbox.constant.MyApplication
import com.devvikram.solaceinbox.databinding.ActivityAddNewMailBinding
import com.devvikram.solaceinbox.model.Attachment
import com.devvikram.solaceinbox.model.Mail
import com.devvikram.solaceinbox.model.UserModel
import com.google.android.material.chip.Chip
import java.util.UUID

class AddNewMailActivity : AppCompatActivity() {
    private lateinit var  binding : ActivityAddNewMailBinding

    private val addComposeMailViewModel: AddComposeMailViewmodel by lazy {
        (application as MyApplication).addComposeMailViewModel
    }
    private val sharedViewModel: SharedViewModel by viewModels()

    private val attachmentList = mutableListOf<Attachment>()
    private lateinit var attachmentAdapter: AttachmentAdapter

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            uris.let {
                for (uri in it) {
                    handleFileSelection(uri)
                }
            }
        }

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
                                    binding.toInput.text?.clear()
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
        setupRecyclerView()

        configureOptionMenu()

        binding.sendButton.setOnClickListener {
            val subject = binding.subjectInput.text.toString()
            val body = binding.messageInput.text.toString()

            val mail = Mail(
                subject = subject,
                body = body,
                recipients = selectedUsers,
                attachments = attachmentList
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

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView() {
        attachmentAdapter = AttachmentAdapter(this, attachmentList) { attachment ->
            attachmentList.remove(attachment)
            attachmentAdapter.notifyDataSetChanged()
        }
        binding.attachmentsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.attachmentsRecyclerView.adapter = attachmentAdapter
    }

    private fun configureOptionMenu() {
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_save_draft -> {
                    Toast.makeText(this, "Save Draft", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.action_attach_file -> {
                    openFilePicker()
                    Toast.makeText(this, "Attach File", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }
    }

    private fun openFilePicker() {
        filePickerLauncher.launch(arrayOf("*/*"))
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleFileSelection(fileUri: Uri) {
        val fileName = getFileName(fileUri)
        val fileSize = getFileSize(fileUri)
        val fileType = contentResolver.getType(fileUri) ?: "application/octet-stream"
        val id = UUID.randomUUID().toString()

        val attachment = Attachment(
            id = id,
            fileName = fileName,
            filePath = fileUri.toString(),
            fileSize = fileSize,
            fileType = fileType
        )

        if (attachmentList.none { it.fileName == fileName }) {
            attachmentList.add(attachment)
            attachmentAdapter.notifyDataSetChanged()
        } else {
            Toast.makeText(this, "You cannot add a duplicate file.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileName(uri: Uri): String {
        var name = ""
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst()) {
                name = it.getString(nameIndex)
            }
        }
        return name
    }

    private fun getFileSize(uri: Uri): Long {
        var size = 0L
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
            if (it.moveToFirst()) {
                size = it.getLong(sizeIndex)
            }
        }
        return size
    }


}
