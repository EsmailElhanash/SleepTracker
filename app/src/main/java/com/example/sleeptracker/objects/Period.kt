package com.example.sleeptracker.objects

import com.amplifyframework.core.Amplify
import com.example.sleeptracker.database.DBAccessPoint
import com.example.sleeptracker.utils.MINUTE_IN_MS
import com.example.sleeptracker.utils.time.DateFormatter
import com.example.sleeptracker.utils.time.TimeUtil
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

data class Period(
        val periodStartMS : Long,
        val periodEndMS : Long,
){
    var periodPortions : ArrayList<Period> = arrayListOf()
    init {
        if(periodEndMS-periodStartMS>10* MINUTE_IN_MS){
            makePeriodPortions()
        }
    }

    override fun toString(): String {
        return this.getPeriodID()
    }

    fun getPeriodID():String {
        return DateFormatter.getID(periodStartMS)
    }
    fun getBasicID():String {
        return DateFormatter.getIDSimple(getPeriodID())
    }

    fun getMinuteRangePeriodID():String {
        return "${TimeUtil.formatTimeMS(periodStartMS)}-${TimeUtil.formatTimeMS(periodEndMS)}"
    }

    fun getStartTime():String {
        return TimeUtil.formatTimeMS(periodStartMS)
    }

    fun getEndTime():String{
        return TimeUtil.formatTimeMS(periodEndMS)
    }

    private fun makePeriodPortions() {
        val step = 10 * MINUTE_IN_MS
        for (p in periodStartMS..periodEndMS step step){
            periodPortions.add(Period(p+1,p+step))
        }
    }

}

