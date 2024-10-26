package com.devvikram.solaceinbox.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devvikram.solaceinbox.R
import com.devvikram.solaceinbox.activities.EmailDetailActivity
import com.devvikram.solaceinbox.databinding.ItemEmaiDraftLayoutBinding
import com.devvikram.solaceinbox.databinding.ItemEmaiSentLayoutBinding
import com.devvikram.solaceinbox.databinding.ItemEmailLayoutBinding
import com.devvikram.solaceinbox.model.Mail
import com.devvikram.solaceinbox.utility.AppUtil

class EmailAdapter(private val activity: Activity, private val emailList: MutableList<Mail>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class InboxViewHolder(
        private val binding: ItemEmailLayoutBinding,
        private val activity: Activity
    ) : BaseEmailViewHolder(binding.root) {
        override fun bind(email: Mail) {
            Log.d("TAG", "InboxEmailBind: ${email.senderName}")
            binding.tvSender.text = email.senderName
            binding.tvSubject.text = email.subject
            binding.tvBodySnippet.text = email.body
            binding.tvTimestamp.text = AppUtil.getTimeFromDate(email.cDate)

            loadProfileImage(binding, email)

            binding.root.setOnClickListener {
                startEmailDetailActivity(email)
            }
        }

        private fun loadProfileImage(binding: ItemEmailLayoutBinding, email: Mail) {
            val profileUrl = ""
            if (profileUrl.isEmpty()) {
                binding.profileImage.visibility = View.GONE
                binding.noProfileLayout.visibility = View.VISIBLE
                binding.firstLetterTextView.text = email.senderName.firstOrNull()?.toString() ?: "?"
                binding.subCatIcon.setBackgroundColor(
                    ContextCompat.getColor(activity, R.color.purple_light_2)
                )
            } else {
                binding.profileImage.visibility = View.VISIBLE
                binding.noProfileLayout.visibility = View.GONE
                Glide.with(activity)
                    .load(profileUrl)
                    .placeholder(R.drawable.baseline_person_24)
                    .into(binding.profileImage)
            }
        }

        private fun startEmailDetailActivity(email: Mail) {
            activity.startActivity(Intent(activity, EmailDetailActivity::class.java).apply {
                putExtra("email", email)
            })
        }
    }

    // ViewHolder for Sent
    class SentViewHolder(
        private val binding: ItemEmaiSentLayoutBinding,
        private val activity: Activity
    ) : BaseEmailViewHolder(binding.root) {
        override fun bind(email: Mail) {
            Log.d("TAG", "SentEmailBind: ${email.senderName}")
            binding.tvRecipient.text = email.recipients[0].email
            binding.tvSubject.text = email.subject
            binding.tvTimestamp.text = AppUtil.getTimeFromDate(email.cDate)

            binding.root.setOnClickListener {
                startEmailDetailActivity(email)
            }
        }

        private fun startEmailDetailActivity(email: Mail) {
            activity.startActivity(Intent(activity, EmailDetailActivity::class.java).apply {
                putExtra("email", email)
            })
        }
    }

    // ViewHolder for Draft
    class DraftViewHolder(
        private val binding: ItemEmaiDraftLayoutBinding,
        private val activity: Activity
    ) : BaseEmailViewHolder(binding.root) {
        override fun bind(email: Mail) {
            Log.d("TAG", "DraftEmailBind: ${email.senderName}")
            binding.tvSubject.text = email.subject.ifEmpty { "(no subject)" }
            binding.tvTimestamp.text = AppUtil.getTimeFromDate(email.cDate)

            binding.root.setOnClickListener {
                startEmailDetailActivity(email)
            }
        }

        private fun startEmailDetailActivity(email: Mail) {
            activity.startActivity(Intent(activity, EmailDetailActivity::class.java).apply {
                putExtra("email", email)
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            INBOX_TYPE -> {
                val binding = ItemEmailLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                InboxViewHolder(binding, activity)
            }

            SENT_TYPE -> {
                val binding = ItemEmaiSentLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                SentViewHolder(binding, activity)
            }

            DRAFT_TYPE -> {
                val binding = ItemEmaiDraftLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                DraftViewHolder(binding, activity)
            }

            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val email = emailList[position]
        when (holder) {
            is InboxViewHolder -> holder.bind(email)
            is SentViewHolder -> holder.bind(email)
            is DraftViewHolder -> holder.bind(email)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (emailList[position].type) {
            "0" -> INBOX_TYPE
            "1" -> SENT_TYPE
            "2" -> DRAFT_TYPE
            else -> throw IllegalArgumentException("Unknown email type")
        }
    }


    override fun getItemCount(): Int {
        return emailList.size
    }


    abstract class BaseEmailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(email: Mail)
    }

    companion object {
        const val INBOX_TYPE = 0
        const val SENT_TYPE = 1
        const val DRAFT_TYPE = 2
    }
}
