package com.hellfish.evemento

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hellfish.evemento.event.Event
import com.hellfish.evemento.event.transport.TransportItem
import com.hellfish.evemento.event.transport.UserMiniDetail
import com.hellfish.evemento.event.poll.Poll
import com.hellfish.evemento.event.poll.Answer


class EventViewModel : ViewModel() {

    var selectedEvent: MutableLiveData<Event> = MutableLiveData()
        private set
    private var guests: List<String> = listOf()
    var rides: MutableLiveData<MutableList<TransportItem>> = MutableLiveData()
        private set
    private var tasks: List<String> = listOf()
    private var polls: MutableLiveData<MutableList<Poll>> = MutableLiveData()
    private var comments: List<String> = listOf()
    fun getPolls(): MutableLiveData<MutableList<Poll>> {
        if (polls.value?.isEmpty() ?: true) {
            polls.value = mutableListOf(
                    Poll.NoVotable("Asdasdesd", listOf(Answer.Closed("Sí", 2), Answer.Closed("No", 1))),
                    Poll.Votable("Asdasdesdo", listOf(Answer.Open("Sí", 2), Answer.Open("No", 1)))
            )
            }
        return polls
    }

    fun loadEvent(event: Event) {
        select(event)
        loadDataFrom(event)
    }

    fun updateView() = select(selectedEvent.value)

    fun selected() = selectedEvent.value

    fun select(event: Event?) {
        loadDataFrom(event)
        selectedEvent.value = event
    }

    //TODO The idea is overload this mehtod with the real type (Guest / Poll / Task / Ride / Comment)
    fun add(element: String) {
        guests += element
    }

    /**
     *The idea is this methos is to called the mocked methods and then replace those methods to pull from server
     */
    private fun loadDataFrom(event: Event?) {
        rides.value = mockedRides()
        guests = listOf() //TODO load it from Firebase
        tasks = listOf() //TODO load it from Firebase
        polls.value = mutableListOf(
                Poll.NoVotable("Asdasdesd", listOf(Answer.Closed("Sí", 2), Answer.Closed("No", 1))),
                Poll.Votable("Asdasdesdo", listOf(Answer.Open("Sí", 2), Answer.Open("No", 1)))
        )
        comments = listOf() //TODO load it from Firebase
    }

    private fun mockedRides(): MutableList<TransportItem> {
        val transports = ArrayList<TransportItem>()
        val driver1 = UserMiniDetail("Gus", "Sarlanga")
        val driver2 = UserMiniDetail("Gas", "Sarlanga")
        val pass_1_1 = UserMiniDetail("juanR", "Sarlanga")
        val pass_1_2 = UserMiniDetail("juanDs", "Sarlanga")
        val pass_2_1 = UserMiniDetail("NicoB", "Sarlanga")
        val pass_2_2 = UserMiniDetail("NicoS", "Sarlanga")
        val passangers1 = ArrayList<UserMiniDetail>()
        val passangers2 = ArrayList<UserMiniDetail>()
        passangers1.add(pass_1_1)
        passangers1.add(pass_1_2)
        passangers2.add(pass_2_1)
        passangers2.add(pass_2_2)

        transports.add(TransportItem(driver1, passangers1, "casa de gus" ,4))
        transports.add(TransportItem(driver2, passangers2, "casa de gas",3))
        return transports
    }

    fun add(poll: Poll) {
        polls.value = polls.value?.plus(poll)?.toMutableList()
    }

    fun edit(newPoll: Poll) {
        polls.value = polls.value?.map { poll -> if (newPoll.question == poll.question) { newPoll } else { poll } }?.toMutableList()
    }

    fun edit(newTransport: TransportItem) {
        rides.value = rides.value?.map { transport ->
            if (newTransport.sameTransport(transport)) { newTransport }
            else { transport }
        }?.toMutableList()
    }

    fun add(transportItem: TransportItem) {
        rides.value = rides.value?.plus(transportItem)?.toMutableList()
    }

    fun remove(transportItem: TransportItem) {
        rides.value = rides.value?.minus(transportItem)?.toMutableList()

    }
}

class InvalidEventElementException(override var message: String): Exception(message)
