package com.hellfish.evemento.event.poll

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellfish.evemento.NavigatorFragment
import com.hellfish.evemento.R

import kotlinx.android.synthetic.main.fragment_poll_list.*

class PollFragment : NavigatorFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_poll_list, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val polls = mutableListOf(Poll.Open("¿Comes carne?", listOf(Answer.Open("Sí", 2), Answer.Open("No", 1))),
                Poll.Closed("¿Llevas mascota?",
                        listOf(Answer.Closed("Sí", 3), Answer.Closed("No", 5))))

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = PollAdapter(polls)
        }
    }

}
