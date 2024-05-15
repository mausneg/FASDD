package com.example.fasdd_android

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalTime
import java.util.Date

@Parcelize
data class Weather(
    val location: String,
    val temperature: String,
    val time: LocalTime,
    val type: String,
) : Parcelable