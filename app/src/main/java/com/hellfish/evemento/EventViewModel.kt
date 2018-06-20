package com.hellfish.evemento

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hellfish.evemento.event.Event

class EventViewModel : ViewModel() {

    var event: MutableLiveData<Event> = MutableLiveData()
        private set
    private var guests: List<String> = listOf()
    private var rides: List<String> = listOf()
    private var tasks: List<String> = listOf()
    private var polls: List<String> = listOf()
    private var comments: List<String> = listOf()

    fun loadEvent(event: Event) {
        select(event)
        guests = listOf() //TODO load it from Firebase
        rides = listOf() //TODO load it from Firebase
        tasks = listOf() //TODO load it from Firebase
        polls = listOf() //TODO load it from Firebase
        comments = listOf() //TODO load it from Firebase
    }

    fun selected() = event.value

    fun select(event: Event) { this.event.value = event }

    //TODO The idea is overload this mehtod with the real type (Guest / Poll / Task / Ride / Comment)
    fun add(element: String) {
        guests += element
    }

}

class InvalidEventElementException(override var message: String): Exception(message)
