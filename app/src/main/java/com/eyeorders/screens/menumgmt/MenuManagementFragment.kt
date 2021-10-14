package com.eyeorders.screens.menumgmt

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.eyeorders.navigator.Navigator
import com.eyeorders.screens.menumgmt.menu.MenuFragment
import com.eyeorders.screens.storestatus.StoreStatusViewHelper
import com.eyeorders.util.message.ToastHelper
import com.eyeorders.util.viewbinding.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.tasleem.orders.R
import com.tasleem.orders.databinding.FragmentMenuMgmtBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MenuManagementFragment : Fragment(R.layout.fragment_menu_mgmt) {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var toastHelper: ToastHelper

    @Inject
    lateinit var storeStatusViewHelper: StoreStatusViewHelper

    private val binding by viewBinding(FragmentMenuMgmtBinding::bind)
    private val viewModel: MenuManagementViewModel by viewModels()

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

        binding.progress.background.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.blue)
        )

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            val titles = state.listing.map { it.title }
            val fragments =
                state.listing.map { MenuFragment.newInstance(MenuCategory(it.id, it.title)) }
            setUpTabs(fragments, titles)

            binding.progress.root.isVisible = state.loading
            state.errorMessage?.let { message ->
                toastHelper.showMessage(message)
            }
        }
    }

    private fun setUpTabs(fragments: List<MenuFragment>, titles: List<String>) {
        val adapter = FragmentAdapter(fragments)
        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    inner class FragmentAdapter(val fragments: List<Fragment>) : FragmentStateAdapter(this) {

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }

        override fun getItemCount(): Int {
            return fragments.size
        }
    }

}