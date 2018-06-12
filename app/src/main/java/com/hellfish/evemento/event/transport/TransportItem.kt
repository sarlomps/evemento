package com.hellfish.evemento.event.transport

data class TransportItem(val driver: UserMiniDetail, val passangers: List<UserMiniDetail>, val totalSlots: Int) {
    fun driverName(): String = driver.nickname
    fun availableSlots(): Int = totalSlots - passangers.size
}

data class UserMiniDetail(val nickname: String, val avatar: String)
