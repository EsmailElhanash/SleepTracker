package com.example.sleeptracker.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.amplifyframework.datastore.generated.model.User
import com.example.sleeptracker.objects.DaysGroup


class UserModel : ViewModel() {
    val user: LiveData<User> = UserObject.user
    val offDays: LiveData<DaysGroup> = UserObject.offDays
    val workDays: LiveData<DaysGroup> = UserObject.workDays
}
