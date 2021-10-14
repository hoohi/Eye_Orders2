package com.eyeorders.screens.workhours

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eyeorders.data.model.DataState
import com.eyeorders.data.usecase.workhours.GetWorkHoursUseCase
import com.eyeorders.screens.storestatus.StoreStatusViewModelHelper
import com.eyeorders.util.livedata.extension.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkHoursViewModel @Inject constructor(
    val storeStatus: StoreStatusViewModelHelper,
    private val getWorkHoursUseCase: GetWorkHoursUseCase,
) : ViewModel() {

    private val mutableViewState = MutableLiveData<WorkHoursViewState>()
    val viewState = mutableViewState.asLiveData()

    private val intent = MutableSharedFlow<Unit>()

    private val stateReducer =
        { oldState: WorkHoursViewState, dataState: DataState<List<WorkHours>> ->
            when (dataState) {
                is DataState.Error -> oldState.copy(
                    loading = false,
                    errorMessage = dataState.message,
                )
                is DataState.Loading -> oldState.copy(loading = true, errorMessage = null)
                is DataState.Success -> oldState.copy(
                    loading = false,
                    errorMessage = null,
                    workHours = dataState.data
                )
            }
        }

    init {
        intent.flatMapLatest { getWorkHoursUseCase.execute() }
            .scan(WorkHoursViewState()) { previous, result ->
                stateReducer(previous, result)
            }.onEach {
                mutableViewState.value = it
            }
            .launchIn(viewModelScope)

        reloadWorkHours()
    }

    fun reloadWorkHours() {
        viewModelScope.launch {
            intent.emit(Unit)
        }
    }
}
