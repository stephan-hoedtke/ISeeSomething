package com.stho.isee.utilities

import java.text.SimpleDateFormat
import java.util.*

object Formatter {

    private val df = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)
    fun toString(calendar: Calendar): String =
        df.format(calendar.time)
}

fun Calendar.toDateTimeString() =
    Formatter.toString(this)

