package com.hellfish.evemento.event.poll

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellfish.evemento.EventViewModel
import com.hellfish.evemento.NavigatorFragment
import com.hellfish.evemento.NetworkManager
import com.hellfish.evemento.R
import com.hellfish.evemento.R.string.title_fragment_new_poll

import kotlinx.android.synthetic.main.fragment_new_poll.*

class NewPollFragment : NavigatorFragment() {

    override val titleId = title_fragment_new_poll

    lateinit var eventViewModel: EventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_new_poll, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        newPollFab.setOnClickListener { _ ->
            val newPoll = Poll.Votable(question=editPollQuestion.text.toString(),
                                       answers=listOf(Answer.Open(editPollAnswer1.text.toString(), listOf()),
                                                      Answer.Open(editPollAnswer2.text.toString(), listOf())),
                                       eventId=eventViewModel.selectedEvent.value!!.eventId,
                                       pollId="")
            NetworkManager.pushPoll(newPoll, { pollId,_ -> Log.d("POLLID", pollId); pollId?.let { eventViewModel.add(newPoll.setId(it)) } })
        }
    }
}
