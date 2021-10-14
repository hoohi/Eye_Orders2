package com.eyeorders.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.eyeorders.data.error.ErrorHandler
import com.eyeorders.data.model.menu.MenuProduct
import com.eyeorders.data.prefs.PrefsDataManager
import com.eyeorders.data.remote.api.ApiService
import com.eyeorders.data.usecase.base.processResponse
import javax.inject.Inject

class MenuProductsDataSource @Inject constructor(
    private val errorHandler: ErrorHandler,
    private val apiService: ApiService,
    private val prefsDataManager: PrefsDataManager,
    private val categoryId: Int,
) : PagingSource<Int, MenuProduct>() {

    override fun getRefreshKey(state: PagingState<Int, MenuProduct>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MenuProduct> {
        return try {
            val page = params.key ?: START_PAGE

            val userId = prefsDataManager.getUserId()
            val result = processResponse {
                apiService.getProducts(userId, categoryId, page)
            }

            val data = result.map { it.toMenuProduct() }
            LoadResult.Page(
                data = data,
                prevKey = null,
                nextKey = if (data.isEmpty()) null else page + 1
            )

        } catch (exception: Exception) {
            LoadResult.Error(Exception(errorHandler.getErrorMessage(exception)))
        }
    }


    companion object {
        private const val START_PAGE = 0
    }
}