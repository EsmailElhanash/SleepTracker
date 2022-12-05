package com.example.sleeptracker.ui

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.Amplify.DataStore
import com.example.sleeptracker.App
import com.example.sleeptracker.R
import com.example.sleeptracker.background.androidservices.AlarmService
import com.example.sleeptracker.background.androidservices.SurveyService
import com.example.sleeptracker.database.utils.DBParameters
import com.example.sleeptracker.databinding.ActivityHomeBinding
import com.example.sleeptracker.models.UserModel
import com.example.sleeptracker.models.UserObject
import com.example.sleeptracker.ui.statistics.DAILY_STATISTICS
import com.example.sleeptracker.ui.statistics.STATISTICS_TYPE
import com.example.sleeptracker.ui.statistics.StatisticsActivity
import com.example.sleeptracker.ui.survey.SurveyActivity
import com.example.sleeptracker.utils.time.DAY_IN_MS
import java.util.*


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var root : View
    val user : UserModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        root = binding.root
        setContentView(root)
        setSupportActionBar(binding.toolBarHomeActivity)



        Intent(applicationContext, AlarmService::class.java).also {
            ContextCompat.startForegroundService(applicationContext,it)
        }
        Intent(applicationContext, SurveyService::class.java).also {
            ContextCompat.startForegroundService(applicationContext,it)
        }



        binding.dailyStatisticsNavButton.setOnClickListener {
            startActivity(Intent(applicationContext, StatisticsActivity::class.java)
                    .putExtra(STATISTICS_TYPE, DAILY_STATISTICS))
        }

        try {
            checkDisabledBatteryOptimizationPermission{
                checkExactAlarmPermission()
            }
        }catch (_:IllegalStateException){

        }
    }

    private fun checkExactAlarmPermission() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val canScheduleExactAlarms = alarmManager.canScheduleExactAlarms()
            if (!canScheduleExactAlarms){
                requestScheduleExactAlarms()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestScheduleExactAlarms(){
        val intent = Intent()
        intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        user.offDays.observe(this){
            it ?: return@observe
            binding.sleepWakeTimesView.offDaysNames.apply {
                this.visibility = View.VISIBLE
                this.text = it.daysNames.toString()
            }
            val st = DBParameters.SLEEP_TIME + ": " +it.sleepTime.toString()
            binding.sleepWakeTimesView.offDaySleepTimeText.text = st
            val wt = DBParameters.WAKEUP_TIME + ": " +it.wakeTime.toString()
            binding.sleepWakeTimesView.offDayWakeTimeText.text = wt
        }
        user.workDays.observe(this){
            it ?: return@observe
            binding.sleepWakeTimesView.workDaysNames.apply {
                this.visibility = View.VISIBLE
                this.text = it.daysNames.toString()
            }
            val st = DBParameters.SLEEP_TIME + ": " +it.sleepTime.toString()
            binding.sleepWakeTimesView.workdaySleepTimeText.text = st
            val wt = DBParameters.WAKEUP_TIME + ": " + it.wakeTime.toString()
            binding.sleepWakeTimesView.workDayWakeTimeText.text = wt
        }
        checkLastSurvey(user)
    }


    private fun checkDisabledBatteryOptimizationPermission(onComplete:()->Unit){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isIgnoringBatteryOptimizations()) {
                showBatteryOptimizationDisableDialog{
                    onComplete()
                }
            }else onComplete()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showBatteryOptimizationDisableDialog(onComplete:()->Unit) {
        val myDialog = AlertDialog.Builder(this)
            .setMessage(R.string.BatteryOptimizationDisableRequest)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                requestBatteryOptimizationDisable()
            }
            .setOnDismissListener {
                onComplete()
            }
            .setCancelable(false)
            .create()
        myDialog.show()
    }

    @SuppressLint("BatteryLife")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestBatteryOptimizationDisable(){
        val intent = Intent()
        intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }

    private fun isIgnoringBatteryOptimizations(): Boolean {
        val powerManager = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        val name = applicationContext.packageName
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return powerManager.isIgnoringBatteryOptimizations(name)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings_button)
            startActivity(Intent(applicationContext, SettingsActivity::class.java))
        return super.onOptionsItemSelected(item)
    }

    private fun checkLastSurvey(user:UserModel){
        val nowMS = Calendar.getInstance().timeInMillis
        UserObject.getSurveyLastUpdatedCaseOne{
            UserObject.getSurveyRetakePeriod { retakePeriod ->
                try {
                    if (nowMS >= (it + retakePeriod * DAY_IN_MS)) { //DONE... todo CRITICAL!!! ATTENTION CHANGE TO >=
                        val i = Intent(this, SurveyActivity::class.java)
                        i.putExtra(SurveyActivity.SURVEY_CASE_EXTRA, SurveyActivity.SURVEY_CASE_1)
                        startActivity(i)
                        finish()
                    }
                } catch (e: Exception) {
                    Log.d("TAG", "onDataChange: ")
                }
            }
        }
    }
}