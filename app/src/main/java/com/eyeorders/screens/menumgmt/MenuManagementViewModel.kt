package com.eyeorders.screens.menumgmt

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eyeorders.data.model.DataState
import com.eyeorders.data.usecase.categories.GetMenuCategoriesUseCase
import com.eyeorders.screens.storestatus.StoreStatusViewModelHelper
import com.eyeorders.util.livedata.event.Event
import com.eyeorders.util.livedata.extension.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MenuManagementViewModel @Inject constructor(
    getMenuCategories: GetMenuCategoriesUseCase,
    val storeStatus: StoreStatusViewModelHelper,
) : ViewModel() {

    private val mutableViewState = MutableLiveData<MenuManagementViewState>()
    val viewState = mutableViewState.asLiveData()

    private val mutableShowLoading = MutableLiveData<Event<Unit>>()
    val showLoading = mutableShowLoading.asLiveData()

    private val mutableHideLoading = MutableLiveData<Event<Unit>>()
    val hideLoading = mutableHideLoading.asLiveData()

    private val mutableShowMessage = MutableLiveData<Event<String?>>()
    val showMessage = mutableShowMessage.asLiveData()

    private val stateReducer =
        { oldState: MenuManagementViewState, dataState: DataState<List<MenuCategory>> ->
            when (dataState) {
                is DataState.Error -> oldState.copy(
                    loading = false,
                    errorMessage = dataState.message,
                )
                is DataState.Loading -> oldState.copy(loading = true, errorMessage = null)
                is DataState.Success -> oldState.copy(
                    loading = false,
                    errorMessage = null,
                    listing = dataState.data
                )
            }
        }

    init {
        getMenuCategories.execute()
            .map {
                Timber.d("DATA_STATE: $it")
                it
            }
            .scan(MenuManagementViewState()) { previous, result ->
                stateReducer(previous, result)
            }.onEach {
                mutableViewState.value = it
            }
            .launchIn(viewModelScope)
    }

    private fun showLoading() {
        mutableShowLoading.postValue(Event(Unit))
    }

    private fun hideLoading() {
        mutableHideLoading.postValue(Event(Unit))
    }

    private fun showMessage(message: String?) {
        mutableShowMessage.postValue(Event(message))
    }
}
