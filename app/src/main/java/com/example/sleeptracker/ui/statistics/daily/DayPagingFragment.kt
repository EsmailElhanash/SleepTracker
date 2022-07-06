package com.example.sleeptracker.ui.statistics.daily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.core.model.query.predicate.QueryPredicate
import com.amplifyframework.datastore.generated.model.TrackerPeriod
import com.example.sleeptracker.R
import com.example.sleeptracker.aws.AWS
import com.example.sleeptracker.utils.time.DAY_IN_MS
import com.example.sleeptracker.utils.time.IDFormatter
import com.example.sleeptracker.utils.time.TimeUtil
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val loadingBar:CircularProgressIndicator = view.findViewById(R.id.loadingBar)

        val periodsIDs = getPeriodsIDs()

        pagingAdapter = PagingAdapter(this)
        viewPager = view.findViewById(R.id.daysPager)
        viewPager.adapter = pagingAdapter

        val tabLayout = view.findViewById<TabLayout>(R.id.days_tabs_layout)


        var predicates : QueryPredicate = Where.matches(TrackerPeriod.ID.beginsWith(periodsIDs.first())).queryPredicate
        periodsIDs.forEach { pid ->
            predicates = predicates.or(TrackerPeriod.ID.beginsWith(pid))
         }
        AWS.getPredicate(
            predicates,
            TrackerPeriod::class.java
        ){
            if (it.data?.isEmpty() == true || it.data == null) return@getPredicate
            val shots = arrayListOf<TrackerPeriod>()
            it.data.forEach { model ->
                shots.add(model as TrackerPeriod)
            }
            activity?.runOnUiThread {
                pagingAdapter.shots.addAll(shots)
                pagingAdapter.notifyDataSetChanged()
                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = IDFormatter.getID(pagingAdapter.shots[position].createdAt.toDate().time)
                }.attach()
                loadingBar.visibility = View.GONE
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

