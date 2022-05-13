package com.example.sleeptracker.ui.statistics

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.example.sleeptracker.R
import com.example.sleeptracker.databinding.ActivityStatisticsBinding
import com.example.sleeptracker.ui.statistics.daily.DayPagingFragment
import com.example.sleeptracker.ui.statistics.weekly.WeekGraphFragment

const val STATISTICS_TYPE = "STATISTICS_TYPE"
const val DAILY_STATISTICS = 0

class StatisticsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatisticsBinding
    private lateinit var root : View



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        root = binding.root
        setContentView(root)

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<DayPagingFragment>(R.id.fragment_container_view)
        }

    }


}