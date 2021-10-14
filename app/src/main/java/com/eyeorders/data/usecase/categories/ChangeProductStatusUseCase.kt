package com.eyeorders.data.usecase.categories

import com.eyeorders.data.cache.AppDatabase
import com.eyeorders.data.dispatcher.AppDispatchers
import com.eyeorders.data.error.ErrorHandler
import com.eyeorders.data.model.DataState
import com.eyeorders.data.prefs.PrefsDataManager
import com.eyeorders.data.remote.api.ApiService
import com.eyeorders.data.usecase.base.processResponse
import com.eyeorders.screens.productavailability.ProductAvailability
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ChangeProductStatusUseCase @Inject constructor(
    private val service: ApiService,
    private val prefsDataManager: PrefsDataManager,
    private val errorHandler: ErrorHandler,
    private val dispatchers: AppDispatchers,
    database: AppDatabase
) {

    private val menuProductDao = database.menuProductDao()

    fun execute(productId: Int, status: ProductAvailability): Flow<DataState<Unit>> = flow {
        emit(DataState.Loading)
        try {
            val product = menuProductDao.getProductById(productId)
            //change the status locally
            val updatedProduct = product.copy(inStock = status.ordinal.toBoolean())
            menuProductDao.update(updatedProduct)
            val userId = prefsDataManager.getUserId()
            processResponse {
                service.changeProductStatus(userId, productId, status.ordinal)
            }
            emit(DataState.Success(Unit))
        } catch (e: Exception) {
            val product = menuProductDao.getProductById(productId)
            //revert if there is a failure
            menuProductDao.update(product)
            emit(DataState.Error(errorHandler.getErrorMessage(e)))
        }
    }.flowOn(dispatchers.io)
}

private fun Int.toBoolean(): Boolean {
    return this == 1
}
