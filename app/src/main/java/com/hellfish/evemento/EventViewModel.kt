package com.hellfish.evemento

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
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

    fun loadRides(callback: (List<TransportItem>?, Int?) -> (Unit)) {
//TODO: IMPLEMENT
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
        rides.value = mockedRides()
        tasks.value = mutableListOf() //TODO load it from Firebase
    }

    private fun mockedRides(): MutableList<TransportItem> {
        val transports = ArrayList<TransportItem>()
        val driver1 = User("gusId","Gus", "Sarlanga", "")
        val gusPlace: Location = Location("casa de gus", Coordinates(-34.588938999999996,-58.5906728))
        val driver2 = User("gasId","Gas", "Sarlanga", "")
        val gasPlace: Location = Location("casa de gas", Coordinates(-34.6017308,-58.586593900000004))
        val pass_1_1 = User("juanRId","juanR", "Sarlanga", "")
        val pass_1_2 = User("juanDsId","juanDs", "Sarlanga", "")
        val pass_2_1 = User("juanBId","NicoB", "Sarlanga", "")
        val pass_2_2 = User("NicoS","NicoS", "Sarlanga", "")
        val passangers1 = ArrayList<User>()
        val passangers2 = ArrayList<User>()
        passangers1.add(pass_1_1)
        passangers1.add(pass_1_2)
        passangers2.add(pass_2_1)
        passangers2.add(pass_2_2)

        transports.add(TransportItem("TransportID1", driver1, passangers1, gusPlace ,4))
        transports.add(TransportItem("TransportID2",driver2, passangers2, gasPlace,3))
        return transports
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

    fun edit(newTransport: TransportItem) {
        rides.value = editList(rides.value, newTransport) { t1, t2 -> t1.sameTransport(t2) }
    }

    fun add(transportItem: TransportItem) {
        rides.value = rides.value?.plus(transportItem)?.toMutableList()
    }

    fun remove(transportItem: TransportItem) {
        rides.value = rides.value?.minus(transportItem)?.toMutableList()

    }
}

class InvalidEventElementException(override var message: String): Exception(message)
