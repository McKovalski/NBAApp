package com.example.myapplication.helpers

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateTimeHelper {

    @RequiresApi(Build.VERSION_CODES.O)
    fun getLongFormattedDate(date: String): String {
        return LocalDate.parse(date.subSequence(0, 10))
            .format(DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy"))
    }

    fun getMonth(date: String): Int {
        return date.substring(5, 7).toInt()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMonthAndYear(date: String): String {
        return LocalDate.parse(date.subSequence(0, 10))
            .format(DateTimeFormatter.ofPattern("MMMM yyyy"))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDayOfWeek(date: String): String {
        return LocalDate.parse(date.subSequence(0, 10))
            .format(DateTimeFormatter.ofPattern("EEEE"))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDayAndMonth(date: String): String {
        return LocalDate.parse(date.subSequence(0, 10))
            .format(DateTimeFormatter.ofPattern("dd MMM"))
    }
}