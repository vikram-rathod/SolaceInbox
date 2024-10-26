package com.devvikram.solaceinbox.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devvikram.solaceinbox.R
import com.devvikram.solaceinbox.ReplyEmailBottomSheetDialog
import com.devvikram.solaceinbox.databinding.ActivityEmailDetailBinding
import com.devvikram.solaceinbox.model.Mail
import com.devvikram.solaceinbox.utility.AppUtil

class EmailDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmailDetailBinding
    private var email: Mail? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.emailToolbar.setNavigationOnClickListener {
            finish()
        }
        email = intent.getParcelableExtra<Mail>("email")

        if (email != null) {
            binding.emailSubject.text = email!!.subject
            binding.emailBody.text = email!!.body
            binding.senderName.text = email!!.senderName
            binding.senderEmail.text = email!!.senderEmail

            binding.emailTimestamp.text = AppUtil.getTimeFromDate(email!!.cDate)
        } else {
            binding.emailSubject.text = "No subject"
            binding.emailBody.text = "No content available"
            binding.senderName.text = "Unknown sender"
            binding.emailTimestamp.text = "Unknown date"
        }

        binding.menuButton.setOnClickListener { view ->
            showPopupMenu(view)
        }
        binding.emailToolbar.setNavigationOnClickListener {
            finish()
        }

        handleReplyAtBottom()
    }


    private fun openBottomSheet() {
        val bottomSheetDialog = ReplyEmailBottomSheetDialog(this)
        bottomSheetDialog.show()
    }

    private fun handleReplyAtBottom() {
        binding.showReplyBottomSheetBtn.setOnClickListener {
            openBottomSheet()
        }

        binding.replyWithMessageTextview.setOnClickListener {
            Toast.makeText(this, "This feature soon available ", Toast.LENGTH_SHORT).show()
            if(email != null){
                binding.replyWithMessageTextview.text = email!!.senderName
                Log.d("TAG", "handleReplyAtBottom: ${email!!.senderEmail}")
                binding.remarkEdittextView.visibility = View.VISIBLE
                binding.expandeBtnToCompose.visibility = View.VISIBLE
            }

        }

    }

    private fun showPopupMenu(view: View?) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.email_details_option_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_reply -> {
                    // Handle Reply action
                    true
                }

                R.id.action_delete -> {
                    // Handle Delete action
                    true
                }

                R.id.action_mark_as_read -> {
                    // Handle Mark as Read action
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }


}