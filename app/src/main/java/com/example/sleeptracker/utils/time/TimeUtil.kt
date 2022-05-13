package com.example.sleeptracker.utils.time

import com.example.sleeptracker.objects.Period
import com.example.sleeptracker.objects.TimePoint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

const val DAY_IN_MS : Long = 86400000

object TimeUtil {
    fun getLocalDate():String{
        val dateMs = Calendar.getInstance().timeInMillis
        return DateFormatter.getID(dateMs)
    }



    fun convertHHMMDurationToMs(duration: String):Long {
    //    duration in pattern "HH:mm" or "H:mm" convert it to ms
        return try {
            val s = duration.split(':')
            (s[0].toLong() * 60 * 60 * 1000) + (s[1].toLong() * 60 * 1000)
        }catch (e:Exception){0}

    }

    fun getIDDateFormat(ms: Long):String{
        return DateFormatter.getID(ms)
    }

//    fun getIDSimpleFormat(ms: Long):String{
//        return DateFormatter.getIDSimple(ms)
//    }

    fun getExactTime():String{
        val dateMs = Calendar.getInstance().timeInMillis
        return DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.ENGLISH)
                .format(Date(dateMs))
    }

    fun getFractionalExactTime():String{
        val dateMs = Calendar.getInstance().timeInMillis
        val pattern = "HH:mm:ss:SSS"
        val formatter = SimpleDateFormat(pattern, Locale.ENGLISH)
        return formatter.format(Date(dateMs))
    }

    fun getDateSimple():String{
        val dateMs = Calendar.getInstance().timeInMillis
        val pattern = "MMM dd, yyyy"
        val formatter = SimpleDateFormat(pattern, Locale.ENGLISH)
        return formatter.format(Date(dateMs))
    }

    fun pidToMS(pid: String):Long?{

        val pattern = "MMM dd, yyyy, HH:mm"
        val formatter = SimpleDateFormat(pattern, Locale.ENGLISH)
        return try {
            formatter.parse(pid)?.time
        }catch (e:Exception){
            return try {
                val pattern2 = "MMM dd, yyyy"
                val formatter2 = SimpleDateFormat(pattern2, Locale.ENGLISH)
                 formatter2.parse(pid)?.time
            }catch (e:Exception){
                null
            }
        }
    }

    private fun getFractionalExactDate(pid:String, timeString: String):Long?{
        val pid2 = DateFormatter.getIDSimple(pid)
        val dateMs = "$pid2 $timeString"
        val pattern = "MMM dd, yyyy HH:mm:ss:SSS"
        val formatter = SimpleDateFormat(pattern, Locale.ENGLISH)
        return try {
            formatter.parse(dateMs)?.time
        }catch (e: Exception){
            null
        }
    }

    fun getDayDateMSFloat(msDate: Long):Float{
        val dateMs = Calendar.getInstance()
        dateMs.timeInMillis = msDate
        val day = dateMs.get(Calendar.DAY_OF_MONTH)
        return day.toFloat()
    }

    fun formatTimeMS(dateMs: Long):String{
        val pattern = "HH:mm"
        val formatter = SimpleDateFormat(pattern, Locale.ENGLISH)
        return formatter.format(Date(dateMs))
    }

    fun formatTimeHMS(dateMs: Long):String{
        val pattern = "HH:mm:ss"
        val formatter = SimpleDateFormat(pattern, Locale.ENGLISH)
        return formatter.format(Date(dateMs))
    }

    fun timeFormatToMS(timeString: String, pid: String):Long?{
        val pid2 = DateFormatter.getIDSimple(pid)
        val fullDateString = "$pid2 $timeString"
        val pattern = "MMM dd, yyyy HH:mm:ss"
        val formatter = SimpleDateFormat(pattern, Locale.ENGLISH)
        return try {
            formatter.parse(fullDateString)?.time
        }catch (e: Exception){
            null
        }
    }

    fun getTimeMS(time: String, pid: String):Long?{
        val pid2 = DateFormatter.getIDSimple(pid)
        val fullDateString = "$pid2 $time"
        val pattern = "MMM dd, yyyy"
        val formatter = SimpleDateFormat(pattern, Locale.ENGLISH)
        return try {
            formatter.parse(fullDateString)?.time
        }catch (e: Exception){
            null
        }
    }

    fun dateFormatToMS(date: String):Long?{
        val pattern = "MMM dd, yyyy"
        val formatter = SimpleDateFormat(pattern, Locale.ENGLISH)
        return try {
            formatter.parse(date)?.time
        }catch (e: Exception){
            null
        }
    }
    private fun getDelayTime(timePoint: TimePoint, nowMs: Long):Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = nowMs
        calendar[Calendar.HOUR_OF_DAY] = timePoint.hour
        calendar[Calendar.MINUTE] = timePoint.minute
        return calendar.timeInMillis
    }

    fun getCorrectEventTimeMS(p: Period ,eventTimeString: String) : Long? {
        val eventTimeMS = getFractionalExactDate(p.getBasicID(),eventTimeString) ?: return null
        return when {
            eventTimeMS>=p.periodStartMS -> {
                eventTimeMS
            }
            eventTimeMS<p.periodStartMS -> {
                eventTimeMS + DAY_IN_MS
            }
            else -> null
        }
    }

    fun getStartEndPair(start: TimePoint,
                        end: TimePoint,
                        timeMS: Long
    ):Pair<Long, Long> {
        var sleepMS = getDelayTime(start, timeMS)
        var wakeMS = getDelayTime(end, timeMS)
        val thePair : Pair<Long, Long>

        if (wakeMS>sleepMS){
            if (wakeMS - sleepMS < DAY_IN_MS){
                if (wakeMS>timeMS){
                    thePair = Pair(sleepMS, wakeMS)
                }else{
                    wakeMS += DAY_IN_MS
                    if (wakeMS - sleepMS < DAY_IN_MS){
                        thePair = Pair(sleepMS, wakeMS)
                    }else{
                        sleepMS += DAY_IN_MS
                        thePair = Pair(sleepMS, wakeMS)
                    }
                }
            }else{
                sleepMS += DAY_IN_MS
                if (wakeMS>timeMS){
                    thePair = Pair(sleepMS, wakeMS)
                }else{
                    wakeMS += DAY_IN_MS
                    if (wakeMS - sleepMS < DAY_IN_MS){
                        thePair = Pair(sleepMS, wakeMS)
                    }else{
                        sleepMS += DAY_IN_MS
                        thePair = Pair(sleepMS, wakeMS)
                    }
                }
            }
        }else{
            if (wakeMS - sleepMS < DAY_IN_MS){
                if (wakeMS>timeMS){
                    sleepMS-= DAY_IN_MS
                    thePair = Pair(sleepMS, wakeMS)
                }else{
                    wakeMS+= DAY_IN_MS
                    if (wakeMS - sleepMS < DAY_IN_MS){
                        thePair = Pair(sleepMS, wakeMS)
                    }else{
                        sleepMS += DAY_IN_MS
                        thePair = Pair(sleepMS, wakeMS)
                    }
                }
            }else{
                sleepMS += DAY_IN_MS
                if (wakeMS>timeMS){
                    thePair = Pair(sleepMS, wakeMS)
                }else{
                    wakeMS += DAY_IN_MS
                    if (wakeMS - sleepMS < DAY_IN_MS){
                        thePair = Pair(sleepMS, wakeMS)
                    }else{
                        sleepMS += DAY_IN_MS
                        thePair = Pair(sleepMS, wakeMS)
                    }
                }
            }
        }

        return thePair
    }


    fun getDifferenceInPickedTimes(sleepTimePoint: TimePoint, awakeTimePoint: TimePoint):Long {

        val calendarFirst = Calendar.getInstance()

        calendarFirst[Calendar.HOUR_OF_DAY] = awakeTimePoint.hour
        calendarFirst[Calendar.MINUTE] = awakeTimePoint.minute

        val calendarLast = Calendar.getInstance()
        calendarLast[Calendar.HOUR_OF_DAY] = sleepTimePoint.hour
        calendarLast[Calendar.MINUTE] = sleepTimePoint.minute

        var diffHours : Long = if (calendarFirst[Calendar.HOUR_OF_DAY] < calendarLast[Calendar.HOUR_OF_DAY])
                ((24+calendarFirst[Calendar.HOUR_OF_DAY]) - calendarLast[Calendar.HOUR_OF_DAY]).toLong()
                        else (calendarFirst[Calendar.HOUR_OF_DAY] - calendarLast[Calendar.HOUR_OF_DAY]).toLong()

        diffHours %= 24
        val diffMinutes = if (calendarFirst[Calendar.MINUTE] < calendarLast[Calendar.MINUTE])
            (60+calendarFirst[Calendar.MINUTE]) - calendarLast[Calendar.MINUTE]
        else calendarFirst[Calendar.MINUTE] - calendarLast[Calendar.MINUTE]

        return diffHours*60*60*1000 + diffMinutes*60*1000
    }

    fun getIDSimpleFormat(ms: Long):String{
        return DateFormatter.getIDSimple(DateFormatter.getID(ms))
    }
}