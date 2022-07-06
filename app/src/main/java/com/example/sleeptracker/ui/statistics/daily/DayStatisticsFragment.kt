package com.example.sleeptracker.ui.statistics.daily

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.color
import androidx.fragment.app.Fragment
import com.amplifyframework.datastore.generated.model.TrackerPeriod
import com.example.sleeptracker.databinding.FragmentSleepingPeriodBinding

const val HOUR_IN_MS : Long = 3600000L

class DayStatisticsFragment(private val period: TrackerPeriod) : Fragment() {
    private lateinit var binding: FragmentSleepingPeriodBinding
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentSleepingPeriodBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        displayData()
    }


    private fun displayData() {
        val stepCount = period.totalMovements
        binding.stepsCount.text = getStepsText(stepCount)

        val sleepTime = period.actualSleepTime
        val wakeTime = period.actualWakeUpTime

        val wakeUpTimeText = wakeTime?.toString()
        val sleepUpTimeText = sleepTime?.toString()

        val periodInfoText = "$sleepUpTimeText To $wakeUpTimeText"
        binding.sleepingPeriod.text = periodInfoText
        binding.sleepDuration.text = period.sleepDuration

    }

    private fun getStepsText(stepCount: Int?): SpannableStringBuilder {
        return if (stepCount != null) {
            SpannableStringBuilder()
                .append("Movements: ")
                .color(Color.BLUE) {
                    append("$stepCount")
                }
        } else {
            SpannableStringBuilder().append(
                    "Movements: not available"
            )
        }
    }

//    private fun setUpLightSensorGraph(
//            chart: BarChart,
//            lightSensorData: ArrayMap<String, Float>,
//            pid: String,
//            sleepTimeForThatPID: Long,
//            wakeUpTimeForThatPID: Long,
//    ) {
//        val keys = lightSensorData.keys
//        val dataSets = arrayListOf<IBarDataSet>()
//        chart.minimumHeight = ScreenUtils.getHeight() * 2 / 3
//
//        keys.forEach { s ->
//            val x = TimeUtil.timeFormatToMS(s, pid)?.toFloat()
//            if (x != null && lightSensorData[s] != null) {
//                val setEntries = arrayListOf<BarEntry>()
//                val entry = BarEntry(x, lightSensorData[s]!!)
//                setEntries.add(entry)
//                val set = BarDataSet(setEntries, "")
//                set.barBorderWidth = 8.0F
//                set.barBorderColor = UniqueColor.get()
//    //                set.valueFormatter = TimeValueFormatter()
//                set.calcMinMax()
//                dataSets.add(set)
//            }
//        }
//        val barData = BarData(dataSets)
//        chart.data = barData
//
//        val yAxis1 = chart.axisLeft
//        yAxis1.axisLineWidth = 1F
//        yAxis1.zeroLineColor = Color.BLACK
//        yAxis1.setDrawZeroLine(true)
//        yAxis1.mAxisMinimum = 0F
//        yAxis1.valueFormatter = YAxisValueFormatter()
//
//
//        val yAxis2 = chart.axisRight
//        yAxis2.setDrawZeroLine(false)
//        yAxis2.setDrawLabels(false)
//        yAxis2.setDrawTopYLabelEntry(false)
//        yAxis2.setDrawAxisLine(false)
//        yAxis2.setDrawGridLines(false)
//        yAxis2.setDrawLimitLinesBehindData(false)
//        yAxis2.setDrawGridLinesBehindData(false)
//
//
//        val xAxis = chart.xAxis
//        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.valueFormatter = XAxisValueFormatter()
//        xAxis.mAxisMinimum = sleepTimeForThatPID.toFloat()
//        xAxis.mAxisMaximum = wakeUpTimeForThatPID.toFloat()
//
//        xAxis.axisLineWidth = 1F
//        chart.legend.form = Legend.LegendForm.EMPTY
//
//        chart.description.isEnabled = false
//        chart.invalidate()
//    }

}