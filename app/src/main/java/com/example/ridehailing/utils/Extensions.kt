package com.example.ridehailing.utils

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun Double.formatAsCurrency(): String {
    val formatter = DecimalFormat("₦#,##0.00")
    return formatter.format(this)
}

fun Double.formatAsDistance(): String {
    return if (this < 1.0) {
        "${(this * 1000).toInt()} m"
    } else {
        "${String.format("%.1f", this)} km"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.formatForDisplay(): String {
    return try {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            this.format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a"))
        } else {
            toString()
        }
    } catch (e: Exception) {
        toString()
    }
}

