package com.eyeorders.util.printer

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import com.eyeorders.data.prefs.PrefsDataManager
import com.eyeorders.screens.orderdetail.model.OrderDetail
import com.eyeorders.util.StringResource
import com.eyeorders.util.date.DateFormatter
import com.eyeorders.util.extension.formatMoney
import com.eyeorders.util.extension.isEnglish
import com.eyeorders.util.printer.sumni.PrintingFontSize.*
import com.eyeorders.util.printer.iposprinter.ESCUtil.fontSizeSet
import com.tasleem.orders.R
import org.apache.commons.text.WordUtils
import javax.inject.Inject

/*
        Estimated maximum number characters per line is 25
* */
class
PrintOrderDetails @Inject constructor(
    private val prefsDataManager: PrefsDataManager,
    private val dateFormatter: DateFormatter,
    private val stringResource: StringResource,
) {

    @SuppressLint("DefaultLocale")
    suspend fun createPrinterData(orderDetails: OrderDetail): List<TableItem> {
        val language = prefsDataManager.getLanguage()
        val vendor = prefsDataManager.getUser()

        // print data
        val printTable = mutableListOf<TableItem>()

        printTable.add(
            TableItem(
                arrayOf(
                    "",
                    "Tasleem #${orderDetails.orderNum}",
                    ""
                ),
                intArrayOf(0, 1, 0),
                intArrayOf(0, 1, 0),
                xxxLargeFontSize,
                true
            )
        )

        printTable.add(
            TableItem(
                arrayOf(
                    "",
                    (if (language.isEnglish()) vendor.companyName else vendor.companyNameArab),
                    ""
                ),
                intArrayOf(0, 1, 0),
                intArrayOf(0, 1, 0),
                largeFontSize,
                true
            )
        )

        printTable.add(
            TableItem(
                arrayOf(
                    "",
                    orderDetails.customerZone,
                    ""
                ),
                intArrayOf(0, 1, 0),
                intArrayOf(0, 1, 0),
                mediumFontSize,
                true
            )
        )

        printTable.add(
            TableItem(
                arrayOf(
                    "",
                    "#${orderDetails.id}",
                    ""
                ), intArrayOf(0, 1, 0), intArrayOf(0, 1, 0), largeFontSize, false
            )
        )

        printTable.add(
            TableItem(
                arrayOf(
                    "",
                    dateFormatter.formatToPrinterDate(orderDetails.dateCreated),
                    ""
                ), intArrayOf(0, 1, 0), intArrayOf(0, 1, 0), largeFontSize, false
            ).apply {
                lineFeedCount = 2 //add some space
            }
        )




        printTable.add(
            TableItem(
                arrayOf("", getString(R.string.printer_customer_label), ""),
                intArrayOf(0, 1, 0),
                intArrayOf(0, 1, 0),
                largeFontSize,
                true
            )
        )

        printTable.add(
            TableItem(
                arrayOf("", orderDetails.customerName, ""),
                intArrayOf(0, 1, 0),
                intArrayOf(0, 1, 0),
                largeFontSize,
                true
            )
        )

        printTable.add(
            TableItem(
                arrayOf(
                    "",
                    orderDetails.customerPhone,
                    ""
                ),
                intArrayOf(0, 1, 0),
                intArrayOf(0, 1, 0),
                largeFontSize,
                true
            )
        )

        printTable.add(
            TableItem(
                arrayOf(
                    "",
                    orderDetails.paymentMethod.toUpperCase(),
                    ""
                ),
                intArrayOf(0, 1, 0),
                intArrayOf(0, 1, 0),
                xLargeFontSize,
                true
            )
        )


        val numberOfItems = orderDetails.products.fold(0) { acc, orderProduct ->
            acc + orderProduct.amount
        }

        //number of items
        printTable.add(
            TableItem(
                arrayOf(
                    "",
                    getString(
                        R.string.new_order_items,
                        numberOfItems
                    ),
                    ""
                ),
                intArrayOf(0, 1, 0),
                intArrayOf(0, 1, 0),
                largeFontSize,
                false
            )
        )

        printTable.add(
            TableItem(
                arrayOf("", "____________________________", ""),
                intArrayOf(0, 1, 0),
                intArrayOf(0, 1, 0),
                largeFontSize,
                true
            )
        )

        for (j in orderDetails.products.indices) {
            val (_, title, amount, productPrice, _, _, _, options, addons, excludeds) = orderDetails.products[j]
            val orderAmount = amount.toString()
            val wrappedTitle = title.wrap()
            val wrapTitleChunks = wrappedTitle.split("\n")

            wrapTitleChunks.forEachIndexed { index, string ->
                if (index == 0) {
                    printTable.add(
                        TableItem(
                            arrayOf(
                                getString(R.string.printer_order_count_label, orderAmount),
                                " $string",
                                productPrice.formatWithCurrency()
                            ), intArrayOf(2, 14, 9), largeFontSize, false
                        )
                    )
                } else {
                    printTable.add(
                        TableItem(
                            arrayOf(
                                "",
                                string,
                                ""
                            ), intArrayOf(2, 14, 9), largeFontSize, false
                        )
                    )
                }
            }

            if (options.isNotEmpty()) {
                for (i in options.indices) {
                    val (_, optionTitle, optionPrice) = options[i]
                    val wrappedOptionTitle = optionTitle?.wrap()
                    val wrappedOptionTitles = wrappedOptionTitle?.split("\n")
                    wrappedOptionTitles?.forEachIndexed { index, string ->
                        if (index == 0) {
                            val price =
                                if (optionPrice.toDoubleOrNull() ?: 0.0 > 0.0) optionPrice.formatWithCurrency() else ""
                            printTable.add(
                                TableItem(
                                    arrayOf(
                                        "",
                                        "${
                                            getString(
                                                R.string.printer_order_count_label,
                                                orderAmount
                                            )
                                        } $string",
                                        price
                                    ), intArrayOf(1, 15, 9), mediumFontSize, false
                                )
                            )
                        } else {
                            printTable.add(
                                TableItem(
                                    arrayOf(
                                        "",
                                        string,
                                        ""
                                    ),
                                    intArrayOf(1, 15, 9), mediumFontSize, false
                                )
                            )
                        }
                    }
                }
            }

            if (addons.isNotEmpty()) {
                for (i in addons.indices) {
                    val (title1, _, price) = addons[i]
                    val title1Wrapped = title1.wrap()
                    val wrappedChunks = title1Wrapped.split("\n")
                    val addonPrice = if (price.toDouble() > 0) price.formatWithCurrency() else ""
                    wrappedChunks.forEachIndexed { index, string ->
                        if (index == 0) {
                            printTable.add(
                                TableItem(
                                    arrayOf(
                                        "",
                                        "+$orderAmount $string",
                                        addonPrice
                                    ), intArrayOf(1, 15, 9), mediumFontSize, false
                                )
                            )
                        } else {
                            printTable.add(
                                TableItem(
                                    arrayOf(
                                        "",
                                        " $string",
                                        ""
                                    ), intArrayOf(1, 15, 9), mediumFontSize, false
                                )
                            )
                        }
                    }
                }
            }

            if (excludeds.isNotEmpty()) {
                for (i in excludeds.indices) {
                    val (title1, _, parameterPrice) = excludeds[i]
                    val title1Wrapped = title1.wrap()
                    val wrappedChunks = title1Wrapped.split("\n")
                    val addonPrice =
                        if (parameterPrice.toDouble() > 0) parameterPrice.formatWithCurrency() else ""
                    wrappedChunks.forEachIndexed { index, string ->
                        if (index == 0) {
                            printTable.add(
                                TableItem(
                                    arrayOf(
                                        "",
                                        "-$orderAmount $string",
                                        addonPrice

                                    ), intArrayOf(1, 15, 9), mediumFontSize, false
                                )
                            )
                        } else {
                            printTable.add(
                                TableItem(
                                    arrayOf(
                                        "",
                                        " $string",
                                        ""

                                    ), intArrayOf(1, 15, 9), mediumFontSize, false
                                )
                            )
                        }
                    }
                }
            }

            //extra space between products
            if (j != orderDetails.products.size - 1) printTable.add(
                TableItem(
                    arrayOf(
                        "",
                        "",
                        ""
                    ), intArrayOf(0, 1, 0), intArrayOf(0, 1, 0), mediumFontSize, true
                )
            )
        }

        printTable.add(
            TableItem(
                arrayOf("", "____________________________", ""),
                intArrayOf(0, 1, 0),
                intArrayOf(0, 1, 0),
                largeFontSize,
                true
            )
        )

        if (orderDetails.notes.isNotEmpty() && orderDetails.notes != "null") {
            printTable.add(
                TableItem(
                    arrayOf("", orderDetails.notes, ""),
                    intArrayOf(0, 1, 0),
                    largeFontSize,
                    false
                )
            )
            printTable.add(
                TableItem(
                    arrayOf("", "____________________________", ""),
                    intArrayOf(0, 1, 0),
                    intArrayOf(0, 1, 0),
                    largeFontSize,
                    true
                )
            )
        }

        printTable.add(
            TableItem(
                arrayOf(
                    getString(R.string.printer_subtotal).plus(" "),
                    "",
                    orderDetails.totalPrice.formatWithCurrency()
                ), intArrayOf(1, 0, 1), intArrayOf(0, 0, 2), xLargeFontSize, true
            )
        )

        if (orderDetails.taxes.isNotEmpty()) {
            for (i in orderDetails.taxes.indices) {
                printTable.add(
                    TableItem(
                        arrayOf(
                            orderDetails.taxes[i].taxName + "(" + orderDetails.taxes[i].taxPercentage + "%)",
                            "",
                            orderDetails.taxes[i].taxPrice.formatWithCurrency()
                        ), intArrayOf(1, 0, 1), intArrayOf(0, 0, 2), mediumFontSize, false
                    )
                )
            }
        }

        if (orderDetails.vendorDiscount > 0) {
            printTable.add(
                TableItem(
                    arrayOf(
                        getString(R.string.order_detail_vendor_discount).plus(" "),
                        "",
                        "- " + orderDetails.vendorDiscount.formatWithCurrency()
                    ), intArrayOf(1, 0, 1), intArrayOf(0, 0, 2), largeFontSize, true
                )
            )
        }

        if (orderDetails.tasleemDiscount > 0) {
            printTable.add(
                TableItem(
                    arrayOf(
                        getString(R.string.order_detail_tasleem_discount).plus(" "),
                        "",
                        "- " + orderDetails.tasleemDiscount.formatWithCurrency()
                    ), intArrayOf(1, 0, 1), intArrayOf(0, 0, 2), largeFontSize, true
                )
            )
        }

        printTable.add(
            TableItem(
                arrayOf(
                    getString(R.string.printer_service_fee).plus(" "),
                    "",
                    orderDetails.deliveryPrice.formatWithCurrency()
                ), intArrayOf(1, 0, 1), intArrayOf(0, 0, 2), largeFontSize, true
            ).apply {
                lineFeedCount = 2
            }
        )


        printTable.add(
            TableItem(
                arrayOf(
                    getString(R.string.printer_total).plus(" "),
                    "",
                    (orderDetails.totalPrice + orderDetails.deliveryPrice).formatWithCurrency()
                ), intArrayOf(1, 0, 2), intArrayOf(0, 0, 2), xxxLargeFontSize, true
            )
        )


        printTable.add(
            TableItem(
                arrayOf(
                    getString(R.string.printer_vat_incl),
                    "",
                    ""
                ), intArrayOf(1, 0, 0), intArrayOf(1, 0, 0), largeFontSize, true
            )
        )

        return printTable
    }

    private fun String.formatWithCurrency(): String {
        return getString(R.string.amount_currency, this)
    }

    private fun Double.formatWithCurrency(): String {
        return this.formatMoney().formatWithCurrency()
    }

    private fun getString(@StringRes resId: Int, vararg args: Any): String {
        return stringResource.getString(resId, *args)
    }

    private fun String.wrap(lineLength: Int = LINE_LENGTH): String {
        return WordUtils.wrap(this, lineLength)
    }

    companion object {
        private const val LINE_LENGTH = 12
    }
}