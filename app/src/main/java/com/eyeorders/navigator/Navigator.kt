package com.eyeorders.navigator

import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import com.eyeorders.data.model.OrderStatus
import com.eyeorders.screens.neworder.NewOrderFragment
import com.eyeorders.screens.orderdetail.OrderDetailFragment
import com.tasleem.orders.R
import javax.inject.Inject

class Navigator @Inject constructor(
    private val componentsProvider: NavComponentsProvider,
) {

    private val navController: NavController
        get() = componentsProvider.navController

    fun toNavDrawer() {
        componentsProvider.drawerLayout.openDrawer(GravityCompat.START)
    }

    fun toActiveOrders() {
        if (navController.currentDestination?.id != R.id.activeOrdersFragment) {
            navController.navigate(R.id.action_global_activeOrdersFragment)
        }
    }

    fun toMenuManagement() {
        if (navController.currentDestination?.id != R.id.menuManagementFragment) {
            navController.navigate(R.id.action_global_menuManagementFragment)
        }
    }

    fun toRecentOrders() {
        if (navController.currentDestination?.id != R.id.recentOrdersFragment) {
            navController.navigate(R.id.action_global_recentOrdersFragment)
        }
    }

    fun toTestConnection() {
        navController.navigate(R.id.action_global_testConnectionDialog)
    }

    fun toHelp() {
        navController.navigate(R.id.action_global_helpDialog)
    }

    fun navigateUp() {
        navController.navigateUp()
    }

    fun toOrderDetail(orderId: Int, @OrderStatus orderStatus: Int? = null) {
        navController.navigate(
            R.id.action_global_orderDetailFragment,
            OrderDetailFragment.makeArguments(orderId, orderStatus)
        )
    }

    fun fromNewOrderToOrderDetail(orderId: Int) {
        navController.navigate(
            R.id.action_newOrderFragment_to_orderDetailFragment,
            OrderDetailFragment.makeArguments(orderId)
        )
    }

    fun toOrderDetailDeeLink(orderId: Int) {
        navController.navigate(
            R.id.action_activeOrdersFragment_to_orderDetailFragment,
            NewOrderFragment.makeArguments(orderId)
        )
    }

    fun toWorkHours() {
        if (navController.currentDestination?.id != R.id.workHoursFragment) {
            navController.navigate(
                R.id.action_global_workHoursFragment,
            )
        }
    }
}