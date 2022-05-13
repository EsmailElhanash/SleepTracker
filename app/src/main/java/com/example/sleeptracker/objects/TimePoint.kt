package com.example.sleeptracker.objects

data class TimePoint(var minute: Int, var hour: Int){
    override fun toString(): String {
        return hours2Digits() + ":" + minutes2Digits()
    }


    companion object{
        fun stringToObject(s:String) : TimePoint? {
            return try {
                val r = TimePoint(
                        s.substring(3..4).toInt(),
                        s.substring(0..1).toInt(),
                )
                if (s.contains("PM")||s.contains("pm")||s.contains("am")||s.contains("AM"))
                    r.hour +=12

                r
            }catch (e:Exception){
                print(e.localizedMessage)
                null
            }
        }
    }
    private fun minutes2Digits() : String {
        return if (minute < 10) "0$minute"
        else minute.toString()
    }
    private fun hours2Digits() : String {
        return if (hour < 10) "0$hour"
        else hour.toString()
    }
}
