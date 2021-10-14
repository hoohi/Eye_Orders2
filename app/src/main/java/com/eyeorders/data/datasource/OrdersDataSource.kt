package com.eyeorders.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.eyeorders.data.error.ErrorHandler
import com.eyeorders.data.model.order.OrderResponse
import com.eyeorders.data.orderstats.OrderStats
import com.eyeorders.data.prefs.PrefsDataManager
import com.eyeorders.data.remote.api.ApiService
import com.eyeorders.data.usecase.base.processOrdersResponse
import javax.inject.Inject

class OrdersDataSource @Inject constructor(
    private val apiService: ApiService,
    private val prefsDataManager: PrefsDataManager,
    private val errorHandler: ErrorHandler,
    private var orderStatus: String
) : PagingSource<Int, OrderResponse>() {


    override fun getRefreshKey(state: PagingState<Int, OrderResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OrderResponse> {
        return try {
            val page = params.key ?: START_PAGE

            val userId = prefsDataManager.getUserId()

            val result = processOrdersResponse {
                apiService.getOrders(userId, orderStatus, page)
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

            val data = result.data
            LoadResult.Page(
                data = data,
                prevKey = null,
                nextKey = if (data.isEmpty()) null else page + 1
            )

        } catch (exception: Exception) {
            val errorMessage = errorHandler.getErrorMessage(exception)
            LoadResult.Error(Exception(errorMessage))
        }
    }

    companion object {
        private const val START_PAGE = 0
    }
}