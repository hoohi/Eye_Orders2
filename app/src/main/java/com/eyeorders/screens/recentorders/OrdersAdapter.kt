package com.eyeorders.screens.recentorders

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eyeorders.screens.recentorders.data.RecentOrder
import com.eyeorders.util.imageloader.ImageLoader
import com.tasleem.orders.databinding.ItemRecentOrderBinding
import timber.log.Timber

class OrdersAdapter(
    private val imageLoader: ImageLoader,
    private val onItemClick: (RecentOrder) -> Unit,
) : ListAdapter<RecentOrder,
        OrdersAdapter.OrdersViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(
            ItemRecentOrderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getHeaderForCurrentPosition(topChildPosition: Int): String {
        Timber.d("Top child position: $topChildPosition")
        return "All - 67 ( 10:00 AM - 12:00 PM )"
    }

    inner class OrdersViewHolder(
        private val binding: ItemRecentOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(order: RecentOrder) {
            binding.itemIdTextView.text = order.id.toString()
            binding.itemNumberTextView.text = "#${order.orderNumber}"
            binding.timeTextView.text = order.dateTime
            binding.nameTextView.text = order.username
            binding.statusTextView.text = order.status
            imageLoader.load(order.userImageUrl, binding.pictureImageView)
            binding.root.setOnClickListener {
                onItemClick.invoke(order)
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<RecentOrder>() {
            override fun areContentsTheSame(oldItem: RecentOrder, newItem: RecentOrder): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: RecentOrder, newItem: RecentOrder): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}