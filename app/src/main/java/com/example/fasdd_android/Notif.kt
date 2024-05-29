package com.example.fasdd_android

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Notif(
    val title: String,
    val dateTime: LocalDateTime,
    val excerpt: String,
    ): Parcelable
