package com.eyeorders.screens.workhours

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.eyeorders.navigator.Navigator
import com.eyeorders.screens.storestatus.StoreStatusViewHelper
import com.eyeorders.util.extension.beVisibleIf
import com.eyeorders.util.imageloader.PlaceHolderDrawable
import com.eyeorders.util.viewbinding.viewBinding
import com.tasleem.orders.R
import com.tasleem.orders.databinding.FragmentWorkHoursBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WorkHoursFragment : Fragment(R.layout.fragment_work_hours) {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var storeStatusViewHelper: StoreStatusViewHelper

    private val binding by viewBinding(FragmentWorkHoursBinding::bind)
    private val viewModel: WorkHoursViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            navigator.toNavDrawer()
        }

        storeStatusViewHelper.initialize(
            binding.stateButton,
            viewLifecycleOwner,
            viewModel.storeStatus.storeStatus,
            viewModel.storeStatus::changeStoreStatus
        )

        val adapter = WorkHoursAdapter()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.reloadWorkHours()
        }

        binding.retryBtn.setOnClickListener {
            viewModel.reloadWorkHours()
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.setEmptyView(binding.emptyGroup)
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        )

        viewModel.viewState.observe(viewLifecycleOwner) {
            binding.swipeRefresh.isRefreshing = it.loading

            if (it.loading) {
                binding.emptyText.text = getString(R.string.loading)
            } else {
                binding.emptyText.text = getString(R.string.empty_work_hours)
            }

            if (it.errorMessage != null) {
                binding.emptyText.text = it.errorMessage
            }

            if (it.loading) {
                binding.emptyImg.setImageDrawable(PlaceHolderDrawable(requireContext()))
            } else {
                binding.emptyImg.setImageResource(R.drawable.ic_work_hours)
            }

            binding.retryBtn.beVisibleIf(it.workHours.isEmpty() && it.errorMessage != null)

            adapter.submitList(it.workHours)
        }
    }
}