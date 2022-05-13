package com.example.sleeptracker.ui.statistics.utils

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.math.BigDecimal
import java.math.RoundingMode

class WeekYAxisValueFormatter: ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return BigDecimal(value.toString())
                .setScale(1, RoundingMode.HALF_EVEN)
                .toString() +" Hr"
    }
}