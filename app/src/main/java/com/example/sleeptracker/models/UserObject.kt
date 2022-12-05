package com.example.sleeptracker.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.DayGroup
import com.amplifyframework.datastore.generated.model.User
import com.example.sleeptracker.aws.AWS
import com.example.sleeptracker.background.androidservices.TrackerService
import com.example.sleeptracker.database.utils.DBParameters
import com.example.sleeptracker.objects.DaysGroup
import com.example.sleeptracker.objects.GroupType
import com.example.sleeptracker.objects.Period
import com.example.sleeptracker.objects.TimePoint
import com.example.sleeptracker.utils.getLiveDataValueOnce
import com.example.sleeptracker.utils.time.DAY_IN_MS
import com.example.sleeptracker.utils.time.TimeUtil
import java.util.*

object UserObject {
    private val USER : MutableLiveData<User> = MutableLiveData()
    val user : LiveData<User> = USER

    private val WorkDays : MutableLiveData<DaysGroup> = MutableLiveData()
    val workDays : LiveData<DaysGroup> = WorkDays
    private val OffDays : MutableLiveData<DaysGroup> = MutableLiveData()
    val offDays : LiveData<DaysGroup> = OffDays

    init {
        loadUser()
        observeUser()
    }

    fun getSurveyRetakePeriod(onComplete: (p:Int) -> Unit) {
        user.getLiveDataValueOnce {
            onComplete(it.retakeSurveyPeriod)
        }
    }

    fun getSurveyLastUpdatedCaseOne(onComplete: (date:Long) -> Unit) {
        user.getLiveDataValueOnce {
            onComplete(it.surveyLastUpdate?.toDate()?.time ?: 0)
        }
    }

    fun getSurveyLastUpdatedCaseTwo(onComplete: (date:Long) -> Unit) {
        user.getLiveDataValueOnce {
            onComplete(it.surveyLastUpdate2?.time?.toDate()?.time ?: 0)
        }
    }

    fun getWorkDaysOnce(send:(workDays: DaysGroup)->Unit){
        user.getLiveDataValueOnce {
            createDaysGroup(it.workday, GroupType.WORK_DAYS)?.let { it1 ->
                send(
                    it1
                )
            }
        }
    }

    fun getOffDaysOnce(send:(offDays: DaysGroup)->Unit){
        user.getLiveDataValueOnce {
            createDaysGroup(it.offDay, GroupType.OFF_DAYS)?.let { it1 ->
                send(
                    it1
                )
            }
        }
    }

    fun updateConsent(v:String,onSuccess:()->Unit,onFailure:()->Unit) {
        user.getLiveDataValueOnce { u ->
            val edited = u.copyOfBuilder().consent(v).build()

            AWS.save(edited){
                if (it.success) onSuccess() else onFailure()
            }
        }
    }

    fun getSleepPeriodCallBack(callback: (periods: List<Period>) -> Unit) {
        user.getLiveDataValueOnce {
            val wd = createDaysGroup(it.workday, GroupType.WORK_DAYS)
            val od = createDaysGroup(it.offDay, GroupType.OFF_DAYS)

            val possibleActivePeriods: ArrayList<Period> = arrayListOf()
            val nowDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) % 7
            val yesterday = Calendar.getInstance().let { cal->
                cal.time = Date(cal.timeInMillis - DAY_IN_MS)
                cal.get(Calendar.DAY_OF_WEEK) % 7
            }
            if (wd != null && od != null) {
                getPossibleActivePeriods(wd,od,nowDayOfWeek)?.let { it1 -> possibleActivePeriods.add(it1) }
                getPossibleActivePeriods(wd,od,yesterday)?.let { it1 -> possibleActivePeriods.add(it1) }
            }
            callback(possibleActivePeriods)
        }


    }

    private fun getPossibleActivePeriods(workDaysGroup: DaysGroup, offDaysGroup: DaysGroup, day:Int): Period?{
        val dayName = DBParameters.DAYS[day]
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

    private fun loadUser(){
        val id = AWS.uid() ?: return
        Amplify.DataStore.query(
            User::class.java, Where.id(id),
            {
                if (it.hasNext()){
                    val u = it.next()
                    if (USER.value == null) {
                        createDaysGroup(u.offDay, GroupType.OFF_DAYS).apply {
                            OffDays.postValue(this)
                        }
                        createDaysGroup(u.workday, GroupType.WORK_DAYS).apply {
                            WorkDays.postValue(this)
                        }
                    }
                    USER.postValue(u)
                }
            },
            {}
        )
    }

    private fun observeUser() {
        val id = AWS.uid() ?: return
        Amplify.DataStore.observe(
            User::class.java, id,
            {}, {
                if (USER.value == null) {
                    createDaysGroup(it.item().offDay, GroupType.OFF_DAYS).apply {
                        OffDays.postValue(this)
                    }
                    createDaysGroup(it.item().workday, GroupType.WORK_DAYS).apply {
                        WorkDays.postValue(this)
                    }
                }
                USER.postValue(it.item())
            },{},{})
    }


    fun updateDayGroups(groups : Pair<DaysGroup, DaysGroup>, onComplete: () -> Unit){
        Amplify.Auth?.currentUser?.userId ?: return
        val workDaysGroup: DaysGroup = groups.first
        val offDaysGroup: DaysGroup = groups.second
        WorkDays.postValue(workDaysGroup)
        OffDays.postValue(offDaysGroup)
        user.getLiveDataValueOnce{
            val u = it
            val nu = u.copyOfBuilder()
            nu.workday(
                DayGroup.builder()
                .sleepTime(workDaysGroup.sleepTime.toString())
                .wakeUpTime(workDaysGroup.wakeTime.toString())
                .days(workDaysGroup.daysNames)
                .build())
            nu.offDay(
                DayGroup.builder()
                .sleepTime(offDaysGroup.sleepTime.toString())
                .wakeUpTime(offDaysGroup.wakeTime.toString())
                .days(offDaysGroup.daysNames)
                .build())
            AWS.save(nu.build()){ res ->
                if (res.data !is User) return@save
                val activePeriod = TrackerService.getActivePeriod()
                if (activePeriod==null) {
                    onComplete()
                    return@save
                }
                onComplete()
            }
        }

    }

    private fun createDaysGroup(dbDayGroup: DayGroup, type: GroupType) : DaysGroup? {
        val st = TimePoint.stringToObject(dbDayGroup.sleepTime) ?: return null
        val wt = TimePoint.stringToObject(dbDayGroup.wakeUpTime) ?: return null
        return DaysGroup(dbDayGroup.days
            ,type
            ,st,wt)
    }

    fun updateUser(user: User?) {
        if (user != null) {
            AWS.save(user){}
        }
    }
}