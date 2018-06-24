package com.hellfish.evemento
import com.hellfish.evemento.api.RestAPI
import com.hellfish.evemento.event.Event

object NetworkManager {
    
    private val api: RestAPI = RestAPI()

    fun getEventsForUser(user: String, callback: (List<Event>?, Int?) -> (Unit)) {
        api.getEventsForUser(user, callback)
    }
}