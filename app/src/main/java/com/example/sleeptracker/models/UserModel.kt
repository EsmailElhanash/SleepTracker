package com.example.sleeptracker.models

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.DayGroup
import com.amplifyframework.datastore.generated.model.User
import com.example.sleeptracker.R
import com.example.sleeptracker.aws.DB
import com.example.sleeptracker.database.utils.DBParameters.DAYS
import com.example.sleeptracker.objects.DaysGroup
import com.example.sleeptracker.objects.GroupType
import com.example.sleeptracker.objects.Period
import com.example.sleeptracker.objects.TimePoint
import com.example.sleeptracker.utils.time.DAY_IN_MS
import com.example.sleeptracker.utils.time.TimeUtil
import java.util.*


class UserModel : ViewModel() {
    var uid: String? = Amplify.Auth?.currentUser?.userId
//    //Sleep Data


    private val workDaysLivedata : MutableLiveData<DaysGroup> = MutableLiveData()
    val workDays : LiveData<DaysGroup> get() = workDaysLivedata
    private val offDaysLivedata : MutableLiveData<DaysGroup> = MutableLiveData()
    val offDays : LiveData<DaysGroup> get() = offDaysLivedata


    init {
        uid?.let { loadSleepTimes(it) }
    }

    fun getWorkDaysOnce(send:(workDays:DaysGroup)->Unit){
        val wd = workDays.value
        if (wd!=null) send(wd)
        else {
            uid?.let { loadSleepTimes(it){ workDays, _ ->
                send(workDays)
            } }
        }
    }

    fun getOffDaysOnce(send:(offDays:DaysGroup)->Unit){
        val od = offDays.value
        if (od!=null) send(od)
        else {
            uid?.let { loadSleepTimes(it){_, offDays ->
                send(offDays)
            } }
        }
    }

    fun updateConsent(v:String,onSuccess:()->Unit,onFailure:()->Unit) {
        val id = uid
        if (id == null) {
            onFailure()
            return
        }
        DB.get(id,User::class.java){ response ->
            if (response.success){
                val edited = (response.data as User).copyOfBuilder().consent(v).build()
//                DB.saveLocal(edited) {}
                DB.save(edited){
                    if (it.success) onSuccess() else onFailure()
                }
            }
        }
    }

    fun getSleepPeriodCallBack(callback: ((periods: List<Period>) -> Unit)) {
        val uid = Amplify.Auth?.currentUser?.userId ?: return
        loadSleepTimes(uid){ workDays :DaysGroup, offDays:DaysGroup->
            val possibleActivePeriods:ArrayList<Period> = arrayListOf()
            val nowDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) % 7
            val yesterday = Calendar.getInstance().let { cal->
                cal.time = Date(cal.timeInMillis - DAY_IN_MS)
                cal.get(Calendar.DAY_OF_WEEK) % 7
             }
            getPossibleActivePeriods(workDays,offDays,nowDayOfWeek)?.let { it1 -> possibleActivePeriods.add(it1) }
            getPossibleActivePeriods(workDays,offDays,yesterday)?.let { it1 -> possibleActivePeriods.add(it1) }
            callback(possibleActivePeriods)
        }
    }

    private fun getPossibleActivePeriods(workDaysGroup: DaysGroup , offDaysGroup: DaysGroup , day:Int):Period?{
        val dayName = DAYS[day]
        val sleepTimePoint : TimePoint
        val awakeTimePoint : TimePoint
        when (dayName) {
            in workDaysGroup.daysNames -> {
                sleepTimePoint = workDaysGroup.sleepTime
                awakeTimePoint = workDaysGroup.wakeTime
            }
            in offDaysGroup.daysNames -> {
                sleepTimePoint = offDaysGroup.sleepTime
                awakeTimePoint = offDaysGroup.wakeTime
            }
            else -> return null
        }
        val timePair = TimeUtil.getStartEndPair(
                sleepTimePoint,
                awakeTimePoint,
                Calendar.getInstance().timeInMillis
        )
        return Period(timePair.first, timePair.second)
    }


    private fun loadSleepTimes(uid: String, onCompleteCallback: ((workDays:DaysGroup, offDays:DaysGroup) -> Unit)? = null) {
        DB.get(uid,User::class.java){
            val u = it.data as User
            val offDaysGroup = createDaysGroup(u.offDay,GroupType.OFF_DAYS)
            offDaysLivedata.postValue(offDaysGroup)

            val workDaysGroup = createDaysGroup(u.workday,GroupType.WORK_DAYS)
            workDaysLivedata.postValue(workDaysGroup)

            if (onCompleteCallback != null && workDaysGroup != null && offDaysGroup != null) {
                onCompleteCallback(workDaysGroup,offDaysGroup)
            }
         }
    }


    fun updateDayGroups(groups : Pair<DaysGroup,DaysGroup>){
        val uid = Amplify.Auth?.currentUser?.userId ?: return
        val workDaysGroup:DaysGroup = groups.first
        val offDaysGroup: DaysGroup = groups.second
        workDaysLivedata.postValue(workDaysGroup)
        offDaysLivedata.postValue(offDaysGroup)
        DB.get(uid,User::class.java){
            val u = it.data as User
            val nu = u.copyOfBuilder()
            nu.workday(DayGroup.builder()
                .sleepTime(workDaysGroup.sleepTime.toString())
                .wakeUpTime(workDaysGroup.wakeTime.toString())
                .days(workDaysGroup.daysNames)
                .build())
            nu.offDay(DayGroup.builder()
                    .sleepTime(offDaysGroup.sleepTime.toString())
                    .wakeUpTime(offDaysGroup.wakeTime.toString())
                    .days(offDaysGroup.daysNames)
                    .build())
            DB.save(nu.build()){

            }
        }

    }

    private fun createDaysGroup(dbDayGroup:DayGroup,type:GroupType) : DaysGroup? {
        val st = TimePoint.stringToObject(dbDayGroup.sleepTime) ?: return null
        val wt = TimePoint.stringToObject(dbDayGroup.wakeUpTime) ?: return null
        return DaysGroup(dbDayGroup.days
            ,type
            ,st,wt)
    }
    private fun ethicMatcher(id:Int,context: Context):String?{
            return when (id) {
                R.id.african_ethnic -> context.getString(R.string.african)
                R.id.white_ethnic ->context.getString(R.string.white)
                R.id.mixed_ethnic ->context.getString(R.string.mixed)
                R.id.asian_ethnic ->context.getString(R.string.asian)
                R.id.caribbean_ethnic ->context.getString(R.string.caribbean)
                R.id.other_ethnic ->context.getString(R.string.other_ethnic)
                else -> null
            }

        }
}
