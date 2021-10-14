package com.eyeorders.screens.recentorders.today

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.eyeorders.navigator.Navigator
import com.eyeorders.screens.recentorders.OrdersAdapter
import com.eyeorders.util.extension.beVisibleIf
import com.eyeorders.util.imageloader.ImageLoader
import com.eyeorders.util.imageloader.PlaceHolderDrawable
import com.eyeorders.util.text.getSpannableScaleText
import com.eyeorders.util.viewbinding.viewBinding
import com.tasleem.orders.R
import com.tasleem.orders.databinding.FragmentTodayOrdersBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TodayOrdersFragment : Fragment(R.layout.fragment_today_orders) {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var imageLoader: ImageLoader

    private val binding by viewBinding(FragmentTodayOrdersBinding::bind)
    private val viewModel: TodayOrdersViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = OrdersAdapter(imageLoader) {
            navigator.toOrderDetail(it.id)
        }

        binding.retryBtn.setOnClickListener {
            viewModel.refresh()
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.setEmptyView(binding.emptyGroup)
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        )

        viewModel.viewState.observe(viewLifecycleOwner) {
            Timber.d("STATE: $it")

            if (it.loading) {
                binding.emptyText.text = getString(R.string.loading)
            } else {
                binding.emptyText.text = getString(R.string.empty_orders_today)
            }

            if (it.errorMessage != null) {
                binding.emptyText.text = it.errorMessage
            }

            if (it.loading) {
                binding.emptyImg.setImageDrawable(PlaceHolderDrawable(requireContext()))
            } else {
                binding.emptyImg.setImageResource(R.drawable.ic_recent_orders)
            }

            val completedOrders = getString(
                R.string.orders_label,
                it.recentOrders.completedOrders
            )
            binding.completeOrdersCountTextView.text = getSpannableScaleText(
                completedOrders,
                it.recentOrders.completedOrders,
                SCALE_FACTOR
            )

            val cancelledOrders = getString(
                R.string.orders_label,
                it.recentOrders.cancelledOrders
            )
            binding.cancelledOrdersCountTextView.text = getSpannableScaleText(
                cancelledOrders,
                it.recentOrders.cancelledOrders,
                SCALE_FACTOR
            )

            if (it.empty) {
                binding.timeHeader.text = getString(R.string.recent_orders_header_empty)
            } else {
                binding.timeHeader.text = getString(
                    R.string.recent_orders_header,
                    it.recentOrders.count.toString(),
                    it.recentOrders.timeStart,
                    it.recentOrders.timeEnd
                )
            }

            binding.retryBtn.beVisibleIf(it.empty)

            adapter.submitList(it.recentOrders.orders)
        }
    }

    companion object {
        private const val SCALE_FACTOR = 1.8f
    }
}