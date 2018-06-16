package com.hellfish.evemento.event

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event(val title: String,
                 val description: String,
                 val startDate: String,
                 val endDate: String,
                 val location: String) : Parcelable