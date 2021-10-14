package com.eyeorders.screens.main

import androidx.lifecycle.*
import com.eyeorders.data.usecase.auth.LogoutUseCase
import com.eyeorders.data.usecase.vendor.GetVendorDetailsUseCase
import com.eyeorders.data.usecase.workhours.SyncWorkHoursUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getVendorDetailsUseCase: GetVendorDetailsUseCase,
    private val logoutUseCase: LogoutUseCase,
    syncWorkHoursUseCase: SyncWorkHoursUseCase,
) : ViewModel() {

    val vendorDetails = getVendorDetailsUseCase.execute().asLiveData()

    private val logoutIntent = MutableLiveData<Unit>()

    val logoutResult = logoutIntent.switchMap {
        logoutUseCase.execute().asLiveData()
    }

    init {
        viewModelScope.launch {
            syncWorkHoursUseCase.execute()
        }
    }
}