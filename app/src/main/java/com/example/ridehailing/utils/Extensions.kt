package com.example.ridehailing.utils

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//// View Extensions
//fun View.visible() {
//    visibility = View.VISIBLE
//}
//
//fun View.gone() {
//    visibility = View.GONE
//}
//
//fun View.invisible() {
//    visibility = View.INVISIBLE
//}
//
//// Context Extensions
//fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
//    Toast.makeText(this, message, duration).show()
//}
//
//// LiveData Extensions
//fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
//    observe(lifecycleOwner, object : Observer<T> {
//        override fun onChanged(value: T) {
//            observer.onChanged(value)
//            removeObserver(this)
//        }
//    })
//}
//
//// Number Extensions
//fun Double.formatAsCurrency(): String {
//    val formatter = DecimalFormat("$#,##0.00")
//    return formatter.format(this)
//}
//
//fun Double.formatAsDistance(): String {
//    return if (this < 1.0) {
//        "${(this * 1000).toInt()} m"
//    } else {
//        "${String.format("%.1f", this)} km"
//    }
//}
//
//// Date Extensions
//@RequiresApi(Build.VERSION_CODES.O)
//fun LocalDateTime.formatForDisplay(): String {
//    return this.format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a"))
//}
//
//@RequiresApi(Build.VERSION_CODES.O)
//fun LocalDateTime.formatTime(): String {
//    return this.format(DateTimeFormatter.ofPattern("hh:mm a"))
//}
//
//@RequiresApi(Build.VERSION_CODES.O)
//fun LocalDateTime.formatDate(): String {
//    return this.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
//}








// View Extensions
fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

// Context Extensions
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.showLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

// LiveData Extensions
fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(value: T) {
            observer.onChanged(value)
            removeObserver(this)
        }
    })
}

// Number Extensions for Nigerian Naira
fun Double.formatAsNairaCurrency(): String {
    val formatter = DecimalFormat("₦#,##0")
    return formatter.format(this)
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

fun Int.formatAsDuration(): String {
    val hours = this / 60
    val minutes = this % 60

    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        else -> "${minutes} min"
    }
}

// Date Extensions
@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.formatForDisplay(): String {
    return try {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            this.format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a"))
        } else {
            toString()
        }
    } catch (e: Exception) {
        toString()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.formatTime(): String {
    return try {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            this.format(DateTimeFormatter.ofPattern("hh:mm a"))
        } else {
            toString()
        }
    } catch (e: Exception) {
        toString()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.formatDate(): String {
    return try {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            this.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        } else {
            toString()
        }
    } catch (e: Exception) {
        toString()
    }
}

// String Extensions
fun String.capitalizeWords(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}

// Location Extensions
fun Double.toCoordinateString(): String {
    return String.format("%.6f", this)
}

// Nigerian location helpers
fun isNigerianCoordinate(latitude: Double, longitude: Double): Boolean {
    return latitude in 4.0..14.0 && longitude in 2.5..15.0
}

fun getNigerianCityFromCoordinates(latitude: Double, longitude: Double): String {
    return when {
        // Lagos coordinates
        latitude in 6.4..6.7 && longitude in 3.2..3.6 -> "Lagos"
        // Abuja coordinates
        latitude in 8.8..9.2 && longitude in 7.0..7.6 -> "Abuja"
        // Kano coordinates
        latitude in 11.8..12.2 && longitude in 8.3..8.7 -> "Kano"
        // Port Harcourt coordinates
        latitude in 4.6..5.0 && longitude in 6.8..7.2 -> "Port Harcourt"
        // Ibadan coordinates
        latitude in 7.2..7.6 && longitude in 3.7..4.1 -> "Ibadan"
        else -> "Nigeria"
    }
}
