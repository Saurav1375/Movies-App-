package com.example.moviesapp.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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