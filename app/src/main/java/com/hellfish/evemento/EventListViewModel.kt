package com.hellfish.evemento

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import com.hellfish.evemento.event.Event

class EventListViewModel : ViewModel() {
    var events: MutableLiveData<MutableList<Event>> = MutableLiveData()
        private set

    fun refresh() { events.value = events.value }

    fun addEvent(event: Event) { events.value?.add(event) }

    @RequiresApi(Build.VERSION_CODES.N)
    fun removeEvent(event: Event) { events.value?.removeIf { e -> e.eventId == event.eventId } }

    fun fetchEventsForCurrentUser(errorCallback: (Int?) -> (Unit)) {
        NetworkManager.getEventsForUser(SessionManager.getCurrentUser()!!.userId) { userEvents, errorMessage ->
            userEvents?.let {
                Log.d("getEventsForUser", it.toString())
                events.value = it.toMutableList()
                errorCallback(null)
                return@getEventsForUser
            }
            errorCallback(errorMessage ?: R.string.api_error_fetching_data)
        }
    }

}