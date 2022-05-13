package com.example.sleeptracker.database.utils

import androidx.collection.ArrayMap
import com.example.sleeptracker.objects.DaysGroup
import com.example.sleeptracker.objects.GroupType
import com.example.sleeptracker.objects.TimePoint
import com.google.firebase.database.DataSnapshot

object SnapshotExtractor {
    fun getStepsCount(snapshot: DataSnapshot):Int? {
        val count : Int? = try {
             (snapshot.child(DBParameters.ACCELEROMETER)
                    .child(DBParameters.STEPS).value as Long).toInt()
        }catch (e:Exception){
            null
        }
        return if (count==null || count <=0){
            try {
                (snapshot.child(DBParameters.ACCELEROMETER)
                        .child(DBParameters.PHONE_MOVEMENT).value as Long).toInt()
            }catch (e:Exception){
                null
            }
        }else count
    }

    fun getLightSensorReadings(snapshot: DataSnapshot): ArrayMap<String, Float> {
        val timeToValueMap:ArrayMap<String,Float>  = ArrayMap()
        try {
            snapshot.child(DBParameters.LIGHT_SENSOR).children.forEach {
                it.key?.let { it1 -> timeToValueMap[it1] = (it.value).toString().toFloat() }
            }
        }catch (e:Exception){
        }
        return timeToValueMap
    }

    fun getWorkDaysGroup(workDaysShot: DataSnapshot): DaysGroup{
        val days = arrayListOf<String>()
        workDaysShot.child(DBParameters.WEEK_DAYS).children.forEach {
            days.add(it.value.toString())
         }
        val st = getSleepTime(workDaysShot)
        val wt = getWakeUpTime(workDaysShot)
        return DaysGroup(days,GroupType.WORK_DAYS,st,wt)

    }

    fun getOffDaysGroup(offDaysShot: DataSnapshot): DaysGroup {
        val days = arrayListOf<String>()
        offDaysShot.child(DBParameters.WEEK_DAYS).children.forEach {
            days.add(it.value.toString())
        }
        val st = getSleepTime(offDaysShot)
        val wt = getWakeUpTime(offDaysShot)
        return DaysGroup(days,GroupType.OFF_DAYS,st,wt)

    }

    fun getSleepTime(snapshot: DataSnapshot):TimePoint{
        return try {
            val s = snapshot.child(DBParameters.SLEEP_TIME).value.toString()
            val r = TimePoint.stringToObject(s)
            if (r!=null)
                r
            else {
                val s2 = snapshot.child(DBParameters.SLEEP_TIME_OLD).value.toString()
                val r2 = TimePoint.stringToObject(s2)
                r2
                    ?:
                    TimePoint(0,0)
            }
        }catch (e:Exception){
            TimePoint(0,0)
        }
    }
    fun getWakeUpTime(shot: DataSnapshot):TimePoint{
        return try {
            val s = shot.child(DBParameters.WAKEUP_TIME).value.toString()
            val r = TimePoint.stringToObject(s)
            if (r!=null)
                r
            else {
                val s2 = shot.child(DBParameters.WAKEUP_TIME_OLD).value.toString()
                val r2 = TimePoint.stringToObject(s2)
                r2 ?: TimePoint(0,0)
            }
        }catch (e:Exception){
            TimePoint(0,0)
        }
    }

    fun getDeviceStateValues(deviceStatesShot: DataSnapshot):HashMap<String,String>{
        val map = HashMap<String,String>()
        try {
            deviceStatesShot.children.forEach lit@{
                if (it.key == null) return@lit
                map[it.key!!] = it.value as String
            }
        }catch (e:Exception) {}
        return map
    }

    fun getLightSensorMap(lightSensorShot: DataSnapshot): HashMap<String, String> {
        val map = HashMap<String,String>()
        try {
            lightSensorShot.children.forEach lit@{
                if (it.key == null) return@lit
                map[it.key!!] = it.value as String
            }
        }catch (e:Exception) {}
        return map
    }
    fun getAccelerometerSensorMap(accSensorShot: DataSnapshot): HashMap<String, String> {
        val map = HashMap<String,String>()
        try {
            map[DBParameters.PHONE_MOVEMENT] = accSensorShot.child(DBParameters.PHONE_MOVEMENT)
                .value as String
            map[DBParameters.CURRENT_READING] = accSensorShot.child(DBParameters.CURRENT_READING)
                .value as String
        }catch (e:Exception) {}
        return map
    }

    fun getMovementTimes(accSensorShot: DataSnapshot): HashMap<String, String> {
        val map = HashMap<String,String>()
        try {
            accSensorShot.child(DBParameters.MOVEMENT_TIMES).children
                .forEach lit@{
                    if (it.key == null) return@lit
                    map[it.key!!] = it.value as String
                }
        }catch (e:Exception) {}
        return map
    }



    fun getSleepDuration(snapshot: DataSnapshot) : String? {
        val v = snapshot.child(DBParameters.SLEEP_DURATION).value
        return  v as? String
    }

    fun getUserEmail(snapshot: DataSnapshot) : String{
        return snapshot.child(DBParameters.EMAIL).value as String
    }
    fun getUserName(snapshot: DataSnapshot) : String{
        return snapshot.child(DBParameters.NAME).value as String
    }
    fun getUserAge(snapshot: DataSnapshot) : Int{
        return (snapshot.child(DBParameters.AGE).value as Long).toInt()
    }

    fun getLastSurveyDate(snapshot: DataSnapshot) : Long{
        return snapshot
            .child(DBParameters.LAST_UPDATED)
            .value
            ?.toString()
            ?.toLong() ?: 0
    }

    fun getLastSurveyCase2Date(snapshot: DataSnapshot) : Long{
        return snapshot
            .child(DBParameters.LAST_SHOWN_CASE_2)
            .value
            ?.toString()
            ?.toLong() ?: 0
    }
}