package com.eyeorders.data.usecase.orders.mapper

import com.eyeorders.data.model.order.OrderProductResponse
import com.eyeorders.screens.orderdetail.model.OrderProduct
import com.eyeorders.util.extension.formatMoney
import com.eyeorders.util.extension.isEnglish

class OrderProductMapper(
    private val language: String
) : Mapper<OrderProductResponse, OrderProduct> {

    private val productOptionMapper = ProductOptionMapper(language)
    private val productAddonMapper = ProductAddonMapper(language)
    private val productExclusionMapper = ProductExclusionMapper(language)

    override fun map(entity: OrderProductResponse): OrderProduct {
        return OrderProduct(
            id = entity.id,
            title = if (language.isEnglish()) {
                entity.title
            } else {
                entity.titleArab
            },
            amount = entity.amount.toInt(),
            productPrice = entity.productPrice.formatMoney(),
            productSingleOriginalPrice = entity.productSingleOriginalPrice.formatMoney(),
            productDiscountPercentage = entity.productDiscountPercentage.toString(),
            singleDiscount = entity.singleDiscount.toString(),
            options = entity.options.map(productOptionMapper::map),
            addons = entity.addons.map(productAddonMapper::map),
            excluded = entity.excluded.map(productExclusionMapper::map),
        )
    }
}