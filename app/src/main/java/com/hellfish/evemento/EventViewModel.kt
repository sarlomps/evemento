package com.hellfish.evemento

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.hellfish.evemento.api.Comment
import com.hellfish.evemento.api.Guest
import com.hellfish.evemento.api.User
import com.hellfish.evemento.event.Event
import com.hellfish.evemento.event.transport.TransportItem
import com.hellfish.evemento.event.poll.Poll
import com.hellfish.evemento.event.task.TaskItem
import com.hellfish.evemento.event.transport.Coordinates
import com.hellfish.evemento.event.transport.Location


class EventViewModel : ViewModel() {

    var selectedEvent: MutableLiveData<Event> = MutableLiveData()
        private set
    var guests: MutableLiveData<MutableList<Guest>> = MutableLiveData()
        private set
    var rides: MutableLiveData<MutableList<TransportItem>> = MutableLiveData()
        private set
    var tasks: MutableLiveData<MutableList<TaskItem>> = MutableLiveData()
    var polls: MutableLiveData<MutableList<Poll>> = MutableLiveData()
    var comments: MutableLiveData<MutableList<Comment>> = MutableLiveData()
        private set

    fun updateView() = select(selectedEvent.value)

    fun selected() = selectedEvent.value

    fun select(event: Event?) {
        selectedEvent.value = event
        comments.value = mutableListOf()
        guests.value = mutableListOf()
        rides.value = mutableListOf()
        polls.value = mutableListOf()
        loadDataFrom(event)     /// TODO: BORRAR AL TERMINAR REFACTOR DE SERVICIOS
    }

    fun loadPolls(onError: (Int) -> (Unit)) {
        selectedEvent.value?.let {
            NetworkManager.getPolls(it) { newPolls, errorMessage ->
                if (newPolls == null) { errorMessage?.let { onError(it) } } else {
                    val thereAreNewPolls = polls.value?.map{it.pollId} != newPolls.map {it.pollId}
                    if(thereAreNewPolls) { polls.value = newPolls.toMutableList() }
                }
            }
        }
        if(selectedEvent.value == null) { onError(R.string.api_error_fetching_data) }
    }

    fun loadRides(onError: (Int?) -> (Unit)) {
        selectedEvent.value?.let {
            NetworkManager.getAllUsers { users, errorMessage ->
                users?.let {
                    NetworkManager.getTransports(selected()!!) { newRides, errorMessage ->
                        newRides?.let {
                            try {
                                val fullTransports = it.map { transport ->
                                    val fullPassengers = transport.passangers.map { passenger ->
                                        val user = users.find { it.userId == passenger.userId }
                                        if (user != null) passenger.fillWith(user)
                                        else null
                                    }
                                    val fullDriver = users.find { it.userId == transport.driver.userId }
                                    if (fullDriver != null) transport.copy(driver = fullDriver,
                                            passangers = fullPassengers.requireNoNulls().toCollection(ArrayList()))
                                    else throw IllegalArgumentException("Null driver")
                                }
                                rides.value = fullTransports.toMutableList()
                            } catch (e: IllegalArgumentException) { onError(R.string.api_error_fetching_data) }
                            return@getTransports
                        }
                        onError(errorMessage)
                    }
                    return@getAllUsers
                }
                onError(errorMessage)
            }
            return
        }
        onError(R.string.api_error_fetching_data)
    }


    fun loadComments(onError: (Int?) -> (Unit)) {
        selectedEvent.value?.let {
            NetworkManager.getComments(it) { newComments, errorMessage ->
                newComments?.let { comments.value = it.toMutableList(); return@getComments }
                onError(errorMessage)
            }
            return
        }
        onError(R.string.api_error_fetching_data)
    }

    fun loadGuests(onError: (Int?) -> (Unit)) {
        selectedEvent.value?.let { event ->
            NetworkManager.getAllUsers { users, errorMessage ->
                users?.let {
                    NetworkManager.getGuests(event) { newGuests, errorMessage ->
                        newGuests?.let {
                            try {
                                val fullGuests = it.map { guest ->
                                    val user = users.find { it.userId == guest.userId }
                                    if (user != null)
                                        guest.copy(
                                                displayName = user.displayName,
                                                imageUrl = user.imageUrl,
                                                email = user.email)
                                    else null
                                }
                                guests.value = fullGuests.requireNoNulls().toMutableList()
                            } catch (e: IllegalArgumentException) { onError(R.string.api_error_fetching_data) }
                            return@getGuests
                        }
                        onError(errorMessage)
                    }
                    return@getAllUsers
                }
                onError(errorMessage)
            }
            return
        }
        onError(R.string.api_error_fetching_data)
    }

    fun loadTasks(callback: (List<TaskItem>?, Int?) -> (Unit)) {
//TODO: IMPLEMENT
    }
    /**
     *The idea is this methos is to called the mocked methods and then replace those methods to pull from server
     */
    /// TODO: BORRAR AL TERMINAR REFACTOR DE SERVICIOS
    private fun loadDataFrom(event: Event?) {
        tasks.value = mutableListOf() //TODO load it from Firebase
    }

    fun <T> editList(list: MutableList<T>?, newT: T, comparator: (T, T) -> Boolean) =
            list?.map { oldT -> if (comparator(oldT, newT)) { newT } else { oldT } }?.toMutableList()

    fun add(comment: Comment) {
        comments.value = comments.value?.plus(comment)?.toMutableList()
    }

    fun remove(comment: Comment) {
        comments.value = comments.value?.minus(comment)?.toMutableList()
    }

    fun edit(newComment: Comment) {
        comments.value = editList(comments.value, newComment) { c1, c2 -> c1.commentId == c2.commentId}
    }

    fun add(guest: Guest) {
        guests.value = guests.value?.plus(guest)?.toMutableList()
    }

    fun remove(guest: Guest) {
        guests.value = guests.value?.minus(guest)?.toMutableList()
    }

    fun add(poll: Poll) {
        polls.value = polls.value?.plus(poll)?.toMutableList()
    }

    fun edit(newPoll: Poll) {
        polls.value = editList(polls.value, newPoll) { p1, p2 -> p1.question == p2.question }
    }

    fun edit(newTransport: TransportItem, callback: (TransportItem?, Int?) -> Unit) {
        NetworkManager.updateTransport(selected()!!.eventId, newTransport) { transport, errorMessage ->
            transport?.let {
                NetworkManager.getAllUsers { users, usersErrorMessage->
                    try {
                        users?.let {
                            val fullPassengers = it.map { passenger ->
                                val user = users.find { it.userId == passenger.userId }
                                if (user != null) passenger.fillWith(user)
                                else null
                            }
                            val fullTransport = transport.copy(passangers = fullPassengers.requireNoNulls().toCollection(ArrayList()))
                            rides.value = editList(rides.value, fullTransport) { t1, t2 -> t1.sameTransport(t2) }
                            callback(transport, null)
                        }
                        return@getAllUsers
                    } catch (e: IllegalArgumentException) { callback(null, R.string.api_error_fetching_data) }
                    callback(null, usersErrorMessage)
                }
                return@updateTransport
            }
            callback(null, errorMessage)
        }
    }

    fun add(transportItem: TransportItem) {
        NetworkManager.pushTransport(selected()!!.eventId, transportItem) { id, errorMessage ->
            id?.let { rides.value = rides.value?.plus(transportItem.copy(transportId = id))?.toMutableList(); return@pushTransport }
            Log.e("TOAST", "Esto debería ser un errorMessage") //TODO Sacarlo a un Toast
        }
    }

    fun remove(transportItem: TransportItem) {
        NetworkManager.deleteTransport(transportItem) { success, errorMessage ->
            if (success) { rides.value = rides.value?.minus(transportItem)?.toMutableList(); return@deleteTransport }
            Log.e("TOAST", "Esto debería ser un errorMessage") //TODO Sacarlo a un Toast
        }
    }

}

class InvalidEventElementException(override var message: String): Exception(message)
