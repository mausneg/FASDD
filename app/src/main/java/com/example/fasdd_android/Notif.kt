package com.example.fasdd_android

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Notif(
    val id: String,
    val type: String,
    val dateTime: LocalDateTime,
    var already_read: Boolean,
    ): Parcelable
