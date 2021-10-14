package com.eyeorders.data.usecase.orders.mapper

import com.eyeorders.data.model.order.ProductExclusionResponse
import com.eyeorders.screens.orderdetail.model.ProductExclusion
import com.eyeorders.util.extension.formatMoney
import com.eyeorders.util.extension.isEnglish

class ProductExclusionMapper(private val language: String) :
    Mapper<ProductExclusionResponse, ProductExclusion> {

    override fun map(entity: ProductExclusionResponse): ProductExclusion {
        return if (language.isEnglish()) {
            ProductExclusion(
                entity.title,
                entity.description,
                entity.parameter_price.formatMoney()
            )
        } else {
            ProductExclusion(
                entity.title_arab,
                entity.description_arab,
                entity.parameter_price.formatMoney()
            )
        }
    }

}