package com.eyeorders.neworderschecker

import com.eyeorders.data.model.DataState
import com.eyeorders.data.usecase.orders.GetNewOrdersUseCase
import timber.log.Timber
import javax.inject.Inject

class CheckForNewOrdersTask @Inject constructor(
    private val getNewOrdersUseCase: GetNewOrdersUseCase,
    private val newOrderNotifier: NewOrderNotifier,
) {

    suspend fun execute() {
        try {
            when (val result = getNewOrdersUseCase.execute()) {
                is DataState.Success -> {
                    Timber.d("Work is successful")
                    if (result.data.isNotEmpty()) {
                        val order = result.data.first()
                        newOrderNotifier.showPopupIfWithinTime(order)
                    }
                }

                is DataState.Error -> {
                    Timber.d("Work failed with: ${result.message}")
                }
                else -> {
                    throw IllegalStateException("Should not emit $result state")
                }
            }
        } catch (e: Exception) {
            Timber.d("Work failed with: $e")
        }
    }
}