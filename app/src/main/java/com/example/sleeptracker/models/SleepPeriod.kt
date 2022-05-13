package com.example.sleeptracker.models

import com.amplifyframework.datastore.generated.model.DeviceState
import com.amplifyframework.datastore.generated.model.SubPeriod
import com.amplifyframework.datastore.generated.model.TrackerPeriod
import com.example.sleeptracker.aws.DB
import com.example.sleeptracker.objects.Period
import com.example.sleeptracker.utils.MINUTE_IN_MS
import com.example.sleeptracker.utils.time.TimeUtil
import java.util.*


class SleepPeriod(val period: Period) {
    val id = period.getPeriodID()

    private var trackerPeriod: TrackerPeriod = TrackerPeriod
        .builder()
        .userId(DB.uid)
        .wakeUpTime(period.getEndTime())
        .sleepTime(period.getStartTime())
        .build()

    private val subPeriods: HashMap<String,SubPeriod> = hashMapOf()
    private val deviceStates:ArrayList<DeviceState> = arrayListOf()
    private val sortedStates : SortedMap<Long,String> = sortedMapOf()


    init {
        loadPeriod{}
    }

    fun initialize(){
        val uid = DB.uid ?: return
        val pid = period.getPeriodID()
        DB.save(TrackerPeriod.builder()
            .userId(uid)
            .wakeUpTime(period.getEndTime())
            .sleepTime(period.getStartTime())
            .id(pid)
            .userTrackerId("$uid-$pid")
            .build()){

        }
    }
    fun loadPeriod(onCompleteCallback: (period:TrackerPeriod?) -> Unit) {
        DB.get(period.getPeriodID(),TrackerPeriod::class.java){
            val p = it.data as? TrackerPeriod ?:return@get
            trackerPeriod = p
            onCompleteCallback(p)
        }
    }

    private fun savePeriod() {
        DB.save(trackerPeriod){}
    }
    fun saveEndTime(onComplete: () -> Unit?) {
        val now = Calendar.getInstance().timeInMillis
        trackerPeriod = trackerPeriod.copyOfBuilder()
            .ended(TimeUtil.formatTimeMS(now))
            .build()
        savePeriod()
        onComplete()
    }
    fun saveAccelerometerReading(reading: Double){
        trackerPeriod =
            trackerPeriod.copyOfBuilder().accelerometerLastReading(reading).build()
        savePeriod()
    }

    fun saveStepTime(){
        val nowMS = Calendar.getInstance().timeInMillis
        period.periodPortions.forEach { sp ->
            if (nowMS in sp.periodStartMS..sp.periodEndMS) {
                val id = sp.getMinuteRangePeriodID()
                val currentValue = subPeriods[id]
                val newSubPeriod = if (currentValue==null)
                    SubPeriod.builder().movementCount(1)
                        .range(id)
                        .trackerPeriodSubPeriodsId(id+"-"+period.getPeriodID())
                        .id(id+ " " + period.getPeriodID())
                        .build()
                else currentValue.copyOfBuilder()
                    .movementCount(currentValue.movementCount+1).build()

                subPeriods[id] = newSubPeriod
                DB.save(newSubPeriod){}
            }
        }


    }

    fun saveState(state: String){
        val now = TimeUtil.getFractionalExactTime()
        val id = state + "-" + now + period.getPeriodID()
        val ds = DeviceState
            .builder()
            .time(now)
            .state(state)
            .trackerPeriodDeviceStatesId(id)
            .build()
        DB.save(ds){}
        deviceStates.add(ds)
    }


    fun calculateSleepDuration(onCompleteCallback: ((duration:Long) -> Unit)? = null) {
        val p = period
        val startCalculations = { sortedStates : SortedMap<Long,String> ->
            val longestPeriod = getLongestPeriod(sortedStates)
            val durationString = getSleepingDuration(longestPeriod)
            val duration2 = getSleepingDurationInNumbers(longestPeriod)
            if (onCompleteCallback != null) {
                onCompleteCallback(longestPeriod)
            }
            trackerPeriod = trackerPeriod.copyOfBuilder()
                .sleepDuration(durationString)
                .durationInNumbers(duration2)
                .build()
                savePeriod()

        }
        //we calculate Sleep Duration using device states
        setSortedStates(p){
            val finalMap : SortedMap<Long,String> = sortedMapOf()
            for (i in it){
                if (i.key<p.periodStartMS) continue
                if (i.value == "on") continue
                if (i.key>p.periodEndMS && i.value == "User present"){
                    finalMap[i.key] = i.value
                    break
                }
                finalMap[i.key] = i.value
            }
            startCalculations(finalMap)
        }
    }

    private fun setSortedStates(p: Period,onCompleteCallback: ((sortedStates: SortedMap<Long,String>) -> Unit)){
        if (deviceStates.size == 0){
            loadPeriod{ period ->
                period?.deviceStates?.let {
                    deviceStates.addAll(it)
                        it.forEach lit@{state->
                        val ms = TimeUtil.getCorrectEventTimeMS(p, state.time) ?: return@lit
                        sortedStates[ms] = state.state
                    }
                }
                onCompleteCallback(sortedStates)
            }
        }else{
            deviceStates.forEach lit@{ state->
                val ms = TimeUtil.getCorrectEventTimeMS(p, state.time) ?: return@lit
                sortedStates[ms] = state.state
            }
            onCompleteCallback(sortedStates)
        }

    }

    fun calculateSessions(onCompleteCallback: ((sessions:HashMap<Long,Period>) -> Unit)? = null){
        var lastEvent : String? = null
        var lastEventTime = 0L
        val sessions : HashMap<Long,Period> = hashMapOf()


        val calculate = { sortedStates :SortedMap<Long,String>->
            for ((time , event) in sortedStates){
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
//            saveSessions(sessions,uid)
            if (onCompleteCallback != null) {
                onCompleteCallback(sessions)
            }
        }
        calculate(sortedStates)
    }

    fun calculateAverageMovementCount(onCompleteCallback: ((totalMovementCount:Int) -> Unit)? = null){
        var averageMovementCount = 0
        val sortedPortions = sortedMapOf<Long,Period>()
        period.periodPortions.forEach{
            sortedPortions[it.periodStartMS] = it
        }
        val countList = arrayListOf<Int>()
        for ((_, p) in sortedPortions){
            val count = subPeriods[p.getMinuteRangePeriodID()]?.movementCount ?: continue
            countList.add(count)
            val lastIndex = countList.size-1
            if (lastIndex>=2){
                val last3Count = countList[lastIndex] +countList[lastIndex-1] +countList[lastIndex-2]
                if (last3Count>20) {
                    averageMovementCount++
                    countList.clear()
                }
            }
        }
        if (onCompleteCallback != null) {
            onCompleteCallback(averageMovementCount)
        }
        val newPeriod = trackerPeriod.copyOfBuilder().averageMovementCount(averageMovementCount.toString()).build()
        trackerPeriod = newPeriod
        DB.save(newPeriod){}
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

    private fun getLongestPeriod(eventsSortedMap:SortedMap<Long,String>):Long{
        var longestPeriod: Long = 0
        var lastAsleep = period.periodStartMS
        var lastUserPresent = period.periodStartMS
        val periodEndMS = period.periodEndMS
        var isPresent = false
        eventsSortedMap.forEach lit@{
            if (it.value == "on") return@lit
            if (it.value == "User present") {
                if (it.key > periodEndMS) {
                    val thisPeriodLength = it.key - lastAsleep
                    if (thisPeriodLength > longestPeriod) {
                        longestPeriod = thisPeriodLength
                    }
                    return@lit
                } else {
                    lastUserPresent = it.key
                    isPresent = true
                }
            } else if (it.value == "off") {
                if (isPresent) {
                    if (it.key - lastUserPresent > 5 * MINUTE_IN_MS) {
                        val newPeriod = lastUserPresent - lastAsleep
                        if (newPeriod > longestPeriod) {
                            longestPeriod = newPeriod
                        }
                        lastAsleep = it.key
                    }
                    isPresent = false
                }
            }
        }

        return longestPeriod
    }





}