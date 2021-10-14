package com.eyeorders.screens.workhours

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eyeorders.util.extension.getString
import com.tasleem.orders.databinding.ItemWorkHoursBinding

class WorkHoursAdapter : ListAdapter<WorkHours,
        WorkHoursAdapter.WorkHoursViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkHoursViewHolder {
        return WorkHoursViewHolder(
            ItemWorkHoursBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WorkHoursViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WorkHoursViewHolder(
        private val binding: ItemWorkHoursBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WorkHours) {
            binding.dayTextView.text = getString(item.day)
            binding.workHoursTextView.text = item.hours
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<WorkHours>() {
            override fun areContentsTheSame(oldItem: WorkHours, newItem: WorkHours): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: WorkHours, newItem: WorkHours): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}