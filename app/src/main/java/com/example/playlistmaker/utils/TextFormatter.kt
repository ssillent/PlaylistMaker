package com.example.playlistmaker.utils

fun Int.formatTracks(): String {
    return when {
        this % 10 == 1 && this % 100 != 11 -> "$this трек"
        this % 10 in 2..4 && (this % 100 < 10 || this % 100 > 20) -> "$this трека"
        else -> "$this треков"
    }
}

fun Long.formatMinutes(): String {
    val value = this.toInt()
    return when {
        value % 10 == 1 && value % 100 != 11 -> "$value минута"
        value % 10 in 2..4 && (value % 100 < 10 || value % 100 > 20) -> "$value минуты"
        else -> "$value минут"
    }
}