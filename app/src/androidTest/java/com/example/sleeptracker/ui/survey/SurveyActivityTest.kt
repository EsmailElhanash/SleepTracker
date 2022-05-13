package com.example.sleeptracker.ui.survey

import android.app.Application
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Test

class SurveyActivityTest{
    var app: Application = ApplicationProvider.
        getApplicationContext() as Application

    @Before
    fun startSurveyActivity(){
        app.startActivity(Intent(
            app.applicationContext,
            SurveyActivity::class.java
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

    }
    @Test
    fun start(){

    }
}