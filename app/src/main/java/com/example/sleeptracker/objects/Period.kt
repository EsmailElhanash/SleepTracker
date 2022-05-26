package com.example.sleeptracker.objects

import com.example.sleeptracker.utils.MINUTE_IN_MS
import com.example.sleeptracker.utils.time.IDFormatter
import com.example.sleeptracker.utils.time.TimeUtil

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
        return IDFormatter.getID(periodStartMS)
    }
    fun getBasicID():String {
        return IDFormatter.getIDSimple(getPeriodID())
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

