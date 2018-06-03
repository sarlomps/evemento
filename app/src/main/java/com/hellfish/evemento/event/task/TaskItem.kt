package com.hellfish.evemento.event.task

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TaskItem(val description: String, val owner: String) : Parcelable