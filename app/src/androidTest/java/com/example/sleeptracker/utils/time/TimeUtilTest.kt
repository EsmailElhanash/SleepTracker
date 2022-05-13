package com.example.sleeptracker.utils.time

import org.junit.Test

class TimeUtilCustomTest {


    private var pid : String? = "May 20, 2021"

    @Test
    fun getFractionalExactDateTest(){
        val t = pid?.let { TimeUtil.getFractionalExactDate(it,"12:00:00:000") }
        print(t)
    }
}