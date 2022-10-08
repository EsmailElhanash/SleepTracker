package com.example.sleeptracker.models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.DayGroup
import com.amplifyframework.datastore.generated.model.User
import com.example.sleeptracker.aws.AWS
import com.example.sleeptracker.background.androidservices.TrackerService
import com.example.sleeptracker.database.utils.DBParameters.DAYS
import com.example.sleeptracker.objects.DaysGroup
import com.example.sleeptracker.objects.GroupType
import com.example.sleeptracker.objects.Period
import com.example.sleeptracker.objects.TimePoint
import com.example.sleeptracker.utils.time.DAY_IN_MS
import com.example.sleeptracker.utils.time.TimeUtil
import java.util.*


class UserModel : ViewModel() {

//    //Sleep Data
    val workDays : MutableLiveData<DaysGroup> = MutableLiveData()
    val offDays : MutableLiveData<DaysGroup> = MutableLiveData()

    private var surveyLastUpdated: Long? = null
    private var surveyLastUpdated2: Long? = null
    private var surveyRetakePeriod: Int? = null
    companion object {
        private const val TAG = "UserModel"
    }
    init {
        AWS.uid()?.let {
            loadUser(it){}
        }
    }

    fun init (){
        AWS.uid()?.let {
            loadUser(it){}
        }
    }

    fun getSurveyRetakePeriod(onComplete: (p:Int) -> Unit) {
        val retakePeriod = surveyRetakePeriod
        if (retakePeriod!=null) onComplete(retakePeriod)
        else {
            AWS.uid()?.let {
                loadUser(it){
                    val retakePeriod2 = surveyRetakePeriod
                    if (retakePeriod2!=null) onComplete(retakePeriod2)
                }
            }
        }
    }

    fun getSurveyLastUpdatedCaseOne(onComplete: (date:Long) -> Unit) {
        val last = surveyLastUpdated
        if (last!=null) onComplete(last)
        else {
            AWS.uid()?.let {
                loadUser(it){
                    val last2 = surveyLastUpdated
                    if (last2!=null) onComplete(last2)
                }
            }
        }
    }

    fun getSurveyLastUpdatedCaseTwo(onComplete: (date:Long) -> Unit) {
        val last = surveyLastUpdated2
        if (last!=null) onComplete(last)
        else {
            AWS.uid()?.let {
                loadUser(it){
                    val last2 = surveyLastUpdated2
                    if (last2!=null) onComplete(last2)
                }
            }
        }
    }

    fun getWorkDaysOnce(send:(workDays:DaysGroup)->Unit){
        val wd = workDays.value
        if (wd!=null) send(wd)
        else {
            AWS.uid()?.let {
                loadUser(it){
                    workDays.value?.let{wd->send(wd)}
                }
            }
        }
    }

    fun getOffDaysOnce(send:(offDays:DaysGroup)->Unit){
        val od = offDays.value
        if (od!=null) send(od)
        else {
            AWS.uid()?.let {
                loadUser(it){
                    offDays.value?.let{od->send(od)}
                }
            }
        }
    }

    fun updateConsent(v:String,onSuccess:()->Unit,onFailure:()->Unit) {
        val id = AWS.uid()
        if (id == null) {
            onFailure()
            return
        }
        AWS.get(id,User::class.java){ response ->
            if (response.success){
                val edited = (response.data as User).copyOfBuilder().consent(v).build()

                AWS.save(edited){
                    if (it.success) onSuccess() else onFailure()
                }
            }
        }
    }

    fun getSleepPeriodCallBack(re:Int? = 0,callback: ((periods: List<Period>) -> Unit)) {
        Log.d(TAG, "getSleepPeriodCallBack: ")
        val uid = AWS.uid() ?: return
        loadUser(uid) {
            val wd = workDays.value
            val od = offDays.value
            if (wd == null||od == null )
            {
                if (re != null && re<10) {
                    Thread.sleep(1000)
                    getSleepPeriodCallBack(re+1, callback)
                }
                return@loadUser
            }
            val possibleActivePeriods:ArrayList<Period> = arrayListOf()
            val nowDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) % 7
            val yesterday = Calendar.getInstance().let { cal->
                cal.time = Date(cal.timeInMillis - DAY_IN_MS)
                cal.get(Calendar.DAY_OF_WEEK) % 7
            }
            getPossibleActivePeriods(wd,od,nowDayOfWeek)?.let { it1 -> possibleActivePeriods.add(it1) }
            getPossibleActivePeriods(wd,od,yesterday)?.let { it1 -> possibleActivePeriods.add(it1) }
            Log.d(TAG, "getSleepPeriodCallBack: $possibleActivePeriods")
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



    private fun loadUser(uid: String, re:Int? = 0, onComplete: ()-> Unit) {
        AWS.get(uid,User::class.java){
            val u = it.data as? User
            if (u==null){
                if (re != null && re<10) {
                    Thread.sleep(3000)
                    loadUser(uid,re+1, onComplete)
                }
                return@get
            }
            val offDaysGroup = createDaysGroup(u.offDay,GroupType.OFF_DAYS)
            offDays.postValue(offDaysGroup)

            val workDaysGroup = createDaysGroup(u.workday,GroupType.WORK_DAYS)
            workDays.postValue(workDaysGroup)

            surveyLastUpdated = u.surveyLastUpdate?.toDate()?.time ?: 0
            surveyLastUpdated2 = u.surveyLastUpdate2?.time?.toDate()?.time ?: 0

            surveyRetakePeriod = u.retakeSurveyPeriod

            onComplete()
         }
    }


    fun updateDayGroups(groups : Pair<DaysGroup,DaysGroup>, onComplete: () -> Unit){
        val uid = Amplify.Auth?.currentUser?.userId ?: return
        val workDaysGroup:DaysGroup = groups.first
        val offDaysGroup: DaysGroup = groups.second
        workDays.postValue(workDaysGroup)
        offDays.postValue(offDaysGroup)
        AWS.get(uid,User::class.java){
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
            AWS.save(nu.build()){ res ->
                val us = res.data as? User ?: return@save
                val activePeriod = TrackerService.getActivePeriod()
                if (activePeriod==null) {
                    onComplete()
                    return@save
                }
                onComplete()
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
}
