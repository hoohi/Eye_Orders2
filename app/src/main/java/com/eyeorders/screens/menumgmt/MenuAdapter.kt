package com.eyeorders.screens.menumgmt

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.eyeorders.screens.menumgmt.menu.MenuProduct
import com.tasleem.orders.R
import com.tasleem.orders.databinding.ItemMenuBinding

class MenuAdapter(
    private val onItemClick: (MenuProduct) -> Unit,
) : PagingDataAdapter<MenuProduct,
        MenuAdapter.MenuViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        return MenuViewHolder(
            ItemMenuBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class MenuViewHolder(
        private val binding: ItemMenuBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MenuProduct) {
            binding.nameTextView.text = item.title
            if (item.isAvailable) {
                binding.statusTextView.text =
                    binding.statusTextView.context.getString(R.string.product_available_text)
                binding.statusTextView.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        binding.statusTextView.context,
                        R.color.green_400
                    )
                )
            } else {
                binding.statusTextView.text =
                    binding.statusTextView.context.getString(R.string.product_unavailable_text)
                binding.statusTextView.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        binding.statusTextView.context,
                        R.color.red
                    )
                )
            }

            binding.statusTextView.setOnClickListener {
                onItemClick.invoke(item)
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<MenuProduct>() {
            override fun areContentsTheSame(oldItem: MenuProduct, newItem: MenuProduct): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: MenuProduct, newItem: MenuProduct): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}