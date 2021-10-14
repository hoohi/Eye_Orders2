package com.eyeorders.screens.activeorders.neworders

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eyeorders.util.extension.getString
import com.tasleem.orders.R
import com.tasleem.orders.databinding.ItemActiveNewOrderBinding

class NewOrdersAdapter(private val onItemClick: (NewOrder) -> Unit) :
    ListAdapter<NewOrder, NewOrdersAdapter.NewOrdersViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewOrdersViewHolder {
        return NewOrdersViewHolder(
            ItemActiveNewOrderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NewOrdersViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NewOrdersViewHolder(
        private val binding: ItemActiveNewOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(order: NewOrder) {
            binding.name.text = order.customerName
            binding.itemNumber.text = getString(R.string.order_number_label, order.orderNumber)
            binding.itemCount.text =
                getString(R.string.new_order_items, order.itemCount)
            binding.itemAmount.text = getString(R.string.amount_currency, order.price)
            binding.itemId.text = order.id.toString()

            binding.itemRoot.setOnClickListener {
                onItemClick.invoke(order)
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<NewOrder>() {
            override fun areContentsTheSame(oldItem: NewOrder, newItem: NewOrder): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: NewOrder, newItem: NewOrder): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}