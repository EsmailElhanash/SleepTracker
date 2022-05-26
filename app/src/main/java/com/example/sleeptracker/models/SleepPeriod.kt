package com.example.sleeptracker.models

import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.generated.model.DeviceState
import com.amplifyframework.datastore.generated.model.SubPeriod
import com.amplifyframework.datastore.generated.model.TrackerPeriod
import com.example.sleeptracker.aws.DB
import com.example.sleeptracker.objects.Period
import com.example.sleeptracker.utils.MINUTE_IN_MS
import com.example.sleeptracker.utils.time.TimeUtil
import java.util.*


class SleepPeriod(var period: Period) {
    val id = period.getPeriodID()
    val pid = "$id-${DB.uid}"

    private var trackerPeriod: TrackerPeriod = TrackerPeriod
        .builder()
        .userId(DB.uid)
        .wakeUpTime(period.getEndTime())
        .sleepTime(period.getStartTime())
        .createdAt(Temporal.DateTime(Date(Calendar.getInstance().timeInMillis),0))
        .id(pid)
        .subPeriods(mutableListOf())
        .deviceStates(mutableListOf())
        .userTrackerId(pid)
        .build()


    private val sortedStates : SortedMap<Long,String> = sortedMapOf()


    init {
        loadPeriod{
            savePeriod()
        }
    }
    fun loadPeriod(onCompleteCallback: () -> Unit) {
        DB.get(pid,TrackerPeriod::class.java){
            val p = it.data as? TrackerPeriod
            if (p!=null) {
                trackerPeriod = p
                trackerPeriod = trackerPeriod.copyOfBuilder()
                    .deviceStates(mutableListOf())
                    .subPeriods(mutableListOf())
                    .build()
                trackerPeriod.deviceStates.addAll(p.deviceStates)
                trackerPeriod.subPeriods.addAll(p.subPeriods)
            }
            onCompleteCallback()
        }
    }

//    fun updatePeriodTimes(sleepTime: String, wakeTime: String){
//        trackerPeriod = with(trackerPeriod.copyOfBuilder()) {
//            sleepTime(sleepTime)
//            wakeUpTime(wakeTime)
//            build()
//        }
//        UserModel().getSleepPeriodCallBack { periods ->
//            periods.forEach {
//                if (Calendar.getInstance().timeInMillis in it.periodStartMS .. it.periodEndMS){
//                    val gt = period.periodGroupType
//                    period = it
//                    period.periodGroupType = gt
//                    return@forEach
//                }
//            }
//        }
//        savePeriod()
//    }

    @Synchronized
    fun savePeriod() {
        DB.save(trackerPeriod){

        }
    }

    fun saveEndTime(onComplete: () -> Unit?) {
        val now = Calendar.getInstance().timeInMillis
        trackerPeriod = trackerPeriod.copyOfBuilder()
            .ended(TimeUtil.formatTimeMS(now))
            .build()
        onComplete()
    }

    fun saveAccelerometerReading(reading: Double){
        trackerPeriod =
            trackerPeriod.copyOfBuilder().accelerometerLastReading(reading).build()
        savePeriod()
    }

    private fun getSubPeriodWithTime(time:String): SubPeriod? {
        return trackerPeriod.subPeriods.stream().filter {
            time == it.range
        }.findFirst().orElse(null)

    }

    fun saveStepTime(){
        val nowMS = Calendar.getInstance().timeInMillis
        period.periodPortions.forEach { sp ->
            if (nowMS in sp.periodStartMS..sp.periodEndMS) {
                val id = sp.getMinuteRangePeriodID()
                val currentValue = getSubPeriodWithTime(id)
                if (currentValue==null) {
                    val newSubPeriod = SubPeriod.builder()
                        .range(id)
                        .movementCount(1)
                        .build()
                    trackerPeriod.subPeriods.add(newSubPeriod)

                }
                else {
                    val newValue = currentValue.copyOfBuilder()
                        .movementCount(currentValue.movementCount+1)
                        .build()
                    trackerPeriod.subPeriods.remove(currentValue)
                    trackerPeriod.subPeriods.add(newValue)
                }
                savePeriod()
            }
        }


    }

    fun saveState(state: String){
        val now = TimeUtil.getFractionalExactTime()
        val ds = DeviceState
            .builder()
            .time(now)
            .state(state)
            .build()
        trackerPeriod.deviceStates.add(ds)
        savePeriod()
    }

    fun calculateSleepDuration(onCompleteCallback: ()->Unit) {
        val p = period
        val startCalculations = { sortedStates : SortedMap<Long,String> ->
            val longestPeriod = getLongestPeriod(sortedStates)
            val durationString = getSleepingDuration(longestPeriod)
            val duration2 = getSleepingDurationInNumbers(longestPeriod)

            trackerPeriod = trackerPeriod.copyOfBuilder()
                .sleepDuration(durationString)
                .durationInNumbers(duration2)
                .build()
            onCompleteCallback()
        }
        //we calculate Sleep Duration using device states
        setSortedStates(p){
            val finalMap : SortedMap<Long,String> = sortedMapOf()
//            finalMap[p.periodStartMS] = "off"
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
        if (trackerPeriod.deviceStates.size == 0){
            loadPeriod{
                trackerPeriod.deviceStates?.let {
                        it.forEach lit@{state->
                        val ms = TimeUtil.getCorrectEventTimeMS(p, state.time) ?: return@lit
                        sortedStates[ms] = state.state
                    }
                }
                onCompleteCallback(sortedStates)
            }
        }else{
            trackerPeriod.deviceStates?.forEach lit@{ state->
                val ms = TimeUtil.getCorrectEventTimeMS(p, state.time) ?: return@lit
                sortedStates[ms] = state.state
            }
            onCompleteCallback(sortedStates)
        }

    }

    fun calculateSessions(onCompleteCallback: () -> Unit){
        var lastEvent : String? = null
        var lastEventTime = 0L
        val sessions : HashMap<Long,Period> = hashMapOf()

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
        val count = sessions.size.toString()
        val strings = mutableListOf<String>()
        sessions.values.forEach {
            strings.add(it.toString())
        }
        trackerPeriod = trackerPeriod.copyOfBuilder().disturbancesCount(count).sessions(strings).build()
        onCompleteCallback()
    }

    fun calculateAverageMovementCount(onCompleteCallback: ()-> Unit){
        var averageMovementCount = 0
        val sortedPortions = sortedMapOf<Long,Period>()
        period.periodPortions.forEach{
            sortedPortions[it.periodStartMS] = it
        }
        val countList = arrayListOf<Int>()
        for ((_, p) in sortedPortions){
            val count = getSubPeriodWithTime(p.getMinuteRangePeriodID())?.movementCount ?: continue
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
        trackerPeriod = trackerPeriod.copyOfBuilder().averageMovementCount(averageMovementCount.toString()).build()
        onCompleteCallback()
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