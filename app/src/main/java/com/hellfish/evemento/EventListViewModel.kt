package com.hellfish.evemento

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Build
import android.util.Log
import com.hellfish.evemento.event.Event

class EventListViewModel : ViewModel() {
    var events: MutableLiveData<MutableList<Event>> = MutableLiveData()
        private set

    fun refresh() { events.value = events.value?.sortedBy { it.startDate }?.toMutableList() }

    fun addEvent(event: Event) {
        events.value?.add(event)
        refresh()
    }
    fun updateEvent(event: Event, onError: (Int?) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            events.value?.removeIf { e -> e.eventId == event.eventId }
            events.value?.add(event)
            refresh()
        }
        else fetchEventsForCurrentUser(onError)
    }
    fun removeEvent(event: Event, onError: (Int?) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) events.value?.removeIf { e -> e.eventId == event.eventId }
        else fetchEventsForCurrentUser(onError)
    }

    fun fetchEventsForCurrentUser(errorCallback: (Int?) -> Unit) {
        NetworkManager.getEventsForUser(SessionManager.getCurrentUser()!!.userId) { userEvents, errorMessage ->
            userEvents?.let {
                Log.d("getEventsForUser", it.toString())
                events.value = it.sortedBy { it.startDate }.toMutableList()
                fetchInvitations(errorCallback)
                return@getEventsForUser
            }
            errorCallback(errorMessage ?: R.string.api_error_fetching_data)
        }
    }

    private fun fetchInvitations(errorCallback: (Int?) -> Unit) {
        NetworkManager.getInvitations(SessionManager.getCurrentUser()!!.userId) { invitations, errorMessage ->
            if (invitations != null) {
                invitations.forEach {
                    NetworkManager.getEvent(it) { event, errorMessage ->
                        if (event != null) addEvent(event)
                        else errorCallback(errorMessage ?: R.string.api_error_fetching_data)
                    }
                }
            }
            else errorCallback(errorMessage ?: R.string.api_error_fetching_data)
        }
    }

}