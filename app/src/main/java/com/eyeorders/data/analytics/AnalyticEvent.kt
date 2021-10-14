package com.eyeorders.data.analytics

sealed class AnalyticEvent(
    val eventName: String,
    val parameters: Map<String, Any>? = null,
) {
    data class ServerErrorEvent(
        val responseBody: String,
    ) : AnalyticEvent(
        "server_error",
        mapOf("response_body" to responseBody)
    )


    data class PrintReceipt(val printerData: String) :
        AnalyticEvent("print_receipt", mapOf("printer_data" to printerData))


    data class PrintReceiptSuccess(val printerData: String) :
        AnalyticEvent("print_receipt_success", mapOf("printer_data" to printerData))

    data class PrintReceiptFail(val printerData: String) :
        AnalyticEvent("print_receipt_error", mapOf("printer_data" to printerData))

    data class PrinterDisconnected(val printerData: String) :
        AnalyticEvent("print_receipt_error", mapOf("printer_data" to printerData))


    object ShowPopup : AnalyticEvent("show_order_popup")
    object PlayOrderReceiveSound : AnalyticEvent("play_order_received_alert")
    object StopOrderReceiveSound : AnalyticEvent("stop_order_received_alert")
    object PlayLostConnectionSound : AnalyticEvent("play_no_connection_alert")
    object StopLostConnectionSound : AnalyticEvent("stop_no_connection_alert")
    object ReceivePushyNotification : AnalyticEvent("receive_pushy")

    sealed class ScreenViewEvent(val screenName: String, val screenClassName: String) :
        AnalyticEvent("screen_view") {
        object Default : ScreenViewEvent("default_screen", "default_class")
        data class ActiveOrdersScreen(val className: String) :
            ScreenViewEvent("screen_active_orders", className)

        data class DateScreen(val className: String) : ScreenViewEvent("screen_date", className)
        data class HelpScreen(val className: String) : ScreenViewEvent("screen_help", className)
        data class LoginScreen(val className: String) : ScreenViewEvent("screen_login", className)
        data class MenuScreen(val className: String) : ScreenViewEvent("screen_menu", className)
        data class MenuManagementScreen(val className: String) :
            ScreenViewEvent("screen_menu_mgmt", className)

        data class NewOrderScreen(val className: String) :
            ScreenViewEvent("screen_new_order", className)

        data class OrderDetailScreen(val className: String) :
            ScreenViewEvent("screen_order_detail", className)

        data class PastOrdersScreen(val className: String) :
            ScreenViewEvent("screen_past_order", className)

        data class ProductAvailabilityScreen(val className: String) :
            ScreenViewEvent("screen_product_availability", className)

        data class RecentOrdersScreen(val className: String) :
            ScreenViewEvent("screen_product_availability", className)

        data class StoreStatusScreen(val className: String) :
            ScreenViewEvent("screen_store_status", className)

        data class TestConnectionScreen(val className: String) :
            ScreenViewEvent("screen_test_connection", className)

        data class TodayOrdersScreen(val className: String) :
            ScreenViewEvent("screen_todays_orders", className)

        data class WorkHoursScreen(val className: String) :
            ScreenViewEvent("screen_work_hours", className)
    }
}
