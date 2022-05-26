package com.example.sleeptracker.utils.time

import java.util.*

val MONTHS_NAMES = arrayOf(
    "Jan",
    "Feb",
    "Mar",
    "Apr",
    "May",
    "Jun",
    "Jul",
    "Aug",
    "Sep",
    "Oct",
    "Nov",
    "Dec",

)

object IDFormatter {
    fun getID(ms:Long):String{
        val cal = Calendar.getInstance()
        cal.time = Date(ms)
        val minute = cal.get(Calendar.MINUTE)
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)
        return MONTHS_NAMES[month] + " " + day + ", "+ year+ ", " + "$hour:$minute"
    }

    fun getIDSimple(pid:String):String{
        val time = TimeUtil.pidToMS(pid)
        val cal = Calendar.getInstance()
        cal.time = time?.let { Date(it) }
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)
        return MONTHS_NAMES[month] + " " + day + ", "+ year
    }
}