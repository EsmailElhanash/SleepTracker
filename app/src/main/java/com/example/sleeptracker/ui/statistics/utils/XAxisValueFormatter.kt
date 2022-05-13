package com.example.sleeptracker.ui.statistics.utils

import com.example.sleeptracker.utils.time.TimeUtil
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class XAxisValueFormatter: ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return TimeUtil.formatTimeMS(value.toLong())
    }
}