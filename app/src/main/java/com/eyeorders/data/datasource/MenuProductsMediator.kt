package com.eyeorders.data.datasource

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.eyeorders.data.cache.AppDatabase
import com.eyeorders.data.cache.menuproduct.MenuProductKey
import com.eyeorders.data.error.ErrorHandler
import com.eyeorders.data.model.menu.MenuProduct
import com.eyeorders.data.prefs.PrefsDataManager
import com.eyeorders.data.remote.api.ApiService
import com.eyeorders.data.usecase.base.processResponse
import timber.log.Timber
import javax.inject.Inject

class MenuProductsMediator @Inject constructor(
    private val errorHandler: ErrorHandler,
    private val prefsDataManager: PrefsDataManager,
    private val apiService: ApiService,
    private val appDatabase: AppDatabase,
) : RemoteMediator<Int, MenuProduct>() {

    var categoryId: Int = -1

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MenuProduct>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    START_PAGE
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    if (remoteKeys?.nextPage == null) {
                        START_PAGE
                    } else {
                        remoteKeys.nextPage
                    }
                }
            }

            val userId = prefsDataManager.getUserId()
            val response = processResponse {
                apiService.getProducts(userId, categoryId, page)
            }

            val data = response.map { it.toMenuProduct() }
            Timber.d("SAVING data: $data")
            appDatabase.menuProductDao().insert(data)
            val keys = data.map { product ->
                MenuProductKey(
                    product.id,
                    page - 1,
                    page + 1
                )
            }

            appDatabase.menuProductKeyDao().insert(keys)

            MediatorResult.Success(endOfPaginationReached = response.isEmpty())

        } catch (exception: Exception) {
            MediatorResult.Error(Exception(errorHandler.getErrorMessage(exception)))
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, MenuProduct>): MenuProductKey? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { product ->
                // Get the remote keys of the last item retrieved
                appDatabase.menuProductKeyDao().getRemoteKeysById(product.id)
            }
    }

    companion object {
        private const val START_PAGE = 0
    }
}