package com.eyeorders.data.usecase.orders

import com.eyeorders.data.dispatcher.AppDispatchers
import com.eyeorders.data.error.ErrorHandler
import com.eyeorders.data.language.LanguageChangeObserver
import com.eyeorders.data.model.DataState
import com.eyeorders.data.prefs.PrefsDataManager
import com.eyeorders.data.remote.api.ApiService
import com.eyeorders.data.usecase.base.processResponse
import com.eyeorders.data.usecase.orders.mapper.OrderDetailMapper
import com.eyeorders.screens.orderdetail.model.OrderDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetOrderDetailUseCase @Inject constructor(
    private val dispatchers: AppDispatchers,
    private val service: ApiService,
    private val errorHandler: ErrorHandler,
    private val prefsDataManager: PrefsDataManager,
    private val languageChangeObserver: LanguageChangeObserver,
) {

    fun execute(orderId: Int): Flow<DataState<OrderDetail>> {
        return languageChangeObserver.observeLangChange().flatMapLatest { language ->
            val orderDetailMapper = OrderDetailMapper(language)
            flow {
                try {
                    emit(DataState.Loading)
                    val userId = prefsDataManager.getUserId()
                    val data = processResponse {
                        service.getOrderDetail(userId, orderId)
                    }.first()
                    emit(
                        DataState.Success(
                            orderDetailMapper.map(data)
                        )
                    )
                } catch (e: Exception) {
                    emit(DataState.Error(errorHandler.getErrorMessage(e)))
                }
            }
        }.flowOn(dispatchers.io)
    }
}