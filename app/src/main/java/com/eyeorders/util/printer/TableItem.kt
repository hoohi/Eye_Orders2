package com.eyeorders.util.printer

class TableItem {
    var text: Array<String>
    var width: IntArray
    var align: IntArray
    var fontSize = 15f // default is 13
        private set
    private var isBold = true
    var lineFeedCount = 0

    constructor(text: Array<String>, width: IntArray) {
        this.text = text
        this.width = width
        align = intArrayOf(0, 0, 0)
    }

    constructor(text: Array<String>, width: IntArray, isBold: Boolean) {
        this.text = text
        this.width = width
        align = intArrayOf(0, 0, 0)
        this.isBold = isBold
    }

    constructor(text: Array<String>, width: IntArray, fontSize: Int) {
        this.text = text
        this.width = width
        align = intArrayOf(0, 0, 0)
        this.fontSize = fontSize.toFloat()
    }

    constructor(
        text: Array<String>,
        width: IntArray,
        align: IntArray,
        fontSize: Int,
        isBold: Boolean
    ) {
        this.text = text
        this.width = width
        this.align = align
        this.fontSize = fontSize.toFloat()
        this.isBold = isBold
    }

    constructor(text: Array<String>, width: IntArray, fontSize: Int, isBold: Boolean) {
        this.text = text
        this.width = width
        align = intArrayOf(0, 0, 0)
        this.fontSize = fontSize.toFloat()
        this.isBold = isBold
    }


    val boldData: ByteArray
        get() = if (isBold) byteArrayOf(0x1B, 0x45, 0x1) else byteArrayOf(0x1B, 0x45, 0x0)

    val valueText: String
        get() {
            var value = ""
            for (s in text) {
                if (s != "") {
                    value = s
                    break
                }
            }
            return value
        }
}