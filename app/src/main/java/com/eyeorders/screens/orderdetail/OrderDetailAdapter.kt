package com.eyeorders.screens.orderdetail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eyeorders.screens.orderdetail.extra.OrderExtra
import com.eyeorders.screens.orderdetail.extra.OrderExtraAdapter
import com.eyeorders.screens.orderdetail.model.OrderProduct
import com.eyeorders.util.extension.beVisibleIf
import com.eyeorders.util.extension.getString
import com.tasleem.orders.R
import com.tasleem.orders.databinding.ItemOrderDetailBinding
import timber.log.Timber

class OrderDetailAdapter : ListAdapter<OrderProduct, OrderDetailAdapter.OrderProductViewHolder>(
        diffUtil
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductViewHolder {
        return OrderProductViewHolder(
                ItemOrderDetailBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                )
        )
    }

    override fun onBindViewHolder(holder: OrderProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderProductViewHolder(
            private val binding: ItemOrderDetailBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val adapter = OrderExtraAdapter()

        @SuppressLint("SetTextI18n")
        fun bind(order: OrderProduct) {
            binding.nameTextView.text = order.title
            binding.amountTextView.text = getString(R.string.amount_currency, order.productPrice)
            val amount = order.amount.toString()
            binding.countTextView.text = getString(R.string.order_count_label, amount)

            val options = order.options
            val addons = order.addons
            val exclusions = order.excluded

            val extras = mutableListOf<OrderExtra>()
            extras.addAll(options.map {
                OrderExtra(
                        getString(R.string.order_option_label, it.optionTitle.toString()),
                        it.optionPrice
                )
            })

            extras.addAll(addons.map {
                OrderExtra(
                        getString(R.string.order_addon_label, it.title),
                        it.price
                )
            })

            extras.addAll(exclusions.map {
                OrderExtra(
                        getString(R.string.order_exclude_label, it.title),
                        it.parameterPrice
                )
            })
            binding.extrasRecyclerView.adapter = adapter
            binding.extrasRecyclerView.beVisibleIf(extras.isNotEmpty())
            adapter.submitList(extras)
            Timber.d("Extras: ${order.addons}")

        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<OrderProduct>() {
            override fun areContentsTheSame(oldItem: OrderProduct, newItem: OrderProduct): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: OrderProduct, newItem: OrderProduct): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}