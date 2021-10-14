package com.eyeorders.data.mockdata.fakes

import com.eyeorders.data.mockdata.fakes.PrimitiveDataFactory.randomInt
import com.eyeorders.data.mockdata.fakes.dictionaries.DictionaryCookery
import com.eyeorders.data.mockdata.fakes.dictionaries.DictionaryLatin
import com.eyeorders.data.mockdata.fakes.dictionaries.DictionarySciFi

object Generators {

    private val cookeryDict = DictionaryCookery()
    private val latinDict = DictionaryLatin()
    private val sciFiDict = DictionarySciFi()

    fun createFakeItemName(): String {
        return cookeryDict.getRandomLine(randomInt(to = 3))
    }

    fun createFakeUserName(): String {
        return latinDict.getRandomLine(randomInt(to = 2), 2)
    }

    fun createFakeAddress(): String {
        return sciFiDict.getRandomLine(randomInt(to = 4))
    }

    fun createFakePhoneNumber(): String {
        var phone = ""
        repeat(11) {
            phone = phone.plus(randomInt(0, 10))
        }
        return phone
    }
}