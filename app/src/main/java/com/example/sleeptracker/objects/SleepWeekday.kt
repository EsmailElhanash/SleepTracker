package com.example.sleeptracker.objects

enum class GroupType{
    OFF_DAYS, WORK_DAYS, NO_TYPE;

    companion object {
        fun fromString(type: String): GroupType? {
            return if (type == "OFF_DAYS") OFF_DAYS else if (type == "WORK_DAYS") WORK_DAYS else null
        }
    }
}

data class DaysGroup
    (var daysNames : List<String>, var groupType : GroupType, var sleepTime: TimePoint, var wakeTime : TimePoint)
