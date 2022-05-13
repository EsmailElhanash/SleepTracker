package com.example.sleeptracker.utils.time

import com.example.sleeptracker.objects.TimePoint


interface TimePickerListener {
    fun onTimePicked(pickedTimeFor : PickedTimeFor, timePoint: TimePoint)
}