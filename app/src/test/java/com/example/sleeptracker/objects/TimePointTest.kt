package com.example.sleeptracker.objects

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TimePointTest{

    @Test
    fun testStringToObjectFunction(){
        val r = TimePoint.stringToObject("17:15")
        print(r)
        assertThat(r?.hour==3).isTrue()
        assertThat(r?.minute==28).isTrue()
    }
}