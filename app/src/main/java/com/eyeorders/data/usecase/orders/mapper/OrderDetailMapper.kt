package com.eyeorders.data.usecase.orders.mapper

import com.eyeorders.data.model.order.orderdetail.OrderDetailResponse
import com.eyeorders.screens.orderdetail.model.OrderDetail

class OrderDetailMapper(
    language: String,
) : Mapper<OrderDetailResponse, OrderDetail> {

    private val orderProductMapper = OrderProductMapper(language)
    private val orderTaxMapper = OrderTaxMapper()

    override fun map(entity: OrderDetailResponse): OrderDetail {
        return OrderDetail(
            id = entity.id,
            customerAvatarPath = entity.customerAvatarPath,
            customerName = entity.customerName,
            customerPhone = entity.customerPhone,
            customerThumbPath = entity.customerThumbPath,
            customerZone = entity.customerZone,
            dateAccepted = entity.dateAccepted,
            dateCreated = entity.dateCreated,
            deliveryPrice = entity.deliveryPrice,
            notes = entity.notes,
            status = entity.status,
            orderComment = entity.orderComment,
            orderNum = entity.orderNum,
            paymentMethod = entity.paymentMethod,
            products = entity.products.map(orderProductMapper::map),
            subTotal = entity.subTotal,
            tasleemDiscount = entity.tasleemDiscount,
            taxes = entity.taxes.map(orderTaxMapper::map),
            totalPrice = entity.totalPrice,
            vendorDiscount = entity.vendorDiscount
        )
    }

}