package com.devvikram.solaceinbox.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.devvikram.solaceinbox.R
import com.devvikram.solaceinbox.databinding.ItemNavMenuBinding
import com.devvikram.solaceinbox.model.NavItemModel

class NavMenuAdapter(
    private val context: Context,
    private val menuItems: List<NavItemModel>

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listener: NevMenuAdapterListener? = null
    private var selectedPosition: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemNavMenuBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MenuItemViewHolder(binding)
    }

    public fun setOnNevMenuAdapterListener(listener: NevMenuAdapterListener){
        this.listener = listener
    }

    public interface NevMenuAdapterListener {
        fun onMenuItemClicked(menuItem: NavItemModel)
    }

    override fun getItemCount(): Int {
        return menuItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val menuItem = menuItems[position]
        val menuItemViewHolder = holder as MenuItemViewHolder
        menuItemViewHolder.bind(menuItem,position)


    }

    fun updateSelection(title: String) {
        val previousSelectedPosition = selectedPosition
        selectedPosition = menuItems.indexOfFirst { it.title == title }

        notifyItemChanged(previousSelectedPosition)
        notifyItemChanged(selectedPosition)


    }

    inner class MenuItemViewHolder(val binding: ItemNavMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(menuItem: NavItemModel, position: Int) {
            binding.titleTextview.text = menuItem.title

            if (position == selectedPosition) {
                binding.root.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.light_purple)
                )
            } else {
                binding.root.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.white)
                )
            }

            // Handle item click
            binding.root.setOnClickListener {
                val previousSelectedPosition = selectedPosition
                selectedPosition = position

                notifyItemChanged(previousSelectedPosition)
                notifyItemChanged(selectedPosition)

                listener?.onMenuItemClicked(menuItem)
            }
        }
    }

}