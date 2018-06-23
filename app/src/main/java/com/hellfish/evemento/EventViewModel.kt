package com.hellfish.evemento

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hellfish.evemento.event.Event
import com.hellfish.evemento.event.transport.TransportItem
import com.hellfish.evemento.event.transport.UserMiniDetail

class EventViewModel : ViewModel() {

    var selectedEvent: MutableLiveData<Event> = MutableLiveData()
        private set
    private var guests: List<String> = listOf()
    private var rides: List<TransportItem> = mockedRides()
    private var tasks: List<String> = listOf()
    private var polls: List<String> = listOf()
    private var comments: List<String> = listOf()

    fun loadEvent(event: Event) {
        select(event)
        guests = listOf() //TODO load it from Firebase
        rides = mockedRides() //TODO load it from Firebase
        tasks = listOf() //TODO load it from Firebase
        polls = listOf() //TODO load it from Firebase
        comments = listOf() //TODO load it from Firebase
    }

    fun updateView() = select(selectedEvent.value)

    fun selected() = selectedEvent.value

    fun select(event: Event?) { selectedEvent.value = event }

    //TODO The idea is overload this mehtod with the real type (Guest / Poll / Task / Ride / Comment)
    fun add(element: String) {
        guests += element
    }

    fun mockedRides(): List<TransportItem> {
        val transports = ArrayList<TransportItem>()
        val driver1 = UserMiniDetail("Gus", "Sarlanga")
        val driver2 = UserMiniDetail("Gas", "Sarlanga")
        val pass_1_1 = UserMiniDetail("juan", "Sarlanga")
        val pass_1_2 = UserMiniDetail("juan", "Sarlanga")
        val pass_2_1 = UserMiniDetail("Nico", "Sarlanga")
        val pass_2_2 = UserMiniDetail("Nico", "Sarlanga")
        transports.add(TransportItem(driver1, listOf(pass_1_1, pass_1_2), 4))
        transports.add(TransportItem(driver2, listOf(pass_2_1, pass_2_2), 3))
        return transports
    }

}

class InvalidEventElementException(override var message: String): Exception(message)
