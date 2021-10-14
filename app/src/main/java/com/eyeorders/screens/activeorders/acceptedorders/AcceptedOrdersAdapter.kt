package com.eyeorders.screens.activeorders.acceptedorders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.eyeorders.util.extension.getString
import com.tasleem.orders.R
import com.tasleem.orders.databinding.ItemActiveAcceptedOrderBinding
import timber.log.Timber

class AcceptedOrdersAdapter(
    private val onItemClick: (AcceptedOrder) -> Unit,
) : PagingDataAdapter<AcceptedOrder,
        AcceptedOrdersAdapter.AcceptedOrdersViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcceptedOrdersViewHolder {
        return AcceptedOrdersViewHolder(
            ItemActiveAcceptedOrderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AcceptedOrdersViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class AcceptedOrdersViewHolder(
        private val binding: ItemActiveAcceptedOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: AcceptedOrder) {
            Timber.d("ORDER: $order")
            binding.itemNumberTextView.text = binding.itemNumberTextView.context.getString(
                R.string.order_number_label,
                order.orderNumber
            )
            binding.itemIdTextView.text =
                getString(R.string.item_id_label, order.id.toString(), order.orderCount.toString())
            binding.timeTextView.text = order.orderDate
            binding.nameTextView.text = order.customerName
            binding.driverNameTextView.text = order.driverName

            binding.actionButton.setOnClickListener {
                onItemClick.invoke(order)
            }

            binding.root.setOnClickListener {
                onItemClick.invoke(order)
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<AcceptedOrder>() {
            override fun areContentsTheSame(
                oldItem: AcceptedOrder,
                newItem: AcceptedOrder
            ): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: AcceptedOrder, newItem: AcceptedOrder): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}