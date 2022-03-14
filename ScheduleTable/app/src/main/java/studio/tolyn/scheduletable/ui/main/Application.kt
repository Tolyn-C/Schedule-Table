package studio.tolyn.scheduletable.ui.main

import android.annotation.SuppressLint
import android.app.Application
import java.text.SimpleDateFormat
import java.util.*

class Application : Application() {
    companion object {
        const val TIME_POINT_LIST = "timePoint"
        const val TIME_POINT = 1800000

        @SuppressLint("SimpleDateFormat")
        val GMT_FORMATTER = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

        @SuppressLint("SimpleDateFormat")
        val START_FORMATTER = SimpleDateFormat("yyyy-MM-dd")

        @SuppressLint("SimpleDateFormat")
        val END_FORMATTER = SimpleDateFormat("MM-dd")

        @SuppressLint("ConstantLocale")
        val TAB_FORMATTER = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())

        @SuppressLint("ConstantLocale")
        val TIME_FORMATTER = SimpleDateFormat("HH:mm", Locale.getDefault())

        @SuppressLint("ConstantLocale")
        val TIME_ZONE_FORMATTER = SimpleDateFormat("zzz", Locale.getDefault())
    }
}