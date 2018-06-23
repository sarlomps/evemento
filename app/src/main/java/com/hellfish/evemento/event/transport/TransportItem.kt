package com.hellfish.evemento.event.transport

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransportItem(val driver: UserMiniDetail, val passangers: List<UserMiniDetail>, val totalSlots: Int) : Parcelable {
    fun driverName(): String = driver.nickname
    fun availableSlots(): Int = totalSlots - passangers.size
}

@Parcelize
data class UserMiniDetail(val nickname: String, val avatar: String) : Parcelable
