package com.hellfish.evemento

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.hellfish.evemento.event.Event

class EventListViewModel : ViewModel() {
    var events: MutableLiveData<List<Event>> = MutableLiveData()
        private set

    fun fetchEventsForCurrentUser(errorCallback: (Int?) -> (Unit)) {
        NetworkManager.getEventsForUser(SessionManager.currentUser!!.uid) { userEvents, errorMessage ->
            userEvents?.let {
                Log.d("getEventsForUser", it.toString())
                events.value = it
                errorCallback(null)
                return@getEventsForUser
            }
            errorCallback(errorMessage ?: R.string.api_error_fetching_data)
        }

    }

}