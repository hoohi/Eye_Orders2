package com.eyeorders.screens.orderdetail.tax

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eyeorders.screens.orderdetail.model.OrderTax
import com.eyeorders.util.extension.getString
import com.tasleem.orders.R
import com.tasleem.orders.databinding.ItemTaxBinding

class OrderTaxAdapter : ListAdapter<OrderTax, OrderTaxAdapter.OrderTaxViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderTaxViewHolder {
        return OrderTaxViewHolder(
            ItemTaxBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OrderTaxViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderTaxViewHolder(
        private val binding: ItemTaxBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(tax: OrderTax) {
            binding.taxLabelTextView.text =
                getString(R.string.tax_text, tax.taxName, tax.taxPercentage)
            binding.taxTextView.text = getString(R.string.amount_currency, tax.taxPrice)
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<OrderTax>() {
            override fun areContentsTheSame(oldItem: OrderTax, newItem: OrderTax): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: OrderTax, newItem: OrderTax): Boolean {
                return oldItem == newItem
            }
        }
    }
}