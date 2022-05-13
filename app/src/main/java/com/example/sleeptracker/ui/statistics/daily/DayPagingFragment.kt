package com.example.sleeptracker.ui.statistics.daily

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.TrackerPeriod
import com.example.sleeptracker.R
import com.example.sleeptracker.aws.DB
import com.example.sleeptracker.database.DBAccessPoint
import com.example.sleeptracker.utils.time.DAY_IN_MS
import com.example.sleeptracker.utils.time.TimeUtil
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.DataSnapshot
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

private const val DISPLAYABLE_DAYS_COUNT = 7

class DayPagingFragment : Fragment() {
    private lateinit var pagingAdapter : PagingAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_statistics_holder, container, false)
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val stateLayout:LinearLayout = view.findViewById(R.id.stateLayout)

        val periodsIDs = getPeriodsIDs()
//        val uid = Firebase.auth.uid ?: return
        pagingAdapter = PagingAdapter(this)
        viewPager = view.findViewById(R.id.daysPager)
//        viewPager.adapter = pagingAdapter
//
//        val tabLayout = view.findViewById<TabLayout>(R.id.days_tabs_layout)
//        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            tab.text = pagingAdapter.shots[position].id
//        }.attach()
        val shots:ArrayList<TrackerPeriod> = arrayListOf()
        periodsIDs.forEach { pid ->
            DB.getPredicate(
                Where.matches(TrackerPeriod.ID.beginsWith(pid)).queryPredicate,
                TrackerPeriod::class.java
            ){
                val shot = it.data as? TrackerPeriod  ?: return@getPredicate
                shots.add(shot)
            }
         }




    }



    private fun getPeriodsIDs() : Array<String> {
        val nowMS = Calendar.getInstance().timeInMillis - DAY_IN_MS
        return Array(DISPLAYABLE_DAYS_COUNT) {
            TimeUtil.getIDSimpleFormat(nowMS - (it * DAY_IN_MS))
        }
    }


    private inner class PagingAdapter(fragment: Fragment)
        : FragmentStateAdapter(fragment) {
        var shots:ArrayList<TrackerPeriod> = arrayListOf()

        override fun getItemCount(): Int {
            return shots.size
        }
        override fun createFragment(position: Int): Fragment {
            return DayStatisticsFragment(shots[position])
        }
        override fun getItemViewType(position: Int): Int {
            return position
        }
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
    }
}

