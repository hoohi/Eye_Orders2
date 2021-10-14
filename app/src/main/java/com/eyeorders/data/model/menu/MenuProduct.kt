package com.eyeorders.data.model.menu

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class MenuProduct(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("title_arab") val titleArab: String,
    @SerializedName("in_stock") val inStock: Boolean,
    @SerializedName("description") val description: String = "",
    @SerializedName("description_arab") val descriptionArab: String = "",
    @SerializedName("rate") val rate: Int = 0,
    @SerializedName("vendor_id") val vendorId: Int = 0,
    @SerializedName("category_id") val categoryId: Int = 0,
    @SerializedName("subcategory_id") val subcategoryId: Int = 0,
    @SerializedName("favorites") val favorites: Int = 0,
    @SerializedName("discount_percentage") val discountPercentage: Int = 0,
    @SerializedName("preparation_time") val preparationTime: Int = 0,
    @SerializedName("currency") val currency: String? = "",
    @SerializedName("price") val price: Double = 0.0,
    @SerializedName("is_removed") val isRemoved: Boolean = false,
    @SerializedName("is_unlimited") val isUnlimited: Boolean = false,
    @SerializedName("date_created") val dateCreated: Long = 0L,
    @SerializedName("date_updated") val dateUpdated: Long = 0L,
    @SerializedName("current_quantity") val currentQuantity: Int = 0,
    @SerializedName("is_deleted") val isDeleted: Boolean = false,
    @SerializedName("category") val category: String = "",
    @SerializedName("category_arab") val categoryArab: String = "",
    @SerializedName("subcategory") val subcategory: String = "",
    @SerializedName("subcategory_arab") val subcategoryArab: String = "",
    @SerializedName("image_path") val imagePath: String = "",
)