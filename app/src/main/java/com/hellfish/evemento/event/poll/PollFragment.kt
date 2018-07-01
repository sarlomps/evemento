package com.hellfish.evemento.event.poll

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellfish.evemento.EventViewModel
import com.hellfish.evemento.NavigatorFragment
import com.hellfish.evemento.NetworkManager
import com.hellfish.evemento.R
import com.hellfish.evemento.R.string.title_fragment_poll_list
import com.hellfish.evemento.lib.Either
import android.arch.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_new_poll.*

import kotlinx.android.synthetic.main.fragment_poll_list.*

class PollFragment : NavigatorFragment() {

    override val titleId = title_fragment_poll_list

    lateinit var eventViewModel: EventViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)

        eventViewModel.loadPolls { showToast(it) }

        eventViewModel.polls.observe(this, Observer { it?.let { setNewPollAdapter(it) } })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_poll_list, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)
        setNewPollAdapter(mutableListOf())
        pollListFab.setOnClickListener { navigatorListener.replaceFragment(NewPollFragment()) }

    }

    private fun setNewPollAdapter(polls: Iterable<Poll>) {
        recyclerView.adapter = PollAdapter(polls.toMutableList(), { poll ->
            eventViewModel.edit(poll)
            NetworkManager.updatePoll(poll, { _,_ -> })
        })
    }
}
