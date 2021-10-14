package com.eyeorders.screens.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eyeorders.util.extension.beVisibleIf
import com.tasleem.orders.databinding.ItemLoadStateBinding

class AppLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<AppLoadStateAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): AppLoadStateAdapter.LoadStateViewHolder {
        return LoadStateViewHolder(
            ItemLoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: AppLoadStateAdapter.LoadStateViewHolder,
        loadState: LoadState
    ) {
        holder.bind(loadState)
    }

    inner class LoadStateViewHolder(val binding: ItemLoadStateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.errorMsg.text = loadState.error.localizedMessage
            }

            binding.progressBar.beVisibleIf(loadState is LoadState.Loading)
            binding.retryButton.beVisibleIf(loadState !is LoadState.Loading)
            binding.errorMsg.beVisibleIf(loadState !is LoadState.Loading)

            binding.retryButton.setOnClickListener {
                retry.invoke()
            }
        }
    }
}