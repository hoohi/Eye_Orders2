package com.eyeorders.screens.activeorders

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.eyeorders.navigator.Navigator
import com.eyeorders.screens.activeorders.acceptedorders.AcceptedOrdersAdapter
import com.eyeorders.screens.activeorders.neworders.NewOrdersAdapter
import com.eyeorders.screens.base.AppLoadStateAdapter
import com.eyeorders.screens.storestatus.StoreStatusViewHelper
import com.eyeorders.util.SpaceItemDecoration
import com.eyeorders.util.extension.beVisibleIf
import com.eyeorders.util.imageloader.ImageLoader
import com.eyeorders.util.imageloader.PlaceHolderDrawable
import com.eyeorders.util.livedata.extension.observeEvent
import com.eyeorders.util.message.ToastHelper
import com.eyeorders.util.viewbinding.viewBinding
import com.tasleem.orders.R
import com.tasleem.orders.databinding.FragmentActiveOrdersBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ActiveOrdersFragment : Fragment(R.layout.fragment_active_orders) {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var toastHelper: ToastHelper

    @Inject
    lateinit var storeStatusViewHelper: StoreStatusViewHelper

    private val binding by viewBinding(FragmentActiveOrdersBinding::bind)
    private val viewModel: ActiveOrdersViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storeStatusViewHelper.initialize(
            binding.stateButton,
            viewLifecycleOwner,
            viewModel.storeStatus.storeStatus,
            viewModel.storeStatus::changeStoreStatus
        )

        binding.toolbar.setNavigationOnClickListener {
            navigator.toNavDrawer()
        }

        initNewOrders()
        initAcceptedOrders()
    }

    private fun initAcceptedOrders() {
        val adapter = AcceptedOrdersAdapter {
            Timber.d("AcceptedOrdersAdapter: $it")
            navigator.toOrderDetail(it.id, it.orderStatus)
        }

        binding.acceptedRetryBtn.setOnClickListener {
            adapter.refresh()
        }

        binding.swipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }


        adapter.addLoadStateListener { loadState ->
            Timber.d("ACCEPTED load state: $loadState")
            binding.acceptedRetryBtn.beVisibleIf(
                loadState.refresh is LoadState.Error ||
                        (loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0)
            )
            if (loadState.refresh is LoadState.NotLoading) {
                binding.acceptedEmptyText.text = getString(R.string.accepted_orders_empty_msg)
            } else {
                binding.acceptedEmptyText.text = getString(R.string.loading)
            }

            if (loadState.refresh is LoadState.Error) {
                val state = loadState.refresh as LoadState.Error
                binding.acceptedEmptyText.text =
                    state.error.message ?: getString(R.string.unknown_exception)
            }

            binding.swipeRefresh.isRefreshing = loadState.refresh is LoadState.Loading

            if (loadState.refresh is LoadState.Loading) {
                binding.acceptedEmptyImg.setImageDrawable(PlaceHolderDrawable(requireContext()))
            } else {
                binding.acceptedEmptyImg.setImageResource(R.drawable.ic_active_orders)
            }

            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let { error ->
                toastHelper.showMessage(
                    error.error.message ?: getString(R.string.unknown_exception)
                )
            }
        }

        binding.acceptedRecyclerView.adapter = adapter.withLoadStateFooter(AppLoadStateAdapter {
            adapter.retry()
        })

        binding.acceptedRecyclerView.setEmptyView(binding.acceptedOrdersEmptyGroup)
        binding.acceptedRecyclerView.isNestedScrollingEnabled = true


        viewModel.acceptedOrders.onEach {
            Timber.d("acceptedOrders: $it")
            adapter.submitData(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.acceptedOrdersCount.onEach {
            binding.acceptedCountTextView.text = it
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.updatedOrder.observeEvent(viewLifecycleOwner) {
            adapter.refresh()
        }
    }

    private fun initNewOrders() {
        val adapter = NewOrdersAdapter {
            navigator.toOrderDetail(it.id, it.orderStatus)
        }


        binding.newRetryBtn.setOnClickListener {
            viewModel.reloadNewOrders()
        }


        binding.newRecyclerView.adapter = adapter
        binding.newRecyclerView.setEmptyView(binding.newOrdersEmptyGroup)
        binding.newRecyclerView.addItemDecoration(
            SpaceItemDecoration(
                endSpace = resources.getDimensionPixelSize(R.dimen.new_orders_space)
            )
        )

        viewModel.newOrders.observe(viewLifecycleOwner) { state ->

            if (state.loading) {
                binding.newEmptyImg.setImageDrawable(PlaceHolderDrawable(requireContext()))
                binding.newEmptyText.text = getString(R.string.loading)
            } else {
                binding.newEmptyImg.setImageResource(R.drawable.ic_active_orders)
                binding.newEmptyText.text = getString(R.string.new_orders_empty_msg)
            }

            binding.newRetryBtn.beVisibleIf(state.errorMessage != null)

            state.errorMessage?.let { message ->
                binding.newEmptyText.text = message
                toastHelper.showMessage(message)
            }

            binding.newCountTextView.text = state.data.size.toString()
            adapter.submitList(state.data)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkIfUpdatedOrder()
    }
}