package com.eyeorders.data.model

import androidx.annotation.IntDef

@IntDef(
    OrderStatus.CREATED,
    OrderStatus.PENDING,
    OrderStatus.VENDOR_CONFIRM,
    OrderStatus.DRIVER_PENDING,
    OrderStatus.PICKUP,
    OrderStatus.NEW_ORDER_READY,
    OrderStatus.ONWAY,
    OrderStatus.ARRIVED,
    OrderStatus.RECEIVED,
    OrderStatus.NOT_PAYED,
    OrderStatus.NOT_DELIVERED,
    OrderStatus.CANCELED,
    OrderStatus.VENDOR_CANCELED,
    OrderStatus.RETURNED,
    OrderStatus.REMOVED,
)
annotation class OrderStatus {
    companion object {
            /**
             * Customer added product to card (not confirm)
             */
            const val CREATED = 0

            /**
             * payment = true, driver = false
             * NEW ORDER
             */
            const val PENDING = 1

            /**
             * Order confirmed by customer and vendor
             * ACCEPTED ORDER
             */
            const val VENDOR_CONFIRM = 2

            /**
             * Driver took order and heading to retailer
             */
            const val DRIVER_PENDING = 3

            /**
             * Driver get order from retailer and not start delivery
             */
            const val PICKUP = 14

            /**
             * if vendor confirms and preparation time finish
             */
            const val NEW_ORDER_READY = 13

            /**
             * Driver get order from retailer and heading to customer location
             * payment = true, driver = true
             */
            const val ONWAY = 4

            /**
             * Driver arrived to customer location
             */
            const val ARRIVED = 5

            /**
             * Driver arrived to customer location, customer accepted order
             */
            const val RECEIVED = 6

            /**
             * Not Payed (Driver arrived to customer, but customer didn't show up)
             */
            const val NOT_PAYED = 7

            /**
             * Driver took order, but never arrived to customer location
             */
            const val NOT_DELIVERED = 8

            /**
             * Order canceled
             */
            const val CANCELED = 9

            /**
             * vendor canceled order
             */
            const val VENDOR_CANCELED = 12

            /**
             * Customer returned product
             */
            const val RETURNED = 10

            /**
             * Customer removed product from cart
             */
            const val REMOVED = 11
    }
}
