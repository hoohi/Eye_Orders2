package com.eyeorders.screens.activeorders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.eyeorders.data.model.DataState
import com.eyeorders.data.prefs.PrefsDataManager
import com.eyeorders.data.usecase.orders.GetAcceptedOrdersCountUseCase
import com.eyeorders.data.usecase.orders.GetAcceptedOrdersUseCase
import com.eyeorders.data.usecase.orders.GetNewOrdersRealtimeUseCase
import com.eyeorders.screens.activeorders.neworders.NewOrder
import com.eyeorders.screens.activeorders.neworders.NewOrderViewState
import com.eyeorders.screens.storestatus.StoreStatusViewModelHelper
import com.eyeorders.util.livedata.event.Event
import com.eyeorders.util.livedata.extension.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActiveOrdersViewModel @Inject constructor(
    private val getNewOrdersUseCase: GetNewOrdersRealtimeUseCase,
    getAcceptedOrdersUseCase: GetAcceptedOrdersUseCase,
    val storeStatus: StoreStatusViewModelHelper,
    getAcceptedOrdersCountUseCase: GetAcceptedOrdersCountUseCase,
    private val prefsDataManager: PrefsDataManager,
) : ViewModel() {

    private val mutableNewOrdersViewState = MutableLiveData<NewOrderViewState>()
    val newOrders = mutableNewOrdersViewState.asLiveData()

    val acceptedOrders = getAcceptedOrdersUseCase.execute().cachedIn(viewModelScope)

    val acceptedOrdersCount = getAcceptedOrdersCountUseCase.execute()

    private val mutableUpdatedOrder = MutableLiveData<Event<Unit>>()
    val updatedOrder = mutableUpdatedOrder.asLiveData()

    private val intent = MutableSharedFlow<Unit>()

    private val stateReducer =
        { oldState: NewOrderViewState, dataState: DataState<List<NewOrder>> ->
            when (dataState) {
                is DataState.Error -> oldState.copy(
                    loading = false,
                    errorMessage = dataState.message,
                )
                is DataState.Loading -> oldState.copy(loading = true, errorMessage = null)
                is DataState.Success -> oldState.copy(
                    loading = false,
                    errorMessage = null,
                    data = dataState.data
                )
            }
        }

    init {
        intent.flatMapLatest { getNewOrdersUseCase.execute() }
            .scan(NewOrderViewState()) { previous, result ->
                stateReducer(previous, result)
            }.onEach {
                mutableNewOrdersViewState.value = it
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            prefsDataManager.setUpdatedOrderStatus(false)
        }

        reloadNewOrders()
    }

    fun reloadNewOrders() {
        viewModelScope.launch {
            intent.emit(Unit)
        }
    }

    fun checkIfUpdatedOrder() {
        viewModelScope.launch {
            if (prefsDataManager.updatedOrderStatus()) {
                mutableUpdatedOrder.postValue(Event(Unit))
            }
        }
    }
}
