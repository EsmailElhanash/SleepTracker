package com.example.sleeptracker.models

import android.util.Log
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
import java.util.*


class SleepPeriod(val period: Period, initState : String) {
    companion object{
        private const val saveEvery = 30000L
        private const val delayBWModels = 2000L
        private val createdAt = Date(Calendar.getInstance().timeInMillis)
    }


    private val TAG = "SleepPeriod ${period.getPeriodID()}"

    val pid = "${period.getPeriodID()}- UserID:${AWS.uid()}"

    private var newData = false
        set(value) {
            synchronized(this){
                field = value
            }
        }

    private var newSubPeriodData = false
        set(value) {
            synchronized(this){
                field = value
            }
        }

    private var accelerometerLastReading : Double = 0.0
    private var totalMovementCount = 0

    private val subPeriodsMovementCount = arrayListOf<Int>()
    private val pendingStates = mutableListOf<Pair<String,String>>()

    private val sortedStates : SortedMap<Long,String> = sortedMapOf()


    init {
        saveState(initState)
        AWS.get(pid,TrackerPeriod::class.java){
                totalMovementCount =
                    (it.data as? TrackerPeriod)?.totalMovements ?: 0
                accelerometerLastReading =
                    (it.data as? TrackerPeriod)?.accelerometerLastReading ?: 0.0

            Thread {
                saveLoop()
            }.start()
        }
        // add initial movement count 0 to all sub periods
        synchronized(period.periodPortions){
            repeat(period.periodPortions.size) {
                subPeriodsMovementCount.add(0)
            }
        }
    }



    private fun saveLoop() {

        while (true) {
            if (newData) {
                newData = false

                AWS.save(
                    TrackerPeriod.builder()
                        .wakeUpTime(period.getEndTime())
                        .sleepTime(period.getStartTime())
                        .createdAt(Temporal.DateTime(createdAt, 0))
                        .userId(AWS.uid())
                        .id(pid)
                        .accelerometerLastReading(accelerometerLastReading)
                        .totalMovements(totalMovementCount)
                        .build()
                ) {}
                Thread.sleep(delayBWModels)

                synchronized(pendingStates){
                    pendingStates.forEach { state ->
                        AWS.save(
                            DeviceState.Builder()
                                .time(state.first)
                                .state(state.second)
                                .trackerperiodId(pid)
                                .id(getStateID(state.first, state.second))
                                .build()
                            ) {}
                        Thread.sleep(delayBWModels)
                    }
                    pendingStates.removeAll { true }
                }
                Thread.sleep(delayBWModels)
            }
            if (newSubPeriodData) {
                newSubPeriodData = false
                run {
                    synchronized(period.periodPortions){
                        period.periodPortions.forEachIndexed { index, subPeriod ->
                            val nowMS = Calendar.getInstance().timeInMillis
                            if (nowMS in subPeriod.periodStartMS..subPeriod.periodEndMS) {
                                val subPid = "${subPeriod.getPeriodID()} - pid:$pid"
                                AWS.save(
                                    SubPeriod.builder()
                                        .range(subPeriod.getMinuteRangePeriodID())
                                        .movementCount(subPeriodsMovementCount[index] + 1)
                                        .trackerperiodId(pid)
                                        .id(subPid)
                                        .build()
                                ) {}
                                return@run
                            }
                        }
                    }
                }
                Log.d(TAG, "finished saving sub periods")
            }
            Thread.sleep(saveEvery)
        }
    }

