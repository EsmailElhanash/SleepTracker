package com.example.sleeptracker.ui

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.example.sleeptracker.objects.TimePoint
import com.example.sleeptracker.utils.time.PickedTimeFor
import com.example.sleeptracker.utils.time.TimePickerListener
import java.util.*

class TimePickerFragment(private val pickedTimeFor : PickedTimeFor) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
//        Log.d(TAG, "hourOfDay=$hourOfDay minute=$minute")
        if (activity is TimePickerListener)
            (activity as TimePickerListener).onTimePicked(pickedTimeFor, TimePoint(minute,hourOfDay))

    }
}