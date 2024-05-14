package com.example.fasdd_android

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class News(
    val title: String,
    val content: String,
    val image: String,
    val dateTime: LocalDateTime,
    val excerpt: String,
) : Parcelable