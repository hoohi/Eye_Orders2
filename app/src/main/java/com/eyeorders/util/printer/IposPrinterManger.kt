package com.eyeorders.util.printer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.tasleem.orders.R


import android.util.Log
import android.widget.Toast
import com.eyeorders.data.analytics.AnalyticEvent
import com.eyeorders.util.printer.iposprinter.HandlerUtils
import com.eyeorders.data.analytics.Analytics
import com.eyeorders.data.dispatcher.AppDispatchers
import com.eyeorders.screens.orderdetail.model.OrderDetail
import com.eyeorders.util.printer.iposprinter.BlutoothUtil
import com.eyeorders.util.printer.iposprinter.ESCUtil
import com.eyeorders.util.printer.iposprinter.IposPrintHelper
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.io.UnsupportedEncodingException
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine
import org.json.JSONObject




class IposPrinterManger @Inject constructor(
    private val printOrderDetails: PrintOrderDetails,
    private val analytics: Analytics,
    private val gson: Gson,
    private val dispatchers: AppDispatchers,

    @ActivityContext private val context: Context
) {


    //Bluetooth Setup
    private var TAG: String = "BluetoothPrinter"
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothPrinterDevice: BluetoothDevice? = null
    private var socket: BluetoothSocket? = null

    // Define status broadcast
    private val PRINTER_NORMAL: Int = 0
    private val PRINTER_PAPERLESS: Int = 1
    private val PRINTER_THP_HIGH_TEMPERATURE: Int = 2
    private val PRINTER_MOTOR_HIGH_TEMPERATURE: Int = 3
    private val PRINTER_IS_BUSY: Int = 4
    private val PRINTER_ERROR_UNKNOWN: Int = 5

    private var printerStatus = PRINTER_ERROR_UNKNOWN

    private val PRINTER_NORMAL_ACTION: String = "com.iposprinter.iposprinterservice.NORMAL_ACTION"
    private val PRINTER_PAPERLESS_ACTION: String =
        "com.iposprinter.iposprinterservice.PAPERLESS_ACTION"
    private val PRINTER_PAPEREXISTS_ACTION: String =
        "com.iposprinter.iposprinterservice.PAPEREXISTS_ACTION"
    private val PRINTER_THP_HIGHTEMP_ACTION: String =
        "com.iposprinter.iposprinterservice.THP_HIGHTEMP_ACTION"
    private val PRINTER_MOTOR_HIGHTEMP_ACTION: String =
        "com.iposprinter.iposprinterservice.MOTOR_HIGHTEMP_ACTION"
    private val PRINTER_BUSY_ACTION: String = "com.iposprinter.iposprinterservice.NORMAL_ACTION"
    private val PRINTER_THP_NORMALTEMP_ACTION: String =
        "com.iposprinter.iposprinterservice.PRINTER_THP_NORMALTEMP_ACTION"
    private val PRINTER_CURRENT_TASK_PRINT_COMPLETE_ACTION: String =
        "com.iposprinter.iposprinterservice.CURRENT_TASK_PRINT_COMPLETE_ACTION"

    //Define the messages

    private val MSG_TEST = 1
    private val MSG_IS_NORMAL = 2
    private val MSG_IS_BUSY = 3
    private val MSG_PAPER_LESS = 4
    private val MSG_PAPER_EXISTS = 5
    private val MSG_THP_HIGH_TEMP = 6
    private val MSG_THP_TEMP_NORMAL = 7
    private val MSG_MOTOR_HIGH_TEMP = 8
    private val MSG_MOTOR_HIGH_TEMP_INIT_PRINTER = 9
    private val MSG_CURRENT_TASK_PRINT_COMPLETE = 10

    //Cycle printing type
    private val MULTI_THREAD_LOOP_PRINT: Int = 1
    private val DEFAULT_LOOP_PRINT = 0

    //Circular print flag
    private var loopPrintFlag = DEFAULT_LOOP_PRINT
    private var isBluetoothOpen = false
    private val mPrinterHandler: HandlerUtils.PrinterHandler? = null


    // Message Processing

    private val mHandlerIntent: HandlerUtils.IHandlerIntent =
        HandlerUtils.IHandlerIntent { msg ->
            when (msg.what) {
                MSG_TEST -> {
                }
                MSG_IS_NORMAL -> if (getPrinterStatus() == PRINTER_NORMAL) {
                    print_loop(loopPrintFlag)
                }
                MSG_IS_BUSY -> Toast.makeText(
                    context,
                    R.string.printer_is_working,
                    Toast.LENGTH_SHORT
                ).show()
                MSG_PAPER_LESS -> {
                    loopPrintFlag = DEFAULT_LOOP_PRINT
                    Toast.makeText(
                        context,
                        R.string.out_of_paper,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                MSG_PAPER_EXISTS -> Toast.makeText(
                    context,
                    R.string.exists_paper,
                    Toast.LENGTH_SHORT
                ).show()
                MSG_THP_HIGH_TEMP -> Toast.makeText(
                    context,
                    R.string.printer_high_temp_alarm,
                    Toast.LENGTH_SHORT
                ).show()
                MSG_MOTOR_HIGH_TEMP -> {
                    loopPrintFlag = DEFAULT_LOOP_PRINT
                    Toast.makeText(
                        context,
                        R.string.motor_high_temp_alarm,
                        Toast.LENGTH_SHORT
                    ).show()
                    mPrinterHandler!!.sendEmptyMessageDelayed(
                        MSG_MOTOR_HIGH_TEMP_INIT_PRINTER,
                        180000
                    ) //马达高温报警，等待3分钟后复位打印机
                }
                MSG_MOTOR_HIGH_TEMP_INIT_PRINTER -> {
                    loopPrintFlag = DEFAULT_LOOP_PRINT
                    printerInit()
                }
                MSG_CURRENT_TASK_PRINT_COMPLETE -> Toast.makeText(
                    context,
                    R.string.printer_current_task_print_complete,
                    Toast.LENGTH_SHORT
                ).show()
                else -> {
                }
            }
        }

    private fun print_loop(loopPrintFlag: Int) {

    }

    private val IPosPrinterStatusListener: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == null) {
                Log.d(
                    TAG,
                    "IPosPrinterStatusListener onReceive action = null"
                )
                return
            }
            // Log.d(TAG,"IPosPrinterStatusListener action = "+action);
            if (action == PRINTER_NORMAL_ACTION) {
                mPrinterHandler!!.sendEmptyMessageDelayed(MSG_IS_NORMAL, 0)
            } else if (action == PRINTER_PAPERLESS_ACTION) {
                mPrinterHandler!!.sendEmptyMessageDelayed(MSG_PAPER_LESS, 0)
            } else if (action == PRINTER_BUSY_ACTION) {
                mPrinterHandler!!.sendEmptyMessageDelayed(MSG_IS_BUSY, 0)
            } else if (action == PRINTER_PAPEREXISTS_ACTION) {
                mPrinterHandler!!.sendEmptyMessageDelayed(MSG_PAPER_EXISTS, 0)
            } else if (action == PRINTER_THP_HIGHTEMP_ACTION) {
                mPrinterHandler!!.sendEmptyMessageDelayed(MSG_THP_HIGH_TEMP, 0)
            } else if (action == PRINTER_THP_NORMALTEMP_ACTION) {
                mPrinterHandler!!.sendEmptyMessageDelayed(MSG_THP_TEMP_NORMAL, 0)
            } else if (action == PRINTER_MOTOR_HIGHTEMP_ACTION) //此时当前任务会继续打印，完成当前任务后，请等待2分钟以上时间，继续下一个打印任务
            {
                mPrinterHandler!!.sendEmptyMessageDelayed(MSG_MOTOR_HIGH_TEMP, 0)
            } else if (action == PRINTER_CURRENT_TASK_PRINT_COMPLETE_ACTION) {
                mPrinterHandler!!.sendEmptyMessageDelayed(MSG_CURRENT_TASK_PRINT_COMPLETE, 0)
            } else if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                when (state) {
                    BluetoothAdapter.STATE_OFF -> {
                        Log.d("aaa", "STATE_OFF 蓝牙关闭")
                        isBluetoothOpen = false
                    }
                    BluetoothAdapter.STATE_TURNING_OFF -> {
                        Log.d("aaa", "STATE_TURNING_OFF 蓝牙正在关闭")
                        isBluetoothOpen = false
                        if (mBluetoothAdapter != null) mBluetoothAdapter = null
                        if (mBluetoothPrinterDevice != null) mBluetoothPrinterDevice = null
                        try {
                            //var localSocket = BluetoothSocket
                            if (socket != null && socket!!.isConnected()) {
                                socket!!.close()
                                socket = null
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    BluetoothAdapter.STATE_ON -> {
                        Log.d("aaa", "STATE_ON 蓝牙开启")
                        loopPrintFlag = DEFAULT_LOOP_PRINT
                        isBluetoothOpen = true
                        loadBluetoothPrinter()
                    }
                    BluetoothAdapter.STATE_TURNING_ON -> {
                        isBluetoothOpen = true
                        Log.d("aaa", "STATE_TURNING_ON 蓝牙正在开启")
                    }
                }
            } else {
                mPrinterHandler!!.sendEmptyMessageDelayed(MSG_TEST, 0)
            }
        }
    }


    protected open fun onResume() {
        // Log.d(TAG, "activity onResume");

        //Register Printer Status Receiver
        val printerStatusFilter = IntentFilter()
        printerStatusFilter.addAction(PRINTER_NORMAL_ACTION)
        printerStatusFilter.addAction(PRINTER_PAPERLESS_ACTION)
        printerStatusFilter.addAction(PRINTER_PAPEREXISTS_ACTION)
        printerStatusFilter.addAction(PRINTER_THP_HIGHTEMP_ACTION)
        printerStatusFilter.addAction(PRINTER_THP_NORMALTEMP_ACTION)
        printerStatusFilter.addAction(PRINTER_MOTOR_HIGHTEMP_ACTION)
        printerStatusFilter.addAction(PRINTER_BUSY_ACTION)
        printerStatusFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(IPosPrinterStatusListener, printerStatusFilter)
        loopPrintFlag = DEFAULT_LOOP_PRINT
        loadBluetoothPrinter()
        if (getPrinterStatus() == PRINTER_NORMAL) printerInit()
        return
    }

    private fun registerReceiver(
        iPosPrinterStatusListener: BroadcastReceiver,
        printerStatusFilter: IntentFilter
    ) {

    }

    protected fun onPause() {
        // Log.d(TAG, "activity onPause");
        return
    }

    protected fun onStop() {
        // Log.e(TAG, "activity onStop");

        unregisterReceiver(IPosPrinterStatusListener)
        loopPrintFlag = DEFAULT_LOOP_PRINT
        return
    }

    private fun unregisterReceiver(iPosPrinterStatusListener: BroadcastReceiver) {

    }

    protected fun onDestroy() {
        // Log.d(TAG, "activity onDestroy");

        mPrinterHandler!!.removeCallbacksAndMessages(null)
        if (mBluetoothAdapter != null) mBluetoothAdapter = null
        if (mBluetoothPrinterDevice != null) mBluetoothPrinterDevice = null
        try {
            if (socket != null && socket!!.isConnected) {
                socket!!.close()
                socket = null
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return
    }

    fun loadBluetoothPrinter() {

        // 1: Get BluetoothAdapter
        mBluetoothAdapter = BlutoothUtil.getBluetoothAdapter()
        if (mBluetoothAdapter == null) {
            Toast.makeText(context, R.string.get_BluetoothAdapter_fail, Toast.LENGTH_LONG)
                .show()
            isBluetoothOpen = false
            return
        } else {
            isBluetoothOpen = true
        }
        //2: Get bluetoothPrinter Devices
        mBluetoothPrinterDevice = BlutoothUtil.getIposPrinterDevice(mBluetoothAdapter)
        if (mBluetoothPrinterDevice == null) {
            Toast.makeText(
                context,
                R.string.get_BluetoothPrinterDevice_fail,
                Toast.LENGTH_LONG
            ).show()
            return
        }
        //3: Get connect Socket
        socket = try {
            BlutoothUtil.getSocket(mBluetoothPrinterDevice)
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }
        Toast.makeText(
            context,
            R.string.get_BluetoothPrinterDevice_success,
            Toast.LENGTH_LONG
        ).show()
    }

    fun getPrinterStatus(): Int {
        val statusData = ByteArray(3)

        if (!isBluetoothOpen) {
            printerStatus = PRINTER_ERROR_UNKNOWN
            return printerStatus
        }
        if (socket == null || !socket!!.isConnected) {
            socket = try {
                BlutoothUtil.getSocket(mBluetoothPrinterDevice)
            } catch (e: IOException) {
                e.printStackTrace()
                return printerStatus
            }
        }
        try {
            val `in` = socket?.inputStream
            val out = socket?.outputStream
            val data: ByteArray = ESCUtil.getPrinterStatus()
            if (out != null) {
                out.write(data, 0, data.size)
            }
            val readsize = `in`?.read(statusData)
            Log.d(
                TAG,
                "~~~ readsize:" + readsize + " statusData:" + statusData[0] + " " + statusData[1] + " " + statusData[2]
            )
            if (readsize != null) {
                if (readsize > 0 && statusData[0] == ESCUtil.ACK && statusData[1].toInt() == 0x11) {
                    printerStatus = statusData[2].toInt()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return printerStatus
    }

    private fun printerInit() {
        ThreadPoolManager.getInstance().executeTask {
            try {
                if (socket == null || !socket!!.isConnected) {
                    socket = BlutoothUtil.getSocket(mBluetoothPrinterDevice)
                }
                //Log.d(TAG,"=====printerInit======");
                val out = socket!!.outputStream
                val data = ESCUtil.init_printer()
                out.write(data, 0, data.size)
                out.close()
                socket!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

     suspend fun printOrderDetailsIposPrinter(orderDetail: OrderDetail): PrintResults{
         Toast.makeText(context, "Method Has been Called", Toast.LENGTH_LONG).show()

         return withContext(dispatchers.io) {
             val data = printOrderDetails.createPrinterData(orderDetail)
             val jsonData = gson.toJson(data)
             analytics.logEvent(AnalyticEvent.PrintReceipt(jsonData))

             ThreadPoolManager.getInstance().executeTask{
                 try {
                     val obj = JSONObject()
                     val charCode = ESCUtil.selectCharCodeSystem(0x01.toByte())
                     val printer_init = ESCUtil.init_printer()
                     val performPrint = ESCUtil.performPrintAndFeedPaper(200.toByte())
                     val cmdBytes = arrayOf(printer_init, performPrint, jsonData, charCode)
                     //val printData = ESCUtil.byteMerger(cmdBytes)

                     try {
                         if (socket == null || !socket!!.isConnected) {
                             socket = BlutoothUtil.getSocket(mBluetoothPrinterDevice)
                         }
                         val data = ESCUtil.byteMerger(cmdBytes)
                         val out = socket!!.outputStream
                         out.write(data, 0, data!!.size)
                         out.close()
                         socket!!.close()
                     } catch (e: IOException) {
                         e.printStackTrace()
                     }
                 }catch (e: UnsupportedEncodingException)
                 {e.printStackTrace()}



             }


             suspendCoroutine { continuation ->
                 val listener = object : IposPrintHelper.Listener {
                     override fun onError(e: Exception) {
                         analytics.logEvent(AnalyticEvent.PrintReceiptFail(jsonData))
                         Timber.e(e, "Error printing")
                         if (continuation.context.isActive) {
                             continuation.resumeWith(Result.success(PrintResults.Error(e)))
                         }
                     }

                     override fun onPrinterDisconnecting() {
                         analytics.logEvent(AnalyticEvent.PrinterDisconnected(jsonData))
                         if (continuation.context.isActive) {
                             continuation.resumeWith(Result.success(PrintResults.Disconnected))
                         }
                     }

                     override fun onSuccess() {
                         Timber.d("Print successful")
                         analytics.logEvent(AnalyticEvent.PrintReceiptSuccess(jsonData))
                         if (continuation.context.isActive) {
                             continuation.resumeWith(Result.success(PrintResults.Success))
                         }
                     }
                 }
                 IposPrintHelper.setListener(listener)
                 //sunmiHelper.printTableItems(data)
             }


         }


        }
    sealed class PrintResults{
        object Success : PrintResults()
        object Disconnected : IposPrinterManger.PrintResults()
        data class Error(val e: Exception) : IposPrinterManger.PrintResults()


    }




}

