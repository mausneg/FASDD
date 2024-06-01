package com.example.fasdd_android

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Notif(
    val id: String,
    val title: String,
    val dateTime: LocalDateTime,
    val message: String,
    val alread_read: Boolean,
    ): Parcelable
