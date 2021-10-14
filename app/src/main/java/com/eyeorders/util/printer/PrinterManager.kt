package com.eyeorders.util.printer

import android.content.Context
import android.widget.Toast
import com.eyeorders.data.analytics.AnalyticEvent
import com.eyeorders.data.analytics.Analytics
import com.eyeorders.data.dispatcher.AppDispatchers
import com.eyeorders.screens.orderdetail.model.OrderDetail
import com.eyeorders.util.printer.sumni.SunmiPrintHelper
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class PrinterManager @Inject constructor(
    private val printOrderDetails: PrintOrderDetails,
    private val dispatchers: AppDispatchers,
    private val gson: Gson,
    private val analytics: Analytics,

    @ActivityContext private val context: Context
) {
    private val sunmiHelper = SunmiPrintHelper.getInstance().apply {
        initPrinter()

    }

    suspend fun printOrderDetails(orderDetail: OrderDetail): PrintResult {
        return withContext(dispatchers.io) {
            val data = printOrderDetails.createPrinterData(orderDetail)
            Toast.makeText(context,"Function Called",Toast.LENGTH_LONG).show()
            val jsonData = gson.toJson(data)
            analytics.logEvent(AnalyticEvent.PrintReceipt(jsonData))
            Timber.d("Printing data: $jsonData")
            suspendCoroutine { continuation ->
                val listener = object : SunmiPrintHelper.Listener {
                    override fun onError(e: Exception) {
                        analytics.logEvent(AnalyticEvent.PrintReceiptFail(jsonData))
                        Timber.e(e, "Error printing")
                        if (continuation.context.isActive) {
                            continuation.resumeWith(Result.success(PrintResult.Error(e)))
                        }
                    }

                    override fun onPrinterDisconnecting() {
                        analytics.logEvent(AnalyticEvent.PrinterDisconnected(jsonData))
                        if (continuation.context.isActive) {
                            continuation.resumeWith(Result.success(PrintResult.Disconnected))
                        }
                    }

                    override fun onSuccess() {
                        Timber.d("Print successful")
                        analytics.logEvent(AnalyticEvent.PrintReceiptSuccess(jsonData))
                        if (continuation.context.isActive) {
                            continuation.resumeWith(Result.success(PrintResult.Success))
                        }
                    }
                }
                sunmiHelper.setListener(listener)
                sunmiHelper.printTableItems(data)
            }
        }
    }
    sealed class PrintResult {
        object Success : PrintResult()
        object Disconnected : PrintResult()
        data class Error(val e: Exception) : PrintResult()
    }
}