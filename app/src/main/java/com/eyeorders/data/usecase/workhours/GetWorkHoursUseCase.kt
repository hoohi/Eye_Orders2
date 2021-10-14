package com.eyeorders.data.usecase.workhours

import com.eyeorders.data.error.ErrorHandler
import com.eyeorders.data.model.DataState
import com.eyeorders.data.prefs.PrefsDataManager
import com.eyeorders.data.remote.api.ApiService
import com.eyeorders.data.usecase.base.processResponse
import com.eyeorders.screens.workhours.WorkHours
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetWorkHoursUseCase @Inject constructor(
    private val service: ApiService,
    private val prefsDataManager: PrefsDataManager,
    private val errorHandler: ErrorHandler,
    private val workHoursMapper: WorkHoursMapper,
) {

    fun execute(): Flow<DataState<List<WorkHours>>> = flow {
        try {
            emit(DataState.Loading)
            val userId = prefsDataManager.getUserId()
            val data = processResponse {
                service.getWorkHours(userId)
            }
            val workHours = data.first()
            prefsDataManager.saveWorkHours(workHours)
            emit(DataState.Success(workHoursMapper.map(workHours)))
        } catch (e: Exception) {
            emit(DataState.Error(errorHandler.getErrorMessage(e)))
        }
    }

}