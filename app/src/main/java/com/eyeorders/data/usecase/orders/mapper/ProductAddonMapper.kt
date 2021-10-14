package com.eyeorders.data.usecase.orders.mapper

import com.eyeorders.data.model.order.ProductAddonResponse
import com.eyeorders.screens.orderdetail.model.ProductAddon
import com.eyeorders.util.extension.formatMoney
import com.eyeorders.util.extension.isEnglish

class ProductAddonMapper(private val language: String) :
    Mapper<ProductAddonResponse, ProductAddon> {

    override fun map(entity: ProductAddonResponse): ProductAddon {
        return if (language.isEnglish()) {
            ProductAddon(
                entity.title,
                entity.description,
                entity.addon_price.formatMoney()
            )
        } else {
            ProductAddon(
                entity.title_arab,
                entity.description_arab,
                entity.addon_price.formatMoney()
            )
        }
    }

}