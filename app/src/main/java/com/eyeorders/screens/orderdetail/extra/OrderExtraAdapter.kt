package com.eyeorders.screens.orderdetail.extra

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eyeorders.util.extension.getString
import com.tasleem.orders.R
import com.tasleem.orders.databinding.ItemOrderExtraBinding

class OrderExtraAdapter : ListAdapter<OrderExtra, OrderExtraAdapter.OrderExtraViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderExtraViewHolder {
        return OrderExtraViewHolder(
            ItemOrderExtraBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OrderExtraViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderExtraViewHolder(
        private val binding: ItemOrderExtraBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(order: OrderExtra) {
            binding.nameTextView.text = order.title
            binding.amountTextView.text = getString(R.string.amount_currency, order.price)
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<OrderExtra>() {
            override fun areContentsTheSame(oldItem: OrderExtra, newItem: OrderExtra): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: OrderExtra, newItem: OrderExtra): Boolean {
                return oldItem == newItem
            }
        }
    }
}