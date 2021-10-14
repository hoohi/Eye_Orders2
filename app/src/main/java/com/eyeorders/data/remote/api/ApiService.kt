package com.eyeorders.data.remote.api

import com.eyeorders.data.model.OrderStatus
import com.eyeorders.data.model.menu.VendorCategory
import com.eyeorders.data.model.order.OrderResponse
import com.eyeorders.data.model.order.orderdetail.OrderDetailResponse
import com.eyeorders.data.remote.api.response.ApiResponse
import com.eyeorders.data.remote.api.response.MenuProductResponse
import com.eyeorders.data.remote.api.response.OrdersApiResponse
import com.eyeorders.data.remote.api.response.WorkHoursResponse
import com.eyeorders.data.remote.api.response.login.LoginResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @POST("index.php?r=api/account-retailer/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("deviceToken") deviceToken: String,
        @Field("devicePlatform") devicePlatform: String,
    ): ApiResponse<LoginResponse>

    @POST("index.php?r=api/products/orders")
    @FormUrlEncoded
    suspend fun getOrders(
        @Field("store_id") userId: Int,
        @Field("status")
        status: String,
        @Field("page") page: Int,
    ): OrdersApiResponse<OrderResponse>


    @POST("index.php?r=api/products/orders")
    @FormUrlEncoded
    suspend fun getRecentOrdersFromStartToEnd(
        @Field("store_id") userId: Int,
        @Field("startDate") startDate: String,
        @Field("endDate") endDate: String,
        @Field("status") status: String = "${OrderStatus.RECEIVED}"
    ): OrdersApiResponse<OrderResponse>

    @POST("index.php?r=api/category/vendor-subcategories")
    @FormUrlEncoded
    suspend fun getCategories(
        @Field("vendor_id") userId: Int,
    ): ApiResponse<VendorCategory>

    @POST("index.php?r=api/products/listing")
    @FormUrlEncoded
    suspend fun getProducts(
        @Field("vendor_id") userId: Int,
        @Field("subcategory_id") categoryId: Int,
        @Field("page") page: Int,
    ): ApiResponse<MenuProductResponse>

    @POST("index.php?r=api/products/change_stop_order")
    @FormUrlEncoded
    suspend fun changeStoreStatus(
        @Field("vendor_id") userId: Int,
        @Field("status") status: Int,
    ): ApiResponse<Unit>

    @POST("index.php?r=api/products/order-detail")
    @FormUrlEncoded
    suspend fun getOrderDetail(
        @Field("store_id") userId: Int,
        @Field("id") orderId: Int,
    ): ApiResponse<OrderDetailResponse>

    @POST("index.php?r=api/products/change")
    @FormUrlEncoded
    suspend fun changeProductStatus(
        @Field("vendor_id") userId: Int,
        @Field("id") productId: Int,
        @Field("status") status: Int,
    ): ApiResponse<Any>

    @POST("index.php?r=api/products/confirm-order")
    @FormUrlEncoded
    suspend fun confirmOrder(
        @Field("store_id") userId: Int,
        @Field("id") orderId: Int,
        @Field("min") min: Int,
        @Field("order_version") orderVersion: Int,
    ): ApiResponse<Unit>

    @POST("index.php?r=api/products/ready-order")
    @FormUrlEncoded
    suspend fun markOrderAsReady(
        @Field("store_id") userId: Int,
        @Field("id") orderId: Int,
    ): ApiResponse<Unit>

    @POST("index.php?r=api/products/cancel-order")
    @FormUrlEncoded
    suspend fun declineOrder(
        @Field("vendor_id") userId: Int,
        @Field("id") orderId: Int,
    ): ApiResponse<Unit>

    @POST("index.php?r=api/account-retailer/work-schedule")
    @FormUrlEncoded
    suspend fun getWorkHours(
        @Field("id") userId: Int,
    ): ApiResponse<WorkHoursResponse>
}
