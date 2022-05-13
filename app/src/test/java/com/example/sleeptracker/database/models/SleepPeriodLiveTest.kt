package com.example.sleeptracker.database.models

import com.example.sleeptracker.database.utils.SnapshotExtractor
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Test

class SleepPeriodLiveTest {
    val pid = "May 20, 2021"

//    @Test
//    fun testCalculateSleepDuration(){
//        val listener : ValueEventListener = object :  ValueEventListener {
//            override fun onDataChange(sleepDataSnapshot: DataSnapshot) {
//                SleepPeriodLive.deviceStates.value?.putAll(SnapshotExtractor.getPeriodDeviceStates(sleepDataSnapshot))
//                SleepPeriodLive.calculateSleepDuration()
//            }
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//        }
//        GlobalScope.launch {
//            Firebase.auth.uid?.let { SleepPeriodLive.getPeriodData(it,listener,pid) }
//        }
//    }
}