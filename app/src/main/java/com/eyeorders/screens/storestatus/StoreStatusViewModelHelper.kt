package com.eyeorders.screens.storestatus

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.eyeorders.data.model.DataState
import com.eyeorders.data.usecase.storestatus.ChangeStoreStatusUseCase
import com.eyeorders.data.usecase.storestatus.GetStoreStatusUseCase
import com.eyeorders.util.livedata.extension.asLiveData
import com.eyeorders.util.livedata.extension.switchMap
import javax.inject.Inject

class StoreStatusViewModelHelper @Inject constructor(
    getStoreStatusUseCase: GetStoreStatusUseCase,
    private val changeStatusUseCase: ChangeStoreStatusUseCase,
){

    private val storeChange = MutableLiveData<StoreStatus>()
    private val changeStore = storeChange.switchMap {
        changeStatusUseCase.execute(it).asLiveData()
    }


    private val mutableStoreStatus = MediatorLiveData<DataState<StoreStatus>>()
    val storeStatus = mutableStoreStatus.asLiveData()

    init {
        mutableStoreStatus.addSource(getStoreStatusUseCase.execute().asLiveData()) {
            mutableStoreStatus.postValue(it)
        }

        mutableStoreStatus.addSource(changeStore){
            mutableStoreStatus.postValue(it)
        }
    }

    fun changeStoreStatus(status: StoreStatus) {
        storeChange.postValue(status)
    }
}