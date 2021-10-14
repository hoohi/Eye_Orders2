package com.eyeorders.data.usecase.workhours

import com.eyeorders.data.error.ErrorHandler
import com.eyeorders.data.model.DataState
import com.eyeorders.data.prefs.PrefsDataManager
import com.eyeorders.data.remote.api.ApiService
import com.eyeorders.data.usecase.base.processResponse
import com.eyeorders.screens.workhours.WorkHours
import timber.log.Timber
import javax.inject.Inject

class SyncWorkHoursUseCase @Inject constructor(
    private val service: ApiService,
    private val prefsDataManager: PrefsDataManager,
    private val errorHandler: ErrorHandler,
    private val workHoursMapper: WorkHoursMapper,
) {

    suspend fun execute(): DataState<List<WorkHours>> {
        return try {
            val userId = prefsDataManager.getUserId()
            val data = processResponse {
                service.getWorkHours(userId)
            }
            val workHours = data.first()
            prefsDataManager.saveWorkHours(workHours)
            Timber.d("Synced work hours successfully")
            DataState.Success(workHoursMapper.map(workHours))
        } catch (e: Exception) {
            Timber.w(e, "Error occurred while syncing work hours")
            DataState.Error(errorHandler.getErrorMessage(e))
        }
    }

}