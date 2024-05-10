package com.example.fasdd_android

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.Duration
import java.time.Period

@Parcelize
data class News(
    var title: String,
    var content: String,
    var imageLink: String,
    var dateTime: LocalDateTime,
    var excerpt: String
) : Parcelable