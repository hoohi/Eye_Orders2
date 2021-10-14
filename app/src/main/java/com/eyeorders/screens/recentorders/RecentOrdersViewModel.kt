package com.eyeorders.screens.recentorders

import androidx.lifecycle.ViewModel
import com.eyeorders.screens.storestatus.StoreStatusViewModelHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecentOrdersViewModel @Inject constructor(
    val storeStatus: StoreStatusViewModelHelper,
) : ViewModel() {

}
