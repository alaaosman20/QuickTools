@file:Suppress("unused")

package com.rzahr.quicktools.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object QuickDateUtils {

    /**
     * changes the time to string
     */
    fun changeTimeToString(lastLocationDate: Long, format: String = "dd/MM/yyyy hh:mm:ss a"): String {

        return SimpleDateFormat(format, Locale.ENGLISH).format(java.util.Date(lastLocationDate).time)
    }

    /**
     * returns a string date
     * @param date the date
     * @param dateFormat the format
     */
    fun getDateString(date: Date, dateFormat: String): String {

        val calendar = Calendar.getInstance()
        calendar.time = date
        return SimpleDateFormat(dateFormat, Locale.ENGLISH).format(calendar.time)
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(english: Boolean, format: String = "yyyy-MM-dd HH:mm:ss"): String {

        val now = Date()
        return if (english)
            SimpleDateFormat(format, Locale.ENGLISH).format(now)
        else
            SimpleDateFormat(format).format(now)
    }
}