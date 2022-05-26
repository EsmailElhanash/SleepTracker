package com.example.sleeptracker.ui.statistics.weekly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.collection.ArrayMap
import androidx.fragment.app.Fragment
import com.example.sleeptracker.databinding.FragmentWeekReportBinding
import com.example.sleeptracker.ui.statistics.utils.WeekXAxisValueFormatter
import com.example.sleeptracker.ui.statistics.utils.WeekYAxisValueFormatter
import com.example.sleeptracker.utils.androidutils.ScreenUtils
import com.example.sleeptracker.utils.time.DAY_IN_MS
import com.example.sleeptracker.utils.time.TimeUtil
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import java.util.*


private const val DISPLAYABLE_DAYS_COUNT = 7
class WeekGraphFragment : Fragment() {
    private val durationsMap = ArrayMap<String,Float>()
    private lateinit var binding: FragmentWeekReportBinding
    private var size:Int = 0
    private val chartData = arrayListOf<IBarDataSet>()
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeekReportBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val periodsIDs = getPeriodsIDs()
        size = periodsIDs.size
        setUpGraph()
        displayData(periodsIDs)
    }

    private fun displayData(periodsIDs:Array<String>){
//        val uid = Firebase.auth.uid ?:return
//        DBAccessPoint.queryPeriods(uid,periodsIDs).forEach { query ->
//            query.get().addOnSuccessListener { shot ->
//                if (shot == null) return@addOnSuccessListener
//
//                val pid = shot.key ?: return@addOnSuccessListener
//                val start = shot.child("Sleep Time").value?.toString() ?: return@addOnSuccessListener
//
//                val end = shot.child("Wake up Time").value?.toString() ?: return@addOnSuccessListener
//
//                val sleepTimeMS = TimeUtil.getTimeMS(start,pid)
//                val wakeTimeMS = TimeUtil.getTimeMS(end,pid)
//
//                if (sleepTimeMS==null || wakeTimeMS == null ) return@addOnSuccessListener
//
//                val sleepPeriod = SleepPeriod(Period(sleepTimeMS,wakeTimeMS))
//
//                sleepPeriod.calculateSleepDuration{ sleepDuration ->
//                    durationsMap[pid] = getSleepingDurationFloat(sleepDuration)
//                    updateGraphData(pid)
//                }
//
//            }
//        }

    }

    private fun setUpGraph(){
        val chart = binding.chart
        chart.minimumHeight = ScreenUtils.getHeight()*2/3
    }
    private fun updateGraphData(pid:String){
        val msDay = TimeUtil.dateFormatToMS(pid)
        val fs = msDay?.let { TimeUtil.getDayDateMSFloat(it) }
        val entry = fs?.let { BarEntry(it, durationsMap[pid]!!) } ?: return

        val chart = binding.chart
        val setEntries = arrayListOf<BarEntry>()
        setEntries.add(entry)
        val set = BarDataSet(setEntries, "")
        chartData.add(set)
        val barData = BarData(chartData)
        barData.setDrawValues(false)
        chart.data = barData

        val yAxis2 = chart.axisRight
        yAxis2.setDrawZeroLine(false)
        yAxis2.setDrawLabels(false)
        yAxis2.setDrawTopYLabelEntry(false)
        yAxis2.setDrawAxisLine(false)
        yAxis2.setDrawGridLines(false)
        yAxis2.setDrawLimitLinesBehindData(false)
        yAxis2.setDrawGridLinesBehindData(false)


        val yAxis1 = chart.axisLeft
        yAxis1.valueFormatter = WeekYAxisValueFormatter()

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        xAxis.valueFormatter = WeekXAxisValueFormatter()
        chart.legend.form = Legend.LegendForm.EMPTY

        chart.description.isEnabled = false
        chart.invalidate()
    }

    private fun getSleepingDurationFloat(msDuration: Long):Float{
        return (msDuration.toDouble() / (1000 * 60 * 60) % 24).toFloat()
    }

    private fun getPeriodsIDs() : Array<String> {
        val nowMS = Calendar.getInstance().timeInMillis - DAY_IN_MS
        return Array(DISPLAYABLE_DAYS_COUNT) {
            TimeUtil.getIDDateFormat(nowMS - (it * DAY_IN_MS))
        }
    }
}