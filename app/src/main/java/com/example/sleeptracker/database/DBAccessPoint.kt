package com.example.sleeptracker.database

import com.example.sleeptracker.database.utils.DBParameters
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


object DBAccessPoint {
    private val instance: FirebaseDatabase = Firebase.database
    private const val TRACKER_DIR = "Tracker"
    private const val DEVICE_STATE_DIR = "Device State"
    private const val ACCELEROMETER_SENSOR_DIR = "Accelerometer"
    const val MOVEMENT_TIMES_DIR = "movement times"
    private const val USERS_DB = "users"
    private const val USER_SURVEYS = "surveys"
    private const val SESSIONS = "Sessions"



    private fun getMainUsersDBInstance(): DatabaseReference {
        return instance.getReference(USERS_DB)
    }


    fun getUserDirectoryReference(uid:String): DatabaseReference {
        return getMainUsersDBInstance().child(uid)
    }


    fun getWorkDaysDirectoryReference(uid:String): DatabaseReference {
        return getMainUsersDBInstance().child(uid).child(DBParameters.WORK_DAY)
    }

    fun getOffDaysDirectoryReference(uid:String): DatabaseReference {
        return getMainUsersDBInstance().child(uid).child(DBParameters.OFF_DAY)
    }

    fun getDeviceStateDirectoryReference(uid:String,period:String): DatabaseReference {
        val path = "$uid/$TRACKER_DIR/$period/$DEVICE_STATE_DIR"
        return getMainUsersDBInstance().child(path)
    }

    fun getAccelerometerSensorDirectoryReference(uid:String,period:String): DatabaseReference {
        val path = "$uid/$TRACKER_DIR/$period/$ACCELEROMETER_SENSOR_DIR"
        return getMainUsersDBInstance().child(path)
    }

    fun getDeviceMovementTimesDirectory(uid:String,period:String): DatabaseReference {
        val dir = getAccelerometerSensorDirectoryReference(uid,period)
        return dir.child(MOVEMENT_TIMES_DIR)
    }

    fun getPeriodDirectoryReference(uid:String, pid:String): DatabaseReference {
        val path = "$uid/$TRACKER_DIR/$pid/"
        return getMainUsersDBInstance().child(path)
    }

    fun queryPeriods(uid:String,periods:Array<String>): ArrayList<Query> {

        val queries: ArrayList<Query> = arrayListOf()
        periods.forEach {
            val path = "$uid/$TRACKER_DIR/$it/"
            queries.add(getMainUsersDBInstance().child(path))
        }
        return queries
    }

    fun getTrackerDirectory(uid:String):DatabaseReference{
        val path = "$uid/$TRACKER_DIR/"
        return getMainUsersDBInstance().child(path)
    }

    fun getSessionsDirectory(uid:String, pid:String): DatabaseReference {
        val path = "$uid/$TRACKER_DIR/$pid/$SESSIONS"
        return getMainUsersDBInstance().child(path)
    }

    fun getUserSurveysReference(uid:String): DatabaseReference {
        return getMainUsersDBInstance().child(uid).child(USER_SURVEYS)
    }

    fun getUserSurveyLastConditionOne(uid:String): DatabaseReference {
        return getMainUsersDBInstance().child(uid).child(USER_SURVEYS)
            .child(DBParameters.LAST_UPDATED)
    }

    fun getUserSurveyLastConditionTwo(uid:String): DatabaseReference {
        return getMainUsersDBInstance().child(uid).child(USER_SURVEYS)
            .child(DBParameters.LAST_SHOWN_CASE_2)
    }

    fun getUserSurveyConditionTwo(uid:String): DatabaseReference {
        return getMainUsersDBInstance().child(uid).child(USER_SURVEYS)
            .child(DBParameters.CONDITION_2)
    }

    fun getPeriodSleepTime(uid: String, period: String): DatabaseReference {
        val path = "$uid/$TRACKER_DIR/$period/Sleep Time"
        return getMainUsersDBInstance().child(path)
    }

    fun getPeriodWakeTime(uid: String, period: String): DatabaseReference {
        val path = "$uid/$TRACKER_DIR/$period/Wake up Time"
        return getMainUsersDBInstance().child(path)
    }

    init {
        instance.setPersistenceEnabled(true)

    }


}