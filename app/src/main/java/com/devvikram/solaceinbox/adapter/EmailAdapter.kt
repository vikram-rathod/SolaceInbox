package com.devvikram.solaceinbox.adapter

import android.annotation.SuppressLint
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
import com.devvikram.solaceinbox.databinding.ItemEmailLayoutBinding
import com.devvikram.solaceinbox.model.Mail
import com.devvikram.solaceinbox.utility.AppUtil
import java.util.ArrayList

class EmailAdapter(private val activity: Activity, private val emailList: MutableList<Mail>) :
    RecyclerView.Adapter<EmailAdapter.EmailViewHolder>() {

    class EmailViewHolder(val binding: ItemEmailLayoutBinding, private val activity: Activity) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(email: Mail) {

            Log.d("TAG", "EmailBind: ${email.senderName} to ${email.senderId}, ${email.subject}")

            binding.tvSender.text = email.senderName
            binding.tvTimestamp.text = AppUtil.getTimeFromDate(email.cDate)

            when {
                AppUtil.isToday(email.cDate) -> {
                    Log.d("EmailTimestamp", "Email is from today: ${email.cDate}")/**/

                    binding.tvTimestamp.visibility = View.GONE
                    binding.tvTimestampText.text = AppUtil.getTimeAgoString(email.cDate)
                }

                AppUtil.isYesterday(email.cDate) -> {
                    Log.d("EmailTimestamp", "Email is from yesterday: ${email.cDate}")

                    binding.tvTimestamp.text = AppUtil.getDateInDmy(email.cDate)
                    binding.tvTimestampText.visibility = View.GONE
                }

                else -> {
                    Log.d("EmailTimestamp", "Email is older: ${email.cDate}")

                    binding.tvTimestamp.visibility = View.GONE
                    binding.tvTimestampText.text = AppUtil.getDateInDmy(email.cDate)
                }
            }

            binding.tvBodySnippet.maxLines = 2
            binding.tvSubject.text = email.subject.ifEmpty { "(no subject)" }
            binding.tvBodySnippet.text = email.body.ifEmpty { "" }
            binding.tvBodySnippet.visibility =
                if (email.body.isEmpty()) View.GONE else View.VISIBLE
            val profileUrl = ""

            if (profileUrl.isEmpty()) {
                binding.profileImage.visibility = View.GONE
                binding.noProfileLayout.visibility = View.VISIBLE
                val firstLetter = if (email.senderName.isNotEmpty()) {
                    email.senderName.first().toString()
                } else {
                    "?"
                }
                binding.firstLetterTextView.text = firstLetter

                val colors = arrayOf(
                    R.color.purple_light_2,
                    R.color.yellow_light_2,
                    R.color.teal_light_2,
                    R.color.cyan_light_2,
                    R.color.pink_light_2,
                    R.color.brown_light_2
                )
                val randomColor = colors.random()
                binding.subCatIcon.setBackgroundColor(ContextCompat.getColor(activity, randomColor))

            } else {
                binding.profileImage.visibility = View.VISIBLE
                binding.noProfileLayout.visibility = View.GONE
                Glide.with(activity)
                    .load("https://images.pexels.com/photos/771742/pexels-photo-771742.jpeg")
                    .placeholder(R.drawable.baseline_person_24).into(binding.profileImage)
//
            }


            itemView.setOnClickListener {
                activity.startActivity(Intent(activity, EmailDetailActivity::class.java).apply {
                    putExtra("email", email)
                })
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {

        val binding = ItemEmailLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EmailViewHolder(binding, activity)


    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {
        val email = emailList[position]
        holder.bind(email)
    }


    override fun getItemCount(): Int {
        return emailList.size
    }

    fun updateEmails(mails: ArrayList<Mail>) {
        emailList.clear()
        emailList.addAll(mails)
        notifyDataSetChanged()
    }

}
