package com.devvikram.solaceinbox.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devvikram.solaceinbox.databinding.ItemCategorizedEmailBinding
import com.devvikram.solaceinbox.model.CategorizedEmail

class CategoryEmailAdapter(
    private val activity: Activity,
    private val categorizedEmailList: List<CategorizedEmail>
) : RecyclerView.Adapter<CategoryEmailAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(private val binding: ItemCategorizedEmailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: CategorizedEmail) {
            binding.textViewCategory.text = category.category

            category.mails.forEachIndexed { index, mail ->
                Log.d(
                    "TAG", """
        Mail #$index:
        - ID: ${mail.id}
        - Sender ID: ${mail.senderId}
        - Sender Name: ${mail.senderName}
        - Subject: ${mail.subject}
        - Recipients: ${mail.recipients.joinToString { it.email }}  
        - Body: ${mail.body}
        - Date: ${mail.cDate}
    """.trimIndent()
                )
            }
            val emailAdapter = EmailAdapter(activity, category.mails)
            binding.recyclerViewEmailsInCategory.adapter = emailAdapter

            binding.recyclerViewEmailsInCategory.layoutManager = LinearLayoutManager(activity)
            binding.recyclerViewEmailsInCategory.addItemDecoration(
                DividerItemDecoration(
                    activity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            ItemCategorizedEmailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categorizedEmailList[position])

    }

    override fun getItemCount(): Int {
        return categorizedEmailList.size
    }


}
