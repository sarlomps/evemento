package com.hellfish.evemento.event

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event(val title: String,
                 val description: String,
                 val time: EventTime,
                 val location: String,
                 val guests: List<String> = listOf(),
                 val rides: List<String> = listOf(),
                 val tasks: List<String> = listOf(),
                 val polls: List<String> = listOf(),
                 val comments: List<String> = listOf()) : Parcelable

@Parcelize
data class EventTime(val startDate: String,
                     val endDate: String): Parcelable