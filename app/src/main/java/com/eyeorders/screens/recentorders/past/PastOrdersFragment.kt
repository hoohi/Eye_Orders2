package com.eyeorders.screens.recentorders.past

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.eyeorders.data.model.EndDate
import com.eyeorders.data.model.StartDate
import com.eyeorders.navigator.Navigator
import com.eyeorders.screens.datedialog.DateDialog
import com.eyeorders.screens.datedialog.DateDialogParams
import com.eyeorders.screens.recentorders.OrdersAdapter
import com.eyeorders.util.date.DateFormatter
import com.eyeorders.util.extension.beVisibleIf
import com.eyeorders.util.imageloader.ImageLoader
import com.eyeorders.util.imageloader.PlaceHolderDrawable
import com.eyeorders.util.text.getSpannableScaleText
import com.eyeorders.util.viewbinding.viewBinding
import com.tasleem.orders.R
import com.tasleem.orders.databinding.FragmentPastOrdersBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class PastOrdersFragment : Fragment(R.layout.fragment_past_orders) {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var dateFormatter: DateFormatter

    private var startDate = StartDate()
    private var endDate = EndDate()

    private val binding by viewBinding(FragmentPastOrdersBinding::bind)
    private val viewModel: PastOrdersViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startDateBg.setOnClickListener {
            val dialog = DateDialog.newInstance(
                DateDialogParams(
                    selectedDate = startDate.date,
                    maxDate = Date()
                )
            ) { date ->
                binding.startDateTextView.text = dateFormatter.formatServerDate(date)
                startDate = StartDate(date)
                checkToBeginSearch()
            }
            dialog.show(childFragmentManager, dialog.javaClass.simpleName)
        }

        binding.endDateBg.setOnClickListener {
            val dialog = DateDialog.newInstance(
                DateDialogParams(
                    selectedDate = endDate.date,
                    maxDate = Date()
                )
            ) { date ->
                binding.endDateTextView.text = dateFormatter.formatServerDate(date)
                endDate = EndDate(date)
                checkToBeginSearch()
            }
            dialog.show(childFragmentManager, dialog.javaClass.simpleName)
        }

        val adapter = OrdersAdapter(imageLoader) {
            navigator.toOrderDetail(it.id)
        }

        binding.retryBtn.setOnClickListener {
            checkToBeginSearch()
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.setEmptyView(binding.emptyGroup)
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        )

        binding.cancelSelectionBtn.setOnClickListener {
            viewModel.onCancelSelection()
        }

        binding.changeSelectionBtn.setOnClickListener {
            viewModel.onChangeSelection()
        }

        viewModel.viewState.observe(viewLifecycleOwner) {

            binding.selectionGroup.isVisible = it.searchMode
            binding.ordersGroup.isVisible = it.searchMode.not()
            binding.cancelSelectionBtn.isVisible = it.firstLoad && it.searchMode

            if (it.loading) {
                binding.emptyText.text = getString(R.string.loading)
            } else {
                binding.emptyText.text = getString(R.string.empty_orders_past)
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

    private fun checkToBeginSearch() {
        if (startDate.date != null && endDate.date != null) {
            viewModel.loadData(startDate, endDate)
        }
    }

    companion object {
        private const val SCALE_FACTOR = 1.8f
    }
}