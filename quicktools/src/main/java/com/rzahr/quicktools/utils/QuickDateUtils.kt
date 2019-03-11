@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.rzahr.quicktools.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object QuickDateUtils {

    const val DASHED_FORMAT = "yyyy-MM-dd HH:mm:ss"
    const val SLASHED_FORMAT = "dd/MM/yyyy hh:mm:ss a"

    /**
     * returns a string date
     * @param date the date
     * @param format the format
     */
    fun getDateString(date: Date, format: String = DASHED_FORMAT): String {

        val calendar = Calendar.getInstance()
        calendar.time = date
        return SimpleDateFormat(format, Locale.ENGLISH).format(calendar.time)
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(english: Boolean = true, format: String = DASHED_FORMAT): String {

        val now = Date()
        return if (english)
            SimpleDateFormat(format, Locale.ENGLISH).format(now)
        else
            SimpleDateFormat(format).format(now)
    }

    /**
     * changes the time to string
     */
    fun changeTimeToString(lastLocationDate: Long, format: String = SLASHED_FORMAT, timeZone: TimeZone? = null): String {

        val simpleDateFormat = SimpleDateFormat(format, Locale.ENGLISH)
        if (timeZone != null) simpleDateFormat.timeZone = timeZone
        return simpleDateFormat.format(Date(lastLocationDate).time)
    }
}