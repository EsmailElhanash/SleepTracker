package com.example.sleeptracker.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.ObserveQueryOptions
import com.amplifyframework.core.model.query.QuerySortBy
import com.amplifyframework.core.model.query.QuerySortOrder
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.core.model.query.predicate.QueryPredicate
import com.amplifyframework.datastore.generated.model.DayGroup
import com.amplifyframework.datastore.generated.model.User
import com.example.sleeptracker.aws.AWS
import com.example.sleeptracker.background.androidservices.TrackerService


class UserModel : ViewModel() {
    private val _user : MutableLiveData<User> = MutableLiveData()
    val user : LiveData<User> = _user

    private val _workDays : MutableLiveData<DayGroup> = MutableLiveData()
    val workDays : LiveData<DayGroup> = _workDays
    private val _offDays : MutableLiveData<DayGroup> = MutableLiveData()
    val offDays : LiveData<DayGroup> = _offDays



    init {
        loadUser()
        observeUser()
    }

    private fun loadUser(){
        AWS.uid {
            val id = it ?: return@uid
            Amplify.DataStore.query(
                User::class.java, Where.identifier(User::class.java,id),
                {
                    if (it.hasNext()){
                        val u = it.next()
                        if (_user.value == null) {
                            _offDays.postValue(u.offDay)
                            _workDays.postValue(u.workday)
                        }
                        _user.postValue(u)
                    }
                },
                {}
            )
        }

    }

    private fun observeUser() {
        AWS.uid {
            val id = it ?: return@uid
            val predicate: QueryPredicate =
                User.ID.eq(id)
            val querySortBy = QuerySortBy("User", "id", QuerySortOrder.ASCENDING)
            val options = ObserveQueryOptions(predicate, null)
            Amplify.DataStore.observe(
                User::class.java, id,
                {}, let@{
                    val item = it.item()
                    if (_user.value == null) {
                        _offDays.postValue(item.offDay)
                        _workDays.postValue(item.workday)
                    }
                    _user.postValue(item)
                }, {}, {})
        }
    }


    fun updateDayGroups(groups : Pair<DayGroup, DayGroup>, onComplete: () -> Unit){
        val workDaysGroup: DayGroup = groups.first
        val offDaysGroup: DayGroup = groups.second
        _workDays.postValue(workDaysGroup)
        _offDays.postValue(offDaysGroup)
        getNonNullUserValue{
            val editedUser = it.copyOfBuilder()
            editedUser.workday(workDaysGroup)
            editedUser.offDay(offDaysGroup)
            AWS.save(editedUser.build()){ res ->
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
}
