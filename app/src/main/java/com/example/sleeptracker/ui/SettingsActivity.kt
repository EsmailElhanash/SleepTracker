package com.example.sleeptracker.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.DayGroup
import com.example.sleeptracker.R
import com.example.sleeptracker.background.androidservices.AlarmService
import com.example.sleeptracker.utils.DBParameters
import com.example.sleeptracker.databinding.ActivitySettingsBinding
import com.example.sleeptracker.models.UserModel
import com.example.sleeptracker.models.getNonNullUserValue
import com.example.sleeptracker.objects.TimePoint
import com.example.sleeptracker.ui.signin.LoginActivity
import com.example.sleeptracker.utils.androidutils.InputValidator
import com.example.sleeptracker.utils.time.PickedTimeFor
import com.example.sleeptracker.utils.time.TimePickerListener
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputLayout

class SettingsActivity : AppCompatActivity() , TimePickerListener {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var root : View
    private var myDialog: AlertDialog? = null

    private var workDaySleepTime: TimePoint? = null
    private var workDayWakeTime: TimePoint? = null
    private var offDaySleepTime: TimePoint? = null
    private var offDayWakeTime: TimePoint? = null
    private val userModel : UserModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        root = binding.root
        setContentView(root)
        setSupportActionBar(binding.toolBarSettingsActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        binding.changePasswordButton.setOnClickListener {
            changePassword(this)
        }

        binding.editWorkDays.setOnClickListener {
            showWorkDaysEditorDialog(this)
         }

        binding.logoutButton.setOnClickListener {
            confirmLogout(this)
        }




        binding.updateTimeButton.setOnClickListener {
            binding.updateTimeButton.isEnabled = false
            getWorkAndOffDaysGroups{
                userModel.updateDayGroups(it){
                    ContextCompat.startForegroundService(applicationContext,Intent(applicationContext,AlarmService::class.java))
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        observeDayGroups()
    }
    private fun showWorkDaysEditorDialog(context: Context){
        val view = LayoutInflater.from(context).inflate(R.layout.week_days_checkboxes, null, false)

        val daysCheckboxes = listOf(
            view.findViewById(R.id.day0Checkbox),
            view.findViewById(R.id.day1Checkbox),
            view.findViewById(R.id.day2Checkbox),
            view.findViewById(R.id.day3Checkbox),
            view.findViewById(R.id.day4Checkbox),
            view.findViewById(R.id.day5Checkbox),
            view.findViewById<MaterialCheckBox>(R.id.day6Checkbox),
        )
        daysCheckboxes.forEach { checkBox->
            userModel.workDays.observe(this){
                if(checkBox.text in it.days){
                    checkBox.isChecked = true
                }
            }
        }


        val myDialog = AlertDialog.Builder(context)
            .setTitle(R.string.choose_your_workdays)
            .setView(view)
            .setOnDismissListener {

            }
            .setOnCancelListener {

            }
            .setPositiveButton(R.string.update){ _, _->}
            .setNegativeButton(android.R.string.cancel){ dialog, _ -> dialog.dismiss()
            }
            .create()
        myDialog.setCancelable(false)
        myDialog.show()
        myDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            getWorkAndOffDaysGroups(view){
                userModel.updateDayGroups(it){
                    ContextCompat.startForegroundService(context.applicationContext,Intent(applicationContext, AlarmService::class.java))
                }
            }
            myDialog.dismiss()
        }

    }

    private fun getWorkAndOffDaysNames(view:View?,  onComplete:(workDaysNames:ArrayList<String>,offDaysNames:ArrayList<String>)->Unit){
        var workDaysNames:ArrayList<String>? = null
        var offDaysNames:ArrayList<String>? = null

        if (view!=null) {
            val daysCheckboxes = listOf(
                view.findViewById(R.id.day0Checkbox),
                view.findViewById(R.id.day1Checkbox),
                view.findViewById(R.id.day2Checkbox),
                view.findViewById(R.id.day3Checkbox),
                view.findViewById(R.id.day4Checkbox),
                view.findViewById(R.id.day5Checkbox),
                view.findViewById<MaterialCheckBox>(R.id.day6Checkbox),
            )
            workDaysNames = arrayListOf()
            offDaysNames = arrayListOf()
            daysCheckboxes.forEachIndexed { index, it ->
                if (it.isChecked) {
                    workDaysNames!!.add(it.text.toString())
                } else {
                    offDaysNames!!.add(it.text.toString())
                }
            }
            onComplete(workDaysNames,offDaysNames)
        }else {
            userModel.workDays.observe(this){
                workDaysNames = ArrayList(it.days)
                if (offDaysNames!=null)
                    onComplete(workDaysNames!!, offDaysNames!!)
            }

            userModel.offDays.observe(this){
                offDaysNames = ArrayList(it.days)
                if (workDaysNames!=null)
                    onComplete(workDaysNames!!, offDaysNames!!)
            }
        }
    }
    private fun getWorkAndOffDaysGroups(view: View? = null , onComplete:(Pair<DayGroup, DayGroup>)->Unit) {
        var wNames:ArrayList<String>? = null
        var oNames:ArrayList<String>? = null

        val fire = {
            getNonNullUserValue {

                if (wNames == null) {
                    wNames = ArrayList(it.workday.days)
                }

                if (oNames == null) {
                    oNames = ArrayList(it.offDay.days)
                }

                val workGroup =DayGroup.builder()
                    .sleepTime(workDaySleepTime?.toString() ?: it.workday.sleepTime)
                    .wakeUpTime(workDayWakeTime?.toString() ?: it.workday.wakeUpTime)
                    .days(wNames)
                    .build()

                val offGroup =DayGroup.builder()
                    .sleepTime(offDaySleepTime?.toString() ?: it.offDay.sleepTime)
                    .wakeUpTime(offDayWakeTime?.toString() ?: it.offDay.wakeUpTime)
                    .days(wNames)
                    .build()

                onComplete(Pair(workGroup, offGroup))
            }
        }

        if (view!=null){
            getWorkAndOffDaysNames(view){ workDaysNames, offDaysNames ->
                wNames = workDaysNames
                oNames = offDaysNames
                fire()
            }
        }else fire()
    }

    private fun observeDayGroups(){
        userModel.offDays.observe(this){
            it ?: return@observe
            binding.sleepWakeTimesView.offDaysNames.apply {
                this.visibility = View.VISIBLE
                this.text = it.days.toString()
            }
            val st = DBParameters.SLEEP_TIME + ": " +it.sleepTime.toString()
            binding.sleepWakeTimesView.offDaySleepTimeText.text = st
            val wt = DBParameters.WAKEUP_TIME + ": " +it.wakeUpTime.toString()
            binding.sleepWakeTimesView.offDayWakeTimeText.text = wt
        }


        userModel.workDays.observe(this){
            it ?: return@observe
            binding.sleepWakeTimesView.workDaysNames.apply {
                this.visibility = View.VISIBLE
                this.text = it.days.toString()
            }
            val st = DBParameters.SLEEP_TIME + ": " +it.sleepTime.toString()
            binding.sleepWakeTimesView.workdaySleepTimeText.text = st
            val wt = DBParameters.WAKEUP_TIME + ": " + it.wakeUpTime.toString()
            binding.sleepWakeTimesView.workDayWakeTimeText.text = wt
        }
    }
    private fun changePassword(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.password_update_layout, null, false)
        val textView = view.findViewById<TextView>(R.id.messageText)
        val myDialog = AlertDialog.Builder(context)
            .setTitle(R.string.change_password)
            .setView(view)
            .setOnDismissListener {

            }
            .setOnCancelListener {

            }
            .setPositiveButton(R.string.update){ _, _->}
            .setNegativeButton(android.R.string.cancel){ dialog, _ -> dialog.dismiss()
            }
            .create()
        myDialog.setCancelable(false)
        myDialog.show()
        myDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            val oldPassword = (view.findViewById(R.id.oldPassword) as TextInputLayout)
                .editText?.text.toString()
            val password = (view.findViewById(R.id.password_update) as TextInputLayout)
            .editText?.text.toString()
            val confirm = (view.findViewById(R.id.password_update_confirm) as TextInputLayout)
                .editText?.text.toString()
            val result = InputValidator.checkPassword(
                context,
                password, confirm
            )
            if (result.isEmpty()){
                myDialog.dismiss()
                Amplify.Auth.updatePassword(oldPassword,password,
                    {
                        showAlertMessage(context.getString(R.string.Password_successfully_changed), context)
                    },
                    {
                        it.message?.let { it1 ->
                            showAlertMessage(it1, context) }
                    })
            }else{
                textView.text = result
            }
        }
    }
    fun showTimePickerDialog(v: View) {
        when (v.id) {
            binding.timesPickLayout.addWorkDaySleepTime.id -> TimePickerFragment(PickedTimeFor.WORK_DAY_SLEEP_TIME).show(supportFragmentManager, "timePicker")
            binding.timesPickLayout.addWorkDayWakeTime.id -> TimePickerFragment(PickedTimeFor.WORK_DAY_WAKE_TIME).show(supportFragmentManager, "timePicker")
            binding.timesPickLayout.addOffDaySleepTime.id -> TimePickerFragment(PickedTimeFor.OFF_DAY_SLEEP_TIME).show(supportFragmentManager, "timePicker")
            binding.timesPickLayout.addOffDayWakeTime.id -> TimePickerFragment(PickedTimeFor.OFF_DAY_WAKE_TIME).show(supportFragmentManager, "timePicker")
        }
    }
    override fun onTimePicked(pickedTimeFor: PickedTimeFor, timePoint: TimePoint) {
        when (pickedTimeFor) {
            PickedTimeFor.WORK_DAY_SLEEP_TIME -> {
                workDaySleepTime  = timePoint
                binding.timesPickLayout.addWorkDaySleepTime.text = timePoint.toString()
            }
            PickedTimeFor.WORK_DAY_WAKE_TIME -> {
                workDayWakeTime = timePoint
                binding.timesPickLayout.addWorkDayWakeTime.text = timePoint.toString()
            }

            PickedTimeFor.OFF_DAY_SLEEP_TIME -> {
                offDaySleepTime = timePoint
                binding.timesPickLayout.addOffDaySleepTime.text = timePoint.toString()
            }
            PickedTimeFor.OFF_DAY_WAKE_TIME -> {
                offDayWakeTime = timePoint
                binding.timesPickLayout.addOffDayWakeTime.text = timePoint.toString()
            }
        }
        binding.updateTimeButton.isEnabled = true
    }
    private fun confirmLogout(context: Context){
        val myDialog = AlertDialog.Builder(context)
            .setMessage("Are you sure?")
            .setPositiveButton(android.R.string.ok) { _, _ ->
                Amplify.Auth.signOut {
                    val intent = Intent(context.applicationContext, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                }
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create()
        myDialog.show()
    }
    private fun showAlertMessage(errorText: String, context: Context) {
        runOnUiThread{
            val myDialog = AlertDialog.Builder(context)
                .setMessage(errorText)
                .setNeutralButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
                .create()
            myDialog.show()
        }
    }
    private fun onFailure(message: String){
        runOnUiThread {
            root.visibility = View.VISIBLE
            Toast.makeText(
                this, message,
                Toast.LENGTH_LONG
            ).show()
        }
    }
    private fun showProgressIndicator() {
        root.visibility = View.GONE
        if (myDialog == null) {
            myDialog = AlertDialog.Builder(this)
                .setView(R.layout.progress_view)
                .setOnDismissListener {

                    myDialog = null
                }
                .setOnCancelListener {

                    myDialog = null
                }
                .create()
        }
        myDialog?.show()
    }


}