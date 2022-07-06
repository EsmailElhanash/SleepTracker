package com.example.sleeptracker.models

import android.content.Context
import android.os.PowerManager
import androidx.core.content.ContextCompat.getSystemService
import com.amplifyframework.core.model.Model
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.generated.model.DeviceState
import com.amplifyframework.datastore.generated.model.Session
import com.amplifyframework.datastore.generated.model.SubPeriod
import com.amplifyframework.datastore.generated.model.TrackerPeriod
import com.example.sleeptracker.aws.AWS
import com.example.sleeptracker.objects.Period
import com.example.sleeptracker.utils.MINUTE_IN_MS
import com.example.sleeptracker.utils.time.TimeUtil
import kotlinx.coroutines.*
import java.util.*


class SleepPeriod(var period: Period, private val initState : String) {
    val pid = "${period.getPeriodID()}- UserID:${AWS.uid}"


    private var trackerPeriod: TrackerPeriod = TrackerPeriod
        .builder()
        .wakeUpTime(period.getEndTime())
        .sleepTime(period.getStartTime())
        .createdAt(Temporal.DateTime(Date(Calendar.getInstance().timeInMillis),0))
        .userId(AWS.uid)
        .id(pid)
        .build()

    private var pendingSaveModels = mutableListOf<Model>()
    private var pendingSaveSubPeriods =  mutableMapOf<String,SubPeriod>()

    private val sortedStates : SortedMap<Long,String> = sortedMapOf()


    init {
        CoroutineScope(Dispatchers.IO).launch {
            saveLoop()
            saveLoopSubPeriod()
        }
        loadPeriod{
             saveState(initState)
        }
    }
    fun loadPeriod(onCompleteCallback: () -> Unit) {
        AWS.get(pid,TrackerPeriod::class.java){
            val p = it.data as? TrackerPeriod
            if (p!=null) trackerPeriod = p

            onCompleteCallback()
        }
    }


    private val saveEvery = 30000L
    private var newData = false
    private var newSubPeriodData = false



    private suspend fun saveLoop(){
        CoroutineScope(Dispatchers.IO).launch{
            while (true){
                if (newData){
                    AWS.save(trackerPeriod){}
                    val pendingSaveCopy = mutableListOf<Model>()
                    pendingSaveCopy.addAll(pendingSaveModels)
                    pendingSaveCopy.forEach { model ->
                        // todo test all data are saved, do miss i.e same indices in both lists orig and copy
                        AWS.save(model){}
                        delay(500)
                    }
                    pendingSaveModels = mutableListOf()
                    newData = false
                }
                delay(saveEvery)
            }
        }
    }

    fun forceSave(onCompleteCallback: ()->Unit){
        forceSaveSubPeriod {
            CoroutineScope(Dispatchers.IO).launch {
                AWS.save(trackerPeriod) {}
                val pendingSaveCopy = mutableListOf<Model>()
                pendingSaveCopy.addAll(pendingSaveModels)
                pendingSaveCopy.forEach {  model ->
                    AWS.save(model) {}
                    delay(500)
                }
                pendingSaveModels = mutableListOf()
                newData = false
                delay(3000)
                onCompleteCallback()
            }
        }
    }


    private suspend fun saveLoopSubPeriod(){
        CoroutineScope(Dispatchers.IO).launch{
            while (true){
                if (newSubPeriodData){
                    val pendingSaveCopy = mutableListOf<SubPeriod>()
                    pendingSaveCopy.addAll(pendingSaveSubPeriods.values)
                    pendingSaveCopy.forEach { subPeriod ->
                        // todo test all data are saved, do miss i.e same indices in both lists orig and copy
                        AWS.save(subPeriod){}
                        delay(500)
                    }
                    newSubPeriodData = false
                }
                delay(saveEvery)
            }
        }
    }

    private fun forceSaveSubPeriod(onCompleteCallback: ()->Unit){
        CoroutineScope(Dispatchers.IO).launch{
            if (newSubPeriodData){
                val pendingSaveCopy = mutableListOf<SubPeriod>()
                pendingSaveCopy.addAll(pendingSaveSubPeriods.values)
                pendingSaveCopy.forEachIndexed { _, subPeriod ->
                    // todo test all data are saved, do miss i.e same indices in both lists orig and copy
                    AWS.save(subPeriod){}
                    delay(500)
                }
                pendingSaveSubPeriods = mutableMapOf()
                newSubPeriodData = false
            }
            delay(3000)
            onCompleteCallback()
        }
    }

    private fun savePeriod() {
        newData = true
    }

    private fun saveSubPeriod() {
        newSubPeriodData = true
    }

    fun saveAccelerometerReading(reading: Double){
        trackerPeriod =
            trackerPeriod.copyOfBuilder().accelerometerLastReading(reading).build()

        savePeriod()
    }

    fun saveStepTime(){
        // todo test it
        val nowMS = Calendar.getInstance().timeInMillis
        period.periodPortions.forEach { sp ->
            if (nowMS in sp.periodStartMS..sp.periodEndMS) {
                val subPid = "${sp.getPeriodID()} - pid:$pid"
                var subPeriod = pendingSaveSubPeriods[sp.getPeriodID()]

                subPeriod = if (subPeriod!=null)
                    subPeriod.copyOfBuilder()
                        .movementCount(subPeriod.movementCount + 1)
                        .build()
                else SubPeriod
                    .builder()
                    .range(sp.getMinuteRangePeriodID())
                    .movementCount(1)
                    .trackerperiodId(pid)
                    .id(subPid)
                    .build()

                pendingSaveSubPeriods[sp.getPeriodID()] = subPeriod

                saveSubPeriod()
            }
        }

        val currentCount = trackerPeriod.totalMovements ?: 0
        trackerPeriod = trackerPeriod.copyOfBuilder()
            .totalMovements(currentCount+1)
            .build()
        savePeriod()

    }

    fun saveState(state: String){
        val now = TimeUtil.getFractionalExactTime()
        val ds = DeviceState
            .builder()
            .time(now)
            .state(state)
            .trackerperiodId(pid)
            .id("$state--$now--$pid")
            .build()
        pendingSaveModels.add(ds)
        savePeriod()

    }

    fun calculateSleepDuration(sessions : HashMap<Long,Period> ,onCompleteCallback: ()->Unit) {
        val longestPeriod = getLongestPeriod(sessions,sortedStates)
        if (longestPeriod==null) {
            onCompleteCallback()
            return
        }
        val durationString = getSleepingDuration(longestPeriod)
        val duration2 = getSleepingDurationInNumbers(longestPeriod)

        trackerPeriod = trackerPeriod.copyOfBuilder()
            .sleepDuration(durationString)
            .durationInNumbers(duration2)
            .build()
        onCompleteCallback()
    }

    private fun setSortedStates(p: Period, onCompleteCallback: ((sortedStates: SortedMap<Long,String>) -> Unit)){
        AWS.getPredicate(
            Where.matches(
                DeviceState.TRACKERPERIOD_ID.eq(pid)
            ).queryPredicate,
            DeviceState::class.java
        ){
            val states = it.data
            states?.forEach lit@{ state ->
                if (state !is DeviceState) return@lit
                val ms = TimeUtil.getCorrectEventTimeMS(p, state.time) ?: return@lit
                sortedStates[ms] = state.state
            }
            onCompleteCallback(sortedStates)
        }

    }

    fun calculateSessions(onCompleteCallback: (sessions : HashMap<Long,Period>) -> Unit){
        setSortedStates(period){
            var lastEvent : String? = null
            var lastEventTime = 0L
            val sessions : HashMap<Long,Period> = hashMapOf()

            for ((time , event) in it){
                if (event == "User present") {
                    if (lastEvent == "User present") {
                        //Do nothing, as we want to keep last USER_PRESENT Event time, first one
                    } else {
                        lastEvent = event
                        lastEventTime = time
                    }
                } else if (event == "off") {
                    if (lastEvent == "User present") {
                        if (time - lastEventTime > 5 * MINUTE_IN_MS) {
                            // here we have a session
                            // lastEventTime is session start in this case
                            // time is session end in this case
                            sessions[lastEventTime] = Period(lastEventTime, time)
                        }
                        lastEvent = event
                        lastEventTime = time
                    } else {
                        lastEvent = event
                        lastEventTime = time
                    }
                }
            }
            val count = sessions.size.toString()

            trackerPeriod = trackerPeriod.copyOfBuilder().disturbancesCount(count).build()
            saveSessions(sessions){
                onCompleteCallback(sessions)
            }
        }
    }

    private fun saveSessions(sessions:HashMap<Long,Period> ,function: () -> Unit) {
        //todo test
        sessions.values.forEach {
            pendingSaveModels.add(
                Session.builder()
                    .start(it.getStartTime())
                    .end(it.getEndTime())
                    .trackerperiodId(pid)
                    .id("${it.getPeriodID()} : pid:$pid")
                    .build()
            )
        }
        function()
    }

    fun calculateAverageMovementCount(onCompleteCallback: ()-> Unit){
        //todo test
        var averageMovementCount = 0
        val sortedPortions = sortedMapOf<Long,Period>()
        period.periodPortions.forEach{
            sortedPortions[it.periodStartMS] = it
        }

        AWS.getPredicate(
            Where.matches(
                SubPeriod.TRACKERPERIOD_ID.eq(pid)
            ).queryPredicate,
            SubPeriod::class.java
        ){ res ->
            val subPeriods = res.data
            val countList = arrayListOf<Int>()
            for ((_, p) in sortedPortions){
                subPeriods?.forEach let@{
                    if (it !is SubPeriod) return@let
                    if (it.range!= p.getMinuteRangePeriodID()) return@let

                    val count = it.movementCount
                    countList.add(count)
                    val lastIndex = countList.size - 1
                    if (lastIndex >= 2) {
                        val last3Count =
                            countList[lastIndex] + countList[lastIndex - 1] + countList[lastIndex - 2]
                        if (last3Count > 20) {
                            averageMovementCount++
                            countList.clear()
                        }
                    }
                }
            }
            trackerPeriod = trackerPeriod.copyOfBuilder().averageMovementCount(averageMovementCount.toString()).build()
            onCompleteCallback()
        }


    }

    private fun getSleepingDuration(msDuration: Long): String {
//        val seconds = (msDuration / 1000).toInt() % 60
        val minutes = (msDuration / (1000 * 60) % 60)
        val hours = (msDuration / (1000 * 60 * 60) % 24)
        return "$hours Hours and $minutes Minutes"
    }
    private fun getSleepingDurationInNumbers(msDuration: Long): String {
//        val seconds = (msDuration / 1000).toInt() % 60
        val minutes = (msDuration / (1000 * 60) % 60)
        val hours = (msDuration / (1000 * 60 * 60) % 24)
        return "$hours:$minutes"
    }

    private fun getLongestPeriod(sessions:HashMap<Long,Period> ,eventsSortedMap:SortedMap<Long,String>):Long?{
        val lastPresence = getLastUserPresenceEventTime(eventsSortedMap) ?: return null
        val firstOff = getFirstOffEventTime(eventsSortedMap) ?: return null
        val longest1 =  lastPresence - firstOff
        val sessionsDurations = getSessionsDurations(sessions)
        return longest1 - sessionsDurations
    }

    private fun getSessionsDurations(sessions: HashMap<Long, Period>): Long {
        var duration = 0L
        sessions.values.forEach {
            duration += it.periodEndMS - it.periodStartMS
        }
        return duration
    }

    private fun getFirstOffEventTime(eventsSortedMap:SortedMap<Long,String>) : Long? {
        var timeMs:Long? = null
        run {
            eventsSortedMap.forEach { (l, s) ->
                if (s == "off") {
                    timeMs = l
                    return@run
                }
            }
        }
        if (timeMs!=null){
            val actualSleepTime = TimeUtil.formatTimeMS(timeMs!!)
            trackerPeriod = trackerPeriod.copyOfBuilder().actualSleepTime(actualSleepTime).build()
        }
        return timeMs
    }

    private fun getLastUserPresenceEventTime(eventsSortedMap:SortedMap<Long,String>) : Long? {
        var timeMs:Long? = null

        while (eventsSortedMap.size > 0){
            val last = eventsSortedMap.lastKey()
            val s = eventsSortedMap.remove(last)
            if (s == "User present") {
                timeMs = last
                break
            }
        }
        if (timeMs!=null){
            val actualWakeUpTime = TimeUtil.formatTimeMS(timeMs)
            trackerPeriod = trackerPeriod.copyOfBuilder().actualWakeUpTime(actualWakeUpTime).build()
        }
        return timeMs
    }


}