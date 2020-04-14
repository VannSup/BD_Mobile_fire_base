package com.example.bd_mobile

import java.sql.Date
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun convertTimestampToDate(time: Long): String {
    val timestamp = Timestamp(time)
    val date = Date(timestamp.time)
    val pattern = "dd/MM/yyyy HH:mm:ss"
    val df: DateFormat = SimpleDateFormat(pattern, Locale.FRANCE)
    return df.format(date)
}