package com.example.sleeptracker

import com.example.sleeptracker.objects.TimePoint

class SignUpActivityTest {

    /*fun getBinding() : ActivitySignUpBinding {
    }
    @Test
    fun `test sign up overall`(){
        getBinding().
    }*/


    fun getSleepTime() : TimePoint {
        return TimePoint(5,5)
    }


    fun getAwakeTime() : TimePoint {
        return TimePoint(10,10)
    }
}