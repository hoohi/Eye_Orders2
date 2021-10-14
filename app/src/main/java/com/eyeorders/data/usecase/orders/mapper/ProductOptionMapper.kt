package com.eyeorders.data.usecase.orders.mapper

import com.eyeorders.data.model.order.ProductOptionResponse
import com.eyeorders.screens.orderdetail.model.ProductOption
import com.eyeorders.util.extension.formatMoney
import com.eyeorders.util.extension.isEnglish

class ProductOptionMapper(private val language: String) :
    Mapper<ProductOptionResponse, ProductOption> {

    override fun map(entity: ProductOptionResponse): ProductOption {
        return if (language.isEnglish()) {
            ProductOption(
                entity.paramTitle,
                entity.optionTitle,
                entity.optionPrice.formatMoney()
            )
        } else {
            ProductOption(
                entity.paramTitleArab,
                entity.optionTitleArab,
                entity.optionPrice.formatMoney()
            )
        }
    }

}