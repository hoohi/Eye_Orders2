package com.eyeorders.screens.menumgmt.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.eyeorders.data.model.DataState
import com.eyeorders.screens.base.AppLoadStateAdapter
import com.eyeorders.screens.menumgmt.MenuAdapter
import com.eyeorders.screens.menumgmt.MenuCategory
import com.eyeorders.screens.productavailability.ProductAvailabilityDialog
import com.eyeorders.screens.productavailability.toAvailability
import com.eyeorders.util.SpaceItemDecoration
import com.eyeorders.util.extension.beVisibleIf
import com.eyeorders.util.imageloader.PlaceHolderDrawable
import com.eyeorders.util.livedata.extension.observeEvent
import com.eyeorders.util.message.ToastHelper
import com.tasleem.orders.R
import com.tasleem.orders.databinding.FragmentMenuBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MenuFragment : Fragment() {

    @Inject
    lateinit var toastHelper: ToastHelper

    private lateinit var binding: FragmentMenuBinding

    private val category by lazy { arguments?.getParcelable<MenuCategory>(CATEGORY)!! }
    private val viewModel: MenuViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = MenuAdapter { product ->
            val dialog = ProductAvailabilityDialog.newInstance(
                product.title,
                product.isAvailable.toAvailability()
            ) {
                viewModel.updateProductAvailability(product, it)
            }
            dialog.show(childFragmentManager, dialog.javaClass.name)
        }

        binding.retryBtn.setOnClickListener {
            adapter.refresh()
        }

        binding.swipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }

        adapter.addLoadStateListener { loadState ->
            binding.swipeRefresh.isRefreshing = (loadState.refresh is LoadState.Loading)

            binding.retryBtn.beVisibleIf(loadState.refresh is LoadState.Error)
            if (loadState.refresh is LoadState.NotLoading) {
                binding.emptyText.text = getString(R.string.menu_empty_msg)
            } else {
                binding.emptyText.text = getString(R.string.loading)
            }

            if (loadState.refresh is LoadState.Error) {
                val state = loadState.refresh as LoadState.Error
                binding.emptyText.text =
                    state.error.message ?: getString(R.string.unknown_exception)
            }

            if (loadState.refresh is LoadState.Loading) {
                binding.emptyImg.setImageDrawable(PlaceHolderDrawable(requireContext()))
            } else {
                binding.emptyImg.setImageResource(R.drawable.ic_menu_mgmt)
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

        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        )

        binding.recyclerView.addItemDecoration(
            SpaceItemDecoration(
                topSpace = resources.getDimensionPixelSize(R.dimen.menu_space),
                bottomSpace = resources.getDimensionPixelSize(R.dimen.menu_space)
            )
        )

        binding.recyclerView.setEmptyView(binding.emptyGroup)
        binding.recyclerView.adapter = adapter.withLoadStateFooter(AppLoadStateAdapter {
            adapter.retry()
        })

        viewModel.changeAvailability.observeEvent(viewLifecycleOwner) { state ->
            binding.swipeRefresh.isRefreshing = (state is DataState.Loading)
            when (state) {
                is DataState.Success -> {
                    toastHelper.showMessage(getString(R.string.menu_mgmt_update_availability_success_msg))
                }

                is DataState.Error -> {
                    toastHelper.showMessage(
                        getString(
                            R.string.menu_mgmt_update_availability_fail_msg,
                            state.message
                        )
                    )
                }
                else -> {
                    //ignore
                }
            }
        }

        viewModel.fetchMenu(category.id).observe(viewLifecycleOwner) {
            Timber.d("PAFGG: $it")
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    companion object {
        private const val CATEGORY = "category"
        fun newInstance(category: MenuCategory): MenuFragment {
            val args = Bundle()
            args.putParcelable(CATEGORY, category)
            val fragment = MenuFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
