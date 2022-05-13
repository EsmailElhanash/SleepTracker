package com.example.sleeptracker.ui.statistics.utils

import android.graphics.Color
import kotlin.random.Random

object UniqueColor {
    private val colorsList = listOf(
            Color.CYAN,
            Color.BLUE,
            Color.GRAY,
            /*Color.LTGRAY,
            Color.YELLOW,*/
            Color.RED,
            Color.GREEN,
            Color.MAGENTA
    )
    private var curList = colorsList.toMutableList()
    fun get(): Int{
        return if (curList.isNotEmpty()){
            curList.removeAt(Random.nextInt(0, curList.size))
        }else{
            curList = colorsList.toMutableList()
            get()
        }
    }
}