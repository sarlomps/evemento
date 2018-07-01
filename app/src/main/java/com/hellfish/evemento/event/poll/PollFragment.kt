package com.hellfish.evemento.event.poll

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellfish.evemento.EventViewModel
import com.hellfish.evemento.NavigatorFragment
import com.hellfish.evemento.R
import com.hellfish.evemento.R.string.title_fragment_poll_list

import kotlinx.android.synthetic.main.fragment_poll_list.*

class PollFragment : NavigatorFragment() {

    override val titleId = title_fragment_poll_list

    lateinit var eventViewModel: EventViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)

        eventViewModel.loadPolls { polls, _ ->
            polls?.let { polls -> recyclerView.adapter = PollAdapter(polls.toMutableList(), { poll -> eventViewModel.edit(poll) }) }
            //showToast(R.string.errorLoadingComments)
        }
//        eventViewModel.polls.observe(this, Observer { polls ->
//            polls?.let { polls -> recyclerView.adapter = PollAdapter(polls, { poll -> eventViewModel.edit(poll) }) }
//        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_poll_list, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val polls = mutableListOf<Poll>()

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = PollAdapter(polls, { poll -> eventViewModel.edit(poll) })
        }
    }

}
