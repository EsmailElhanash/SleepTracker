package com.example.sleeptracker.database.models

import android.util.Log
import android.util.TimeUtils
import com.example.sleeptracker.database.DBAccessPoint
import com.example.sleeptracker.models.SleepPeriod
import com.example.sleeptracker.objects.Period
import com.example.sleeptracker.utils.MINUTE_IN_MS
import com.example.sleeptracker.utils.time.DAY_IN_MS
import com.example.sleeptracker.utils.time.TimeUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Test

@DelicateCoroutinesApi
class SleepPeriodLiveTestAndroid{
    private var pid : String = "Oct 13, 2021"
    lateinit var  sleepDataSnapshot : DataSnapshot
    private var period : SleepPeriod? = null

    @DelicateCoroutinesApi
    @Before
    fun before(){
        val uid = Firebase.auth.uid ?: return
        GlobalScope.launch(Dispatchers.Main) {
            DBAccessPoint.getPeriodDirectoryReference(uid,pid)
                .get().addOnSuccessListener {
                    val st = TimeUtil.getTimeMS(it.child("Sleep Time").value.toString(),pid )
                    var wt = TimeUtil.getTimeMS(it.child("Wake up Time").value.toString(),pid )

                    if (st == null || wt == null) return@addOnSuccessListener
                    if (wt<st) wt+= DAY_IN_MS
                    period = SleepPeriod(Period(st,wt))
                }
        }

        Thread.sleep(10000)
    }

    @DelicateCoroutinesApi
    @Test/*(timeout = 50000)*/
    fun testCalculateSessions(){
        GlobalScope.launch(Dispatchers.Main) {
//            period.deviceStates.value?.putAll(SnapshotExtractor.getPeriodDeviceStates(sleepDataSnapshot))
            period?.calculateSessions{
                Log.d("sessions", ": $it")
            }
        }
        Thread.sleep(50000)
    }

    @DelicateCoroutinesApi
    @Test/*(timeout = 50000)*/
    fun testCalculateSleepDuration(){
        GlobalScope.launch(Dispatchers.Main) {
//            period.deviceStates.value?.putAll(SnapshotExtractor.getPeriodDeviceStates(sleepDataSnapshot))
            period?.calculateSleepDuration{
                Log.d("getSleepingDurationInNumbers", ": ${getSleepingDurationInNumbers(it)}")
            }
        }
        Thread.sleep(50000)
    }
//    @Test
//    fun saveStepTime(){
//        GlobalScope.launch(Dispatchers.Main) {
//            val pair = TimeUtil.dateFormatToMS(pid)?.let {
//                TimeUtil.getSleepAndWakeTimesMS(
//                    SnapshotExtractor.getSleepTime(sleepDataSnapshot),
//                    SnapshotExtractor.getWakeUpTime(sleepDataSnapshot),
//                    it
//                )
//            }
//
//
//            if (pair != null) {
//                period.periodLive.value = Period(pair.first,pair.second)
//            }
//            period.deviceStates.value?.putAll(SnapshotExtractor.getPeriodDeviceStates(sleepDataSnapshot))
//            Firebase.auth.uid?.let { period.saveStepTime(it,pid) }
//        }
//    }
    private fun getSleepingDurationInNumbers(msDuration: Long): String {
//        val seconds = (msDuration / 1000).toInt() % 60
        val minutes = (msDuration / (1000 * 60) % 60)
        val hours = (msDuration / (1000 * 60 * 60) % 24)
        return "$hours:$minutes"
    }
}