package com.example.aspectjx.utils

import android.os.SystemClock
import android.util.Log

object SingleClickUtil {

    private var lastClickTime: Long = 0

    fun isDoubleClick(intervalMillis: Long): Boolean {
        val time = SystemClock.elapsedRealtime()
        val timeInterval = Math.abs(time - lastClickTime)
        Log.d("ClickUtil", "timeInterval:$timeInterval, millis:$intervalMillis")
        return if (timeInterval < intervalMillis) {
            true
        } else {
            lastClickTime = time
            false
        }
    }

}