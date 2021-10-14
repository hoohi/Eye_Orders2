package com.eyeorders.data.usecase.orders.mapper

import com.eyeorders.data.model.order.OrderTaxResponse
import com.eyeorders.screens.orderdetail.model.OrderTax
import com.eyeorders.util.extension.formatMoney

class OrderTaxMapper : Mapper<OrderTaxResponse, OrderTax> {

    override fun map(entity: OrderTaxResponse): OrderTax {
        return OrderTax(
            entity.taxName,
            entity.taxPercentage.toString(),
            entity.taxPrice.formatMoney()
        )
    }
}