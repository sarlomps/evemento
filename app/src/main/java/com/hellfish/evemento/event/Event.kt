package com.hellfish.evemento.event

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime

@Parcelize
data class Event(var eventId:String,
                 var title: String,
                 var description: String,
                 var startDate: DateTime,
                 var endDate: DateTime,
                 var location: String,
                 var user: String ) : Parcelable
