package com.sarlomps.evemento.event.transport

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.sarlomps.evemento.api.User
import kotlinx.android.parcel.Parcelize

data class TransportItem(val transportId: String, val driver: User, val passangers: ArrayList<User>, val startpoint: Location,val totalSlots: Int) {
    fun sameTransport(transportItem: TransportItem): Boolean = driver.sameUser(transportItem.driver)
    fun driverName(): String = driver.displayName
    fun availableSlots(): Int = maxOf(totalSlots - passangers.size, 0)
    fun isFull(): Boolean = totalSlots.equals(passangers.size)
    fun isAlreadyInTransport(passanger: User): Boolean = passangers.any { passanger.sameUser(it)} || passanger.sameUser(driver)
    fun latLong(): LatLng = startpoint.latLng()
}

data class Location(val name: String, val coordinates: Coordinates) {
    fun latLng(): LatLng {
        return LatLng(coordinates.latitude, coordinates.longitude)
    }
}

data class Coordinates(val latitude: Double, val longitude: Double) {
    constructor(latLng: LatLng) : this(latLng.latitude, latLng.longitude)
}

