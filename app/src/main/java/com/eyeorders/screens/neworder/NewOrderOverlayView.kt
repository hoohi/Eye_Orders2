package com.eyeorders.screens.neworder

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.tasleem.orders.R
import com.tasleem.orders.databinding.LayoutNewOrderBinding

@SuppressLint("ViewConstructor")
class NewOrderOverlayView(context: Context) : ConstraintLayout(context) {

    private val binding = LayoutNewOrderBinding.inflate(LayoutInflater.from(context), this)

    private lateinit var newOrderItem: NewOrderItem

    private var onClick: (NewOrderItem) -> Unit = {}

    init {
        binding.root.setOnClickListener {
            onClick.invoke(newOrderItem)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        binding.root.setBackgroundResource(R.drawable.background_new_order)
        initView()
    }

    private fun initView() {
//        binding.countTextView.text = newOrderItem.orderCount.toString()
    }

    fun setNewOrder(newOrder: NewOrderItem) {
        this.newOrderItem = newOrder
    }

    fun onClick(callback: (NewOrderItem) -> Unit) {
        onClick = callback
    }
}