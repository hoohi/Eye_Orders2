package com.eyeorders.screens.recentorders.past

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eyeorders.data.model.EndDate
import com.eyeorders.data.model.PastOrderDataState
import com.eyeorders.data.model.StartDate
import com.eyeorders.data.usecase.orders.GetPastOrdersUseCase
import com.eyeorders.screens.recentorders.data.RecentOrders
import com.eyeorders.util.livedata.extension.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PastOrdersViewModel @Inject constructor(
    private val getPastOrdersUseCase: GetPastOrdersUseCase
) : ViewModel() {

    private val mutableViewState = MutableLiveData<PastOrdersViewState>()
    val viewState = mutableViewState.asLiveData()

    private val intentEmitter = MutableSharedFlow<PastOrderIntent>()

    private val stateReducer =
        { oldState: PastOrdersViewState, dataState: PastOrderDataState<RecentOrders> ->
            when (dataState) {

                is PastOrderDataState.Error -> oldState.copy(
                    loading = false,
                    errorMessage = dataState.message,
                    empty = false,
                    firstLoad = true,
                )

                is PastOrderDataState.Loading -> oldState.copy(
                    loading = true,
                    errorMessage = null,
                    empty = false
                )
                is PastOrderDataState.Success -> oldState.copy(
                    searchMode = false,
                    loading = false,
                    errorMessage = null,
                    recentOrders = dataState.data,
                    firstLoad = true,
                    empty = dataState.data.count == 0
                )

                is PastOrderDataState.CancelSelection -> oldState.copy(
                    searchMode = false,
                )

                is PastOrderDataState.ChangeSelection -> oldState.copy(
                    searchMode = true,
                )
            }
        }

    private val intentProcessor: (PastOrderIntent) -> Flow<PastOrderDataState<RecentOrders>> = { intent ->
        when (intent) {
            is PastOrderIntent.LoadOrders -> getPastOrdersUseCase.execute(
                intent.startDate,
                intent.endDate
            )

            is PastOrderIntent.CancelSelection ->  flowOf(PastOrderDataState.CancelSelection)

            is PastOrderIntent.ChangeSelection -> flowOf(PastOrderDataState.ChangeSelection)
        }
    }

    init {
        intentEmitter.flatMapLatest {
            intentProcessor.invoke(it)
        }.scan(PastOrdersViewState()) { previous, result ->
            stateReducer(previous, result)
        }.onEach {
            mutableViewState.value = it
        }
            .launchIn(viewModelScope)
    }

    fun loadData(startDate: StartDate, endDate: EndDate) {
            emitIntent(PastOrderIntent.LoadOrders(startDate, endDate))
    }

    fun onCancelSelection() {
        emitIntent(PastOrderIntent.CancelSelection)
    }

    fun onChangeSelection() {
        emitIntent(PastOrderIntent.ChangeSelection)
    }

    private fun emitIntent(intent: PastOrderIntent) {
        viewModelScope.launch {
            intentEmitter.emit(intent)
        }
    }
}
