package com.eyeorders.util.args

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class FragmentArgsKtTest {

    @Test
    fun testClass(){
        val result = Int::class.java.isAssignableFrom(Int::class.java)
        assertThat(true, `is`(result))
    }
}