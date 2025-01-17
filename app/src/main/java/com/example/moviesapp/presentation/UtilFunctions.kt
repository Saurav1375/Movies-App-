package com.example.moviesapp.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object TimeAgoFormatter {
    private const val SECOND_MILLIS = 1000L
    private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private const val DAY_MILLIS = 24 * HOUR_MILLIS
    private const val WEEK_MILLIS = 7 * DAY_MILLIS
    private const val MONTH_MILLIS = 30L * DAY_MILLIS
    private const val YEAR_MILLIS = 365L * DAY_MILLIS

    fun getTimeAgo(timestamp: Long): String {
        val now = System.currentTimeMillis()
        if (timestamp > now || timestamp <= 0) {
            return "just now"
        }

        val diff = now - timestamp
        return when {
            diff < MINUTE_MILLIS -> "just now"
            diff < 2 * MINUTE_MILLIS -> "1m ago"
            diff < 60 * MINUTE_MILLIS -> "${diff / MINUTE_MILLIS}m ago"
            diff < 2 * HOUR_MILLIS -> "1h ago"
            diff < 24 * HOUR_MILLIS -> "${diff / HOUR_MILLIS}h ago"
            diff < 48 * HOUR_MILLIS -> "1 day ago"
            diff < 7 * DAY_MILLIS -> "${diff / DAY_MILLIS} days ago"
            diff < 2 * WEEK_MILLIS -> "1 week ago"
            diff < 4 * WEEK_MILLIS -> "${diff / WEEK_MILLIS} weeks ago"
            diff < 2 * MONTH_MILLIS -> "1 month ago"
            diff < 12 * MONTH_MILLIS -> "${diff / MONTH_MILLIS} months ago"
            diff < 2 * YEAR_MILLIS -> "1 year ago"
            else -> "${diff / YEAR_MILLIS} years ago"
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
fun getYearFromReleaseDate(releaseDate: String, format : String = "MMM yyyy"): String {

    try {
        val date = LocalDate.parse(releaseDate)
        val formatter = DateTimeFormatter.ofPattern(format)
        return date.format(formatter)
    } catch (e: Exception) {
        e.printStackTrace()
        return ""

    }

}

fun getRuntimeFromMinutes(minutes: Int): String {
    val hours = minutes / 60
    val remainingMinutes = minutes % 60
    if (hours == 0) {
        return "${remainingMinutes}m"
    }
    return "${hours}h ${remainingMinutes}m"

}
fun getLanguageName(languageCode: String): String {
    val locale = Locale(languageCode)
    return locale.displayLanguage // Returns the language name in the default locale
}