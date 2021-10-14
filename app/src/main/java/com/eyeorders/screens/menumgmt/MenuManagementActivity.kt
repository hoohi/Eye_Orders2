package com.eyeorders.screens.menumgmt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.eyeorders.navigator.NavComponentsProvider
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
class MenuManagementActivity : AppCompatActivity(), NavComponentsProvider {
    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var toastHelper: ToastHelper

    @Inject
    lateinit var storeStatusViewHelper: StoreStatusViewHelper

    override val navController: NavController
        get() = TODO("")

    override val drawerLayout: DrawerLayout
        get() = TODO("Not yet implemented")

    private val binding by viewBinding(FragmentMenuMgmtBinding::inflate)
    private val viewModel: MenuManagementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener {
            navigator.toNavDrawer()
        }


        storeStatusViewHelper.initialize(
            binding.stateButton,
            this,
            viewModel.storeStatus.storeStatus,
            viewModel.storeStatus::changeStoreStatus
        )

        binding.progress.background.setBackgroundColor(
            ContextCompat.getColor(this, R.color.blue)
        )

        viewModel.viewState.observe(this) { state ->
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

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, MenuManagementActivity::class.java)
        }
    }
}