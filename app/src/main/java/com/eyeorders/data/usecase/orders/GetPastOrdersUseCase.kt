package com.eyeorders.data.usecase.orders

import android.annotation.SuppressLint
import com.eyeorders.data.dispatcher.AppDispatchers
import com.eyeorders.data.error.ErrorHandler
import com.eyeorders.data.language.LanguageChangeObserver
import com.eyeorders.data.model.EndDate
import com.eyeorders.data.model.PastOrderDataState
import com.eyeorders.data.model.StartDate
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
import timber.log.Timber
import javax.inject.Inject

class GetPastOrdersUseCase @Inject constructor(
    private val prefsDataManager: PrefsDataManager,
    private val service: ApiService,
    private val errorHandler: ErrorHandler,
    private val dispatchers: AppDispatchers,
    private val languageChangeObserver: LanguageChangeObserver,
    private val dateFormatter: DateFormatter,
) {

    @SuppressLint("DefaultLocale")
    fun execute(startDate: StartDate, endDate: EndDate): Flow<PastOrderDataState<RecentOrders>> {
        return languageChangeObserver.observeLangChange()
            .flatMapMerge { language ->
                flow {
                    try {
                        Timber.d("NEw Lang:$language ")
                        emit(PastOrderDataState.Loading)

                        /*This ensures that in the query, startDate must be before the endDate*/
                        val dateStart = if (startDate.date!!.after(endDate.date!!)) {
                            dateFormatter.formatServerDate(endDate.date)
                        } else {
                            dateFormatter.formatServerDate(startDate.date)
                        }
                        val dateEnd = if (startDate.date.after(endDate.date)) {
                            dateFormatter.formatServerDate(startDate.date)
                        } else {
                            dateFormatter.formatServerDate(endDate.date)
                        }

                        val userId = prefsDataManager.getUserId()

                        val result = processOrdersResponse {
                            service.getRecentOrdersFromStartToEnd(userId, dateStart, dateEnd)
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
                        val data = result.data.sortedByDescending { it.dateCreated }
                        val startTime =
                            dateFormatter.formatDate(data.lastOrNull()?.dateCreated)
                        val endTime = dateFormatter.formatDate(data.firstOrNull()?.dateCreated)

                        val orders = data.map {
                            RecentOrder(
                                it.id,
                                it.orderNum.toString(),
                                dateFormatter.formatDate(it.dateCreated),
                                it.status.capitalize(),
                                it.customer,
                                ""
                            )
                        }

                        emit(
                            PastOrderDataState.Success(
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
                        emit(PastOrderDataState.Error(errorHandler.getErrorMessage(e)))
                    }
                }
            }.flowOn(dispatchers.io)
    }

}