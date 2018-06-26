package com.hellfish.evemento.event.transport

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransportItem(val driver: UserMiniDetail, val passangers: ArrayList<UserMiniDetail>, val startpoint: String,val totalSlots: Int) : Parcelable {
    fun sameTransport(transportItem: TransportItem): Boolean = driver.sameUser(transportItem.driver)
    fun driverName(): String = driver.nickname
    fun availableSlots(): Int = maxOf(totalSlots - passangers.size, 0)
    fun isFull(): Boolean = totalSlots.equals(passangers.size)
    fun isAlreadyInTransport(passanger: UserMiniDetail): Boolean = passangers.any { passanger.sameUser(it)} || passanger.sameUser(driver)
}

@Parcelize
data class UserMiniDetail(val nickname: String, val avatar: String) : Parcelable {
    fun sameUser(user: UserMiniDetail): Boolean = nickname.equals(user.nickname)
}