    private fun forceSave(onCompleteCallback: ()->Unit){
        Thread {
            newData = false
            AWS.save(
                TrackerPeriod.builder()
                    .wakeUpTime(period.getEndTime())
                    .sleepTime(period.getStartTime())
                    .createdAt(Temporal.DateTime(createdAt,0))
                    .userId(AWS.uid())
                    .id(pid)
                    .accelerometerLastReading(accelerometerLastReading)
                    .actualSleepTime(actualSleepTime)
                    .actualWakeUpTime(actualWakeUpTime)
                    .averageMovementCount(avgMovementCount)
                    .disturbancesCount(disturbancesCount)
                    .sleepDuration(sleepDuration)
                    .durationInNumbers(durationInNumbers)
                    .totalMovements(totalMovementCount)
                    .build()
            ) {}
            Thread.sleep(delayBWModels)


            sessions?.let { sessions ->
                synchronized(sessions){
                    sessions.forEach {
                        AWS.save(
                            Session.builder()
                                .start(it.getStartTime())
                                .end(it.getEndTime())
                                .trackerperiodId(pid)
                                .id("${it.getPeriodID()}- ${it.getMinuteRangePeriodID()} : pid:$pid")
                                .build()
                        ) {}
                        Thread.sleep(delayBWModels)
                    }
                    sessions.removeAll { true }
                }
            }
            synchronized(pendingStates){
                pendingStates.forEach {  state ->
                    AWS.save(
                        DeviceState.Builder()
                            .time(state.first)
                            .state(state.second)
                            .trackerperiodId(pid)
                            .id(getStateID(state.second,state.first))
                            .build()
                    ) {}
                    Thread.sleep(delayBWModels)
                }
            }
            Thread.sleep(delayBWModels)

            if (newSubPeriodData){
                newSubPeriodData = false
                run {
                    synchronized(period.periodPortions){
                        period.periodPortions.forEachIndexed let@{ index, subPeriod ->
                            val nowMS = Calendar.getInstance().timeInMillis
                            if (subPeriod.periodStartMS < nowMS + 30 * MINUTE_IN_MS) {
                                return@let
                            } else if (subPeriod.periodStartMS > nowMS) {
                                return@run
                            }
                            val subPid = "${subPeriod.getPeriodID()} - pid:$pid"
                            AWS.save(
                                SubPeriod.builder()
                                    .range(subPeriod.getMinuteRangePeriodID())
                                    .movementCount(subPeriodsMovementCount[index] + 1)
                                    .trackerperiodId(pid)
                                    .id(subPid)
                                    .build()
                            ) {}
                            Thread.sleep(delayBWModels)
                        }
                    }
                }
                Log.d(TAG, "finished saving sub periods")
            }
            Thread.sleep(delayBWModels)
            onCompleteCallback()
        }.start()
    }

    private fun setNewData() {
        newData = true
    }

    private fun setNewSubPeriodData() {
        newSubPeriodData = true
    }

    fun saveAccelerometerReading(reading: Double){
        accelerometerLastReading = reading
        setNewData()
    }

    fun saveStepTime(){
        val nowMS = Calendar.getInstance().timeInMillis
        synchronized(period.periodPortions){
            period.periodPortions.forEachIndexed { index, sp ->
                if (nowMS in sp.periodStartMS..sp.periodEndMS) {
                    subPeriodsMovementCount[index]++
                    setNewSubPeriodData()
                }
            }
        }
        totalMovementCount++
        setNewData()

    }

    private fun getStateID(time:String,state: String) : String{
        return "$state--$time--$pid"
    }

    fun saveState(state: String){
        val now = TimeUtil.getFractionalExactTime()
        synchronized(pendingStates){ pendingStates.add(Pair(now, state)) }
        setNewData()
    }

    fun calculateSleepDuration(sessions : HashMap<Long,Period> ,onCompleteCallback: ()->Unit) {
        val longestPeriod = getLongestPeriod(sessions,sortedStates)
        if (longestPeriod==null) {
            onCompleteCallback()
            return
        }

        sleepDuration = getSleepingDuration(longestPeriod)
        durationInNumbers = getSleepingDurationInNumbers(longestPeriod)

        onCompleteCallback()
    }

    private fun createSortedDeviceStatesList(p: Period, onCompleteCallback: ((sortedStates: SortedMap<Long,String>) -> Unit)){
        forceSave {
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

    }

    fun calculateSessions(onCompleteCallback: (sessions : HashMap<Long,Period>) -> Unit){
        createSortedDeviceStatesList(period){
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
            disturbancesCount = sessions.size.toString()

            Log.d("calculateSessions", "sessions count: ${sessions.size} ")
            saveSessions(sessions){
                onCompleteCallback(sessions)
            }
        }
    }

    private fun saveSessions(sessions0:HashMap<Long,Period> ,function: () -> Unit) {
        sessions = mutableListOf()
        sessions0.values.forEach {
            sessions?.add(
                it
            )
        }
        function()
    }

    fun calculateAverageMovementCount(onCompleteCallback: ()-> Unit){
            forceSave {
                var averageMovementCount = 0
                val sortedPortions = sortedMapOf<Long,Period>()
                synchronized(period.periodPortions){
                    period.periodPortions.forEach {
                        sortedPortions[it.periodStartMS] = it
                    }
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
                    avgMovementCount = averageMovementCount.toString()
                        forceSave {
                            onCompleteCallback()
                        }
                }
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
            val actSleepTime = TimeUtil.formatTimeMS(timeMs!!)
            actualSleepTime = actSleepTime
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
            val wakeTime = TimeUtil.formatTimeMS(timeMs)
            actualWakeUpTime = wakeTime
        }
        return timeMs
    }

    // Data calculated at end :
    private var actualWakeUpTime: String? = null
    private var avgMovementCount: String? = null
    private var actualSleepTime: String? = null
    private var sessions: MutableList<Period>? = null
    private var sleepDuration: String? = null
    private var durationInNumbers: String? = null
    private var disturbancesCount: String? = null
}