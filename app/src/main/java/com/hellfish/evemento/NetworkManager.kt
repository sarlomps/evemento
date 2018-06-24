package com.hellfish.evemento
import com.hellfish.evemento.api.EventMapper
import com.hellfish.evemento.api.RestAPI
import com.hellfish.evemento.event.Event

object NetworkManager {
    
    private val api: RestAPI = RestAPI()

    fun getEventsForUser(user: String, callback: (List<Event>?, Int?) -> (Unit)) {
        api.getEventsForUser(user, callback)
    }

    fun pushEvent(event:Event, callback: (String?, Int?) -> (Unit)) {
        api.pushEvent(EventMapper().mapToEntity(event), callback)
    }

    // EJEMPLO DE COMO SE USA:
    // PUSHEA AL SERVER Y RETORNA EL NUEVO ID DEL EVENTO QUE LE OTORGO FIREBASE
//    NetworkManager.pushEvent(event) { name, errorMessage ->
//        name?.let {
//            Log.d("pushEvent, new id:", it)
//            event.eventId = it
//            return@pushEvent
//        }
//        showSnackbar(errorMessage ?: R.string.network_unknown_error, main_container)
//    }


}