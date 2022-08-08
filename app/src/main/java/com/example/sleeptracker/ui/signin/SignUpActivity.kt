package com.example.sleeptracker.ui.signin

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.children
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.DayGroup
import com.amplifyframework.datastore.generated.model.User
import com.example.sleeptracker.R
import com.example.sleeptracker.aws.AWS
import com.example.sleeptracker.models.UserModel
import com.example.sleeptracker.databinding.ActivitySignUpBinding
import com.example.sleeptracker.objects.DaysGroup
import com.example.sleeptracker.objects.GroupType
import com.example.sleeptracker.objects.TimePoint
import com.example.sleeptracker.ui.ConsentActivity
import com.example.sleeptracker.ui.MainActivity
import com.example.sleeptracker.ui.MainActivity.Companion.TEST
import com.example.sleeptracker.ui.TimePickerFragment
import com.example.sleeptracker.utils.androidutils.InputValidator
import com.example.sleeptracker.utils.time.PickedTimeFor
import com.example.sleeptracker.utils.time.TimePickerListener
import com.example.userdataapp.App2Service
import com.example.userdataapp.SignUpStrings
import com.google.android.material.radiobutton.MaterialRadioButton
import kotlin.collections.HashMap


class SignUpActivity : AppCompatActivity()
        , TimePickerListener {

    private lateinit var binding: ActivitySignUpBinding
    private var workDaySleepTime: TimePoint? = null
    private var workDayWakeTime: TimePoint? = null
    private var offDaySleepTime: TimePoint? = null
    private var offDayWakeTime: TimePoint? = null
    private lateinit var root : View
    private var myDialog: AlertDialog? = null
    private var id1 : String? = null
    private var id2 : String? = null
    private var userCreated = false
    private val user : UserModel by viewModels()

    private val app2SignUpReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent==null) return
            val errors = intent.getStringExtra(App2Service.SIGN_UP_ERROR)
            Log.d("app2SignUpReceiver", "onReceive: $errors")
            if (errors==null){
                val sid = intent.getStringExtra(App2Service.SID)
                val result = intent.getBooleanExtra(App2Service.SIGN_UP_RESULT,false)
                if (result){
                    if (sid!=null){
                        id2 = sid
                        createUser()
                    }
                }else {
                    onFailure("error occurred")
                }
            }else {
                onFailure(errors)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(app2SignUpReceiver,
            IntentFilter(App2Service.SIGN_UP_RESULT))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        root = binding.root
        setContentView(root)
        setUpEthnicRadioGroup()
        binding.genderPicker.adapter =
                GenderSpinnerAdapter(this, resources.getStringArray(R.array.gender_entries).asList())

        binding.submitButton.setOnClickListener {
            submit()
        }
        if (TEST) setTest()


    }


    @SuppressLint("all")
    private fun setTest() {
        workDaySleepTime = TimePoint(0,0)
        workDayWakeTime = TimePoint(0,0)
        offDaySleepTime = TimePoint(0,0)
        offDayWakeTime = TimePoint(0,0)
        binding.ethnicGroups.ethnicRadioGroup.check(R.id.asian_ethnic)
        binding.nameInput.editText?.setText("eeee0000")
        binding.ageInput.editText?.setText("22")
        binding.emailInput.editText?.setText("esmailelhanash${MainActivity.c}@gmail.com")
        binding.genderPicker.setSelection(1)
        binding.passwordInput.editText?.setText("eeee1111")
        binding.passwordConfirmInput.editText?.setText("eeee1111")
    }

    private fun dataToMap() : HashMap<String,String> {
        val ethnicID = binding.ethnicGroups.ethnicRadioGroup.checkedRadioButtonId
        val ethnicText = findViewById<RadioButton>(ethnicID).text.toString()
        val name = binding.nameInput.editText?.text.toString()
        val pw = binding.passwordInput.editText?.text.toString()
        val i = binding.genderPicker.selectedItemPosition
        val gender = ((binding.genderPicker.adapter as GenderSpinnerAdapter).getItem(i) as String)
        val age = binding.ageInput.editText?.text.toString()
        val email = binding.emailInput.editText?.text.toString()

        return hashMapOf(
            SignUpStrings.ETHNIC to ethnicText,
            SignUpStrings.NAME to name,
            SignUpStrings.PASSWORD to pw,
            SignUpStrings.GENDER to gender,
            SignUpStrings.AGE to age,
            SignUpStrings.EMAIL to email,
        )
    }





    private fun submit() {
        var inputsError = InputValidator.checkInputs(this, binding)
        if (inputsError.isEmpty()) {
            if ((workDaySleepTime == null || workDayWakeTime == null || offDaySleepTime == null || offDayWakeTime == null)) {
                inputsError = getString(R.string.specify_sleep_awake_time)
                showAlertMessage(inputsError,this)
            } else {
                showProgressIndicator()
                signUp1{
                    signUp2(it)
                }
            }
        } else {
            showAlertMessage(inputsError,this)
        }
    }

    private fun signUp1(onSidReady:(sid:String) -> Unit){
        val email = binding.emailInput.editText?.text.toString()
        val pw = binding.passwordInput.editText?.text.toString()

        val options = AuthSignUpOptions.builder()
            .userAttribute(AuthUserAttributeKey.email(), email)
            .build()
        Amplify.Auth.signUp(email, pw, options,
            lit@{ it1 ->
                val id = it1.user?.userId
                if (id==null){
                    onFailure(it1.toString())
                    return@lit
                }
                onSidReady(id)

                Amplify.Auth.signIn(email,pw, {
                    if (it.isSignInComplete) {
                        id1 = id
                        createUser()
                    }
                },
                    {
                        it.message?.let { it2 -> onFailure(it2) }
                    }
                )

            },{
                it.localizedMessage?.let { it1 -> onFailure(it1) }
            }
        )
    }

    private fun signUp2(sid:String) {
        val data = dataToMap()
        val intent2 = Intent(this, App2Service::class.java)
        intent2.putExtra(App2Service.REQUEST,App2Service.SIGN_UP)
        data.entries.forEach {
            intent2.putExtra(it.key,it.value)
        }
        intent2.putExtra(SignUpStrings.SID,sid)
        applicationContext.startService(intent2)
    }

    private fun onFailure(message: String){
        runOnUiThread {
            root.visibility = View.VISIBLE
            myDialog?.cancel()
            Toast.makeText(
                this, message,
                Toast.LENGTH_LONG
            ).show()
        }
    }


    private fun createUser()  {
        if (id1 != null && id2!= null && !userCreated){
            userCreated = true
            val daysGroups = getWorkAndOffDaysGroups()
            if (daysGroups == null){
                onFailure("error occurred")
                return
            }
            val workDays = createWorkDaysEntry(daysGroups.first)
            val offDays = createOffDaysEntry(daysGroups.second)

            val u = User.builder()
                .sid(id2)
                .offDay(offDays)
                .workday(workDays)
                .retakeSurveyPeriod(28)
                .id(id1)
                .build()

            AWS.save(u){
                if (it.success){
                    runOnUiThread {
                        user.init()
                        val consentIntent = Intent(this,ConsentActivity::class.java)
                        startActivity(consentIntent)
                    }
                }else if (it.error!= null){
                    onFailure(it.error)
                }
            }
        }

    }

    private fun setUpEthnicRadioGroup(){
        binding.ethnicGroups.ethnicRadioGroup.children.forEach {
            if (it is MaterialRadioButton) {
                val text = it.text
                val hint = it.hint
                it.text = HtmlCompat.fromHtml("<br />" +
                        "<b><font color='" + Color.BLACK + "'>" + text + "</font></b>" + "<br />" +
                        "<small> <font color='" + Color.GRAY + "'>" + hint + "</font></small>" + "<br />",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
        }
    }

    private fun createWorkDaysEntry( workDays: DaysGroup): DayGroup{
        return DayGroup.builder()
            .sleepTime(workDays.sleepTime.toString())
            .wakeUpTime(workDays.wakeTime.toString())
            .days(workDays.daysNames)
            .build()
    }

    private fun createOffDaysEntry(offDays: DaysGroup) : DayGroup{
        return DayGroup.builder()
            .sleepTime(offDays.sleepTime.toString())
            .wakeUpTime(offDays.wakeTime.toString())
            .days(offDays.daysNames)
            .build()
    }

    private fun getWorkAndOffDaysGroups(): Pair<DaysGroup, DaysGroup>? {
        if (workDaySleepTime == null || workDayWakeTime == null || offDaySleepTime == null || offDayWakeTime == null) {
            return null
        }

        val workDays = arrayListOf<String>()
        val offDays = arrayListOf<String>()
        val daysCheckboxes = listOf(
            binding.weekDaysCheckboxes.day0Checkbox,
            binding.weekDaysCheckboxes.day1Checkbox,
            binding.weekDaysCheckboxes.day2Checkbox,
            binding.weekDaysCheckboxes.day3Checkbox,
            binding.weekDaysCheckboxes.day4Checkbox,
            binding.weekDaysCheckboxes.day5Checkbox,
            binding.weekDaysCheckboxes.day6Checkbox
        )

        daysCheckboxes.forEachIndexed { _, it ->
            if (it.isChecked) {
                workDays.add(it.text.toString())
            } else {

                offDays.add(it.text.toString())
            }
        }
        val workGroup =
            DaysGroup(workDays, GroupType.WORK_DAYS, workDaySleepTime!!, workDayWakeTime!!)
        val offGroup =
            DaysGroup(offDays, GroupType.WORK_DAYS, offDaySleepTime!!, offDayWakeTime!!)
        return Pair(workGroup, offGroup)
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
    }

    private fun showAlertMessage(errorText: String, context: Context) {
        myDialog = AlertDialog.Builder(context)
            .setMessage(errorText)
            .setNeutralButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            .setOnDismissListener {
//                        root.visibility = View.VISIBLE
                myDialog = null
            }
            .setOnCancelListener {
//                        root.visibility = View.VISIBLE
                myDialog = null
            }
            .create()
        myDialog?.show()
    }
}