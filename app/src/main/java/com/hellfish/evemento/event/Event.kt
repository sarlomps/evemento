package com.hellfish.evemento.event

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime

@Parcelize
data class Event(val title: String,
                 val description: String,
                 val startDate: DateTime,
                 val endDate: DateTime,
                 val location: String) : Parcelable