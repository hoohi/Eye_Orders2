package com.eyeorders.screens.recentorders.today

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eyeorders.data.model.DataState
import com.eyeorders.data.usecase.orders.GetTodayOrdersUseCase
import com.eyeorders.screens.recentorders.data.RecentOrders
import com.eyeorders.util.livedata.extension.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodayOrdersViewModel @Inject constructor(
    private val getTodayOrdersUseCase: GetTodayOrdersUseCase
) : ViewModel() {

    private val mutableViewState = MutableLiveData<TodayOrdersViewState>()
    val viewState = mutableViewState.asLiveData()

    private val intentEmitter = MutableSharedFlow<Unit>()

    private val stateReducer =
        { oldState: TodayOrdersViewState, dataState: DataState<RecentOrders> ->
            when (dataState) {
                is DataState.Error -> oldState.copy(
                    loading = false,
                    errorMessage = dataState.message,
                    empty = false,
                )
                is DataState.Loading -> oldState.copy(
                    loading = true,
                    errorMessage = null,
                    empty = false
                )
                is DataState.Success -> oldState.copy(
                    loading = false,
                    errorMessage = null,
                    recentOrders = dataState.data,
                    empty = dataState.data.count == 0
                )
            }
        }

    init {
        intentEmitter.flatMapLatest {
            getTodayOrdersUseCase.execute()
        }.scan(TodayOrdersViewState()) { previous, result ->
            stateReducer(previous, result)
        }.onEach {
            mutableViewState.value = it
        }
            .launchIn(viewModelScope)

        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            intentEmitter.emit(Unit)
        }
    }
}
