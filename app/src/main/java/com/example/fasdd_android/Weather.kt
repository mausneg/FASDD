package com.example.fasdd_android

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Weather(
    val id: Int,
    val location: String,
    val temperature: Int,
    val type: String,
) : Parcelable