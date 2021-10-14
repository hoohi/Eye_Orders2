package com.eyeorders.data.usecase.orders

import android.annotation.SuppressLint
import com.eyeorders.data.dispatcher.AppDispatchers
import com.eyeorders.data.error.ErrorHandler
import com.eyeorders.data.language.LanguageChangeObserver
import com.eyeorders.data.model.DataState
import com.eyeorders.data.orderstats.OrderStats
import com.eyeorders.data.prefs.PrefsDataManager
import com.eyeorders.data.remote.api.ApiService
import com.eyeorders.data.usecase.base.processOrdersResponse
import com.eyeorders.screens.recentorders.data.RecentOrder
import com.eyeorders.screens.recentorders.data.RecentOrders
import com.eyeorders.util.date.DateFormatter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import javax.inject.Inject

class GetTodayOrdersUseCase @Inject constructor(
    private val prefsDataManager: PrefsDataManager,
    private val service: ApiService,
    private val errorHandler: ErrorHandler,
    private val dispatchers: AppDispatchers,
    private val languageChangeObserver: LanguageChangeObserver,
    private val dateFormatter: DateFormatter,
) {

    @SuppressLint("DefaultLocale")
    fun execute(): Flow<DataState<RecentOrders>> {
        return languageChangeObserver.observeLangChange()
            .flatMapMerge { language ->
                flow {
                    try {
                        Timber.d("NEw Lang:$language ")
                        emit(DataState.Loading)
                        val now = LocalDateTime.now()
                        val date = "${now.year}-${now.monthValue}-${now.dayOfMonth}"
                        val userId = prefsDataManager.getUserId()

                        val result = processOrdersResponse {
                            service.getRecentOrdersFromStartToEnd(userId, date, date)
                        }
                        prefsDataManager.saveOrderStats(
                            OrderStats(
                                result.isStopOrder,
                                result.completedOrders,
                                result.cancelledOrders,
                                result.newOrder,
                                result.acceptedOrder,
                            )
                        )
                        val data = result.data.sortedBy { it.dateCreated }
                        val startTime =
                            dateFormatter.formatToTime(data.firstOrNull()?.dateCreated)
                        val endTime = dateFormatter.formatToTime(data.lastOrNull()?.dateCreated)
                        val orders = data.map {
                            RecentOrder(
                                it.id,
                                it.orderNum.toString(),
                                dateFormatter.formatToTime(it.dateCreated),
                                it.status.capitalize(),
                                it.customer,
                                ""
                            )
                        }
                        emit(
                            DataState.Success(
                                RecentOrders(
                                    result.completedOrders,
                                    result.cancelledOrders,
                                    data.size,
                                    startTime,
                                    endTime,
                                    orders
                                )
                            )
                        )
                    } catch (e: Exception) {
                        emit(DataState.Error(errorHandler.getErrorMessage(e)))
                    }
                }
            }.flowOn(dispatchers.io)
    }

}