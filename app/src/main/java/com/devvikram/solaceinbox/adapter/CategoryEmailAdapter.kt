package com.devvikram.solaceinbox.adapter

import android.annotation.SuppressLint
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
    private val categorizedEmailList: ArrayList<CategorizedEmail>
) : RecyclerView.Adapter<CategoryEmailAdapter.CategoryViewHolder>() {
    private var emailAdapter: EmailAdapter? = null


    inner class CategoryViewHolder(private val binding: ItemCategorizedEmailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bind(category: CategorizedEmail) {
            binding.textViewCategory.text = category.category
            Log.d("TAG", "Caetksdjfl: ${category.mails.size}")

                binding.recyclerViewEmailsInCategory.layoutManager = LinearLayoutManager(activity)
                binding.recyclerViewEmailsInCategory.addItemDecoration(
                    DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
                )
                emailAdapter = EmailAdapter(activity, category.mails.toMutableList())
                binding.recyclerViewEmailsInCategory.adapter = emailAdapter
                emailAdapter?.notifyDataSetChanged()

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

    fun updateData(categorized: ArrayList<CategorizedEmail>) {
        categorizedEmailList.clear()
        categorizedEmailList.addAll(categorized)
        notifyDataSetChanged()
    }


}
