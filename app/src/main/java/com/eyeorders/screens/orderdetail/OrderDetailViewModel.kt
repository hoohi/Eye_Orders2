package com.eyeorders.screens.orderdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eyeorders.data.model.DataState
import com.eyeorders.data.model.OrderStatus
import com.eyeorders.data.usecase.orders.ChangeOrderStatusUseCase
import com.eyeorders.data.usecase.orders.DeclineOrderUseCase
import com.eyeorders.data.usecase.orders.GetOrderDetailUseCase
import com.eyeorders.screens.orderdetail.model.OrderDetail
import com.eyeorders.util.StringResource
import com.eyeorders.util.livedata.event.Event
import com.eyeorders.util.livedata.extension.asLiveData
import com.eyeorders.util.printer.IposPrinterManger
import com.eyeorders.util.printer.PrinterManager
import com.tasleem.orders.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor (
    private val getOrderDetailUseCase: GetOrderDetailUseCase,
    private val changeOrderStatusUseCase: ChangeOrderStatusUseCase,
    private val declineOrderUseCase: DeclineOrderUseCase,
    private val stringResource: StringResource,
    private val iposPrinterManger: IposPrinterManger,
    private val printerManager: PrinterManager

) : ViewModel() {

    private val intents = MutableSharedFlow<OrderDetailIntent>()

    private lateinit var defaultState: OrderDetailViewState

    private val mutableViewState = MutableLiveData<OrderDetailViewState>()
    val viewState = mutableViewState.asLiveData()

    private val mutableShowToast = MutableLiveData<Event<String>>()
    val showToast = mutableShowToast.asLiveData()

    private val stateReducer =
        { oldState: OrderDetailViewState, dataState: DataState<OrderDetail> ->
            when (dataState) {
                is DataState.Error -> oldState.copy(
                    loading = false,
                    success = false,
                    errorMessage = dataState.message,
                )
                is DataState.Loading -> oldState.copy(loading = true, errorMessage = null)
                is DataState.Success -> oldState.copy(
                    loading = false,
                    errorMessage = null,
                    success = true,
                    orderDetail = dataState.data
                )
            }
        }

    private val intentProcessor: (OrderDetailIntent) -> Flow<DataState<OrderDetail>> = { intent ->
        when (intent) {
            is OrderDetailIntent.LoadInitial -> getOrderDetailUseCase.execute(intent.orderId)
            is OrderDetailIntent.ChangeOrderStatus -> {
                val order = viewState.value?.orderDetail
                check(order != null) { "Order should not be null at this point" }
                changeOrderStatusUseCase.execute(order)
            }
            is OrderDetailIntent.DeclineOrder -> {
                val order = viewState.value?.orderDetail
                check(order != null) { "Order should not be null at this point" }
                declineOrderUseCase.execute(order)
            }
        }
    }

    fun changeOrderStatus() {
        val order = viewState.value?.orderDetail
        check(order != null) { "Order should not be null at this point" }
        emitIntent(OrderDetailIntent.ChangeOrderStatus)
    }

    fun declineOrder() {
        val order = viewState.value?.orderDetail
        check(order != null) { "Order should not be null at this point" }
        emitIntent(OrderDetailIntent.DeclineOrder)
    }

    fun fetchOrderDetail(orderId: Int) {
        emitIntent(OrderDetailIntent.LoadInitial(orderId))
    }

    private fun emitIntent(intent: OrderDetailIntent) {
        viewModelScope.launch {
            intents.emit(intent)
        }
    }

    fun init(orderStatus: Int?) {
        defaultState = OrderDetailViewState(
            orderDetail = orderStatus?.let { OrderDetail().copy(status = orderStatus) }
                ?: OrderDetail()
        )
        intents
            .flatMapLatest {
                intentProcessor.invoke(it)
            }
            .scan(defaultState) { previous, result ->
                if(previous.orderDetail.status == OrderStatus.PENDING &&
                    (result as? DataState.Success)?.data?.status == OrderStatus.VENDOR_CONFIRM){
                    printOrderDetails() // Sunmi V2 Printer
                    printOrderDetailsIposPrinter() // IposPrinter
                }
                stateReducer(previous, result)
            }
            .onEach {
                mutableViewState.value = it
            }
            .launchIn(viewModelScope)
    }

    fun printOrderDetails() {
        viewModelScope.launch {
            val orderDetail = viewState.value?.orderDetail
            check(orderDetail != null) { "Order should not be null at this point" }
            val message = when (val result = printerManager.printOrderDetails(orderDetail)) {
                is PrinterManager.PrintResult.Success -> stringResource.getString(R.string.print_success_msg)
                is PrinterManager.PrintResult.Error -> result.e.message ?: stringResource.getString(
                    R.string.printer_error_msg
                )
                is PrinterManager.PrintResult.Disconnected -> stringResource.getString(R.string.printer_disconnected_msg)
            }
            mutableShowToast.postValue(Event(message))
        }
    }
// This is for Ipos printer ( Milestone )
    fun printOrderDetailsIposPrinter(){
        viewModelScope.launch {
            val orderDetail = viewState.value?.orderDetail
            check(orderDetail != null) { "Order should not be null at this point" }
            val message = when (val result = iposPrinterManger.printOrderDetailsIposPrinter(orderDetail)) {
                is IposPrinterManger.PrintResults.Success -> stringResource.getString(R.string.print_success_msg)
                is IposPrinterManger.PrintResults.Error -> result.e.message ?: stringResource.getString(
                    R.string.printer_error_msg
                )
                is IposPrinterManger.PrintResults.Disconnected -> stringResource.getString(R.string.printer_disconnected_msg)
            }
            mutableShowToast.postValue(Event(message))
        }

    }
}