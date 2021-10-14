package com.eyeorders.screens.recentorders

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.eyeorders.navigator.Navigator
import com.eyeorders.screens.recentorders.past.PastOrdersFragment
import com.eyeorders.screens.recentorders.today.TodayOrdersFragment
import com.eyeorders.screens.storestatus.StoreStatusViewHelper
import com.eyeorders.util.viewbinding.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.tasleem.orders.R
import com.tasleem.orders.databinding.FragmentRecentOrdersBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RecentOrdersFragment : Fragment(R.layout.fragment_recent_orders) {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var storeStatusViewHelper: StoreStatusViewHelper

    private val binding by viewBinding(FragmentRecentOrdersBinding::bind)
    private val viewModel: RecentOrdersViewModel by viewModels()

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

        val fragments = listOf(
            TodayOrdersFragment(),
            PastOrdersFragment()
        )
        val titles = listOf(
            getString(R.string.today),
            getString(R.string.past)
        )

        val adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }

            override fun getItemCount(): Int {
                return fragments.size
            }
        }

        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}