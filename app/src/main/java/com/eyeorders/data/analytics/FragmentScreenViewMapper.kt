package com.eyeorders.data.analytics

import androidx.fragment.app.Fragment
import com.eyeorders.data.analytics.AnalyticEvent.ScreenViewEvent
import com.eyeorders.data.analytics.AnalyticEvent.ScreenViewEvent.*
import com.eyeorders.screens.activeorders.ActiveOrdersFragment
import com.eyeorders.screens.datedialog.DateDialog
import com.eyeorders.screens.help.HelpDialog
import com.eyeorders.screens.login.LoginFragment
import com.eyeorders.screens.menumgmt.MenuManagementFragment
import com.eyeorders.screens.menumgmt.menu.MenuFragment
import com.eyeorders.screens.neworder.NewOrderFragment
import com.eyeorders.screens.orderdetail.OrderDetailFragment
import com.eyeorders.screens.productavailability.ProductAvailabilityDialog
import com.eyeorders.screens.recentorders.RecentOrdersFragment
import com.eyeorders.screens.recentorders.past.PastOrdersFragment
import com.eyeorders.screens.recentorders.today.TodayOrdersFragment
import com.eyeorders.screens.storestatus.StoreStatusDialog
import com.eyeorders.screens.testconnection.TestConnectionDialog
import com.eyeorders.screens.workhours.WorkHoursFragment
import javax.inject.Inject

class FragmentScreenViewMapper @Inject constructor() {
    fun convert(fragment: Fragment): ScreenViewEvent {
        return when (fragment) {
            is ActiveOrdersFragment -> ActiveOrdersScreen(fragment.javaClass.simpleName)
            is DateDialog -> DateScreen(fragment.javaClass.simpleName)
            is HelpDialog -> HelpScreen(fragment.javaClass.simpleName)
            is LoginFragment -> LoginScreen(fragment.javaClass.simpleName)
            is MenuFragment -> MenuScreen(fragment.javaClass.simpleName)
            is MenuManagementFragment -> MenuManagementScreen(fragment.javaClass.simpleName)
            is NewOrderFragment -> NewOrderScreen(fragment.javaClass.simpleName)
            is OrderDetailFragment -> OrderDetailScreen(fragment.javaClass.simpleName)
            is PastOrdersFragment -> PastOrdersScreen(fragment.javaClass.simpleName)
            is ProductAvailabilityDialog -> ProductAvailabilityScreen(
                fragment.javaClass.simpleName
            )
            is RecentOrdersFragment -> RecentOrdersScreen(fragment.javaClass.simpleName)
            is StoreStatusDialog -> StoreStatusScreen(fragment.javaClass.simpleName)
            is TestConnectionDialog -> TestConnectionScreen(fragment.javaClass.simpleName)
            is TodayOrdersFragment -> TestConnectionScreen(fragment.javaClass.simpleName)
            is WorkHoursFragment -> WorkHoursScreen(fragment.javaClass.simpleName)
            else -> Default
        }
    }
}