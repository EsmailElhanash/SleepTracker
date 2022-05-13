package com.example.sleeptracker.utils.time

import com.example.sleeptracker.objects.TimePoint
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.*

class TimeUtilTest {

    @Test
    fun getSleepAndWakeTimesMS() {
        val h1 = 12..18
        val h2 = 20..22
        val m1 = 10..20
        val m2 = 4..10
        h1.forEach { h0 ->
            m1.forEach { m0 ->
                h2.forEach { h->
                    m2.forEach { m->
                        val (sleep,wake)=TimeUtil.getStartEndPair(
                                TimePoint(m0,h0),
                                TimePoint(m,h),Calendar.getInstance().timeInMillis
                        )
                        assertThat(wake-sleep).isIn(0..DAY_IN_MS)
                    }
                }
            }
        }
    }

    @Test
    fun awakeBiggerThanNow() {
        val h1 = 0..23
        val h2 = 0..23
        val m1 = 0..59
        val m2 = 0..59
        val now = Calendar.getInstance().timeInMillis
        h1.forEach { h0 ->
            m1.forEach { m0 ->
                h2.forEach { h->
                    m2.forEach { m->
                            val (sleep, wake) = TimeUtil.getStartEndPair(
                                    TimePoint(m0, h0),
                                    TimePoint(m, h),Calendar.getInstance().timeInMillis
                            )
                            assertThat(wake).isGreaterThan(now)
                    }
                }
            }
        }
    }

    @Test
    fun getSleepAndWakeTimesMS_test2() {
        val pair = TimeUtil.getStartEndPair(TimePoint(0,22),
            TimePoint(0,6),0)
        print("testresult22:${pair.first}:${pair.second}")
    }
    @Test
    fun convertHHMMToMsTest(){
        val hs = IntRange(
            0,23
        )
        val ms = IntRange(
            0,59
        )
        hs.forEach { h->
            ms.forEach { m ->
                val expected = h * 60 * 60 * 1000 + m * 60 * 1000
                val duration = "$h:$m"
                assertThat(TimeUtil.convertHHMMDurationToMs(duration))
                    .isEqualTo(expected)
                print(TimeUtil.convertHHMMDurationToMs(duration))
            }
        }

    }

    @Test
    fun pidToMsTest(){

    }
}