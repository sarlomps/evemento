package com.hellfish.evemento.event.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellfish.evemento.NavigatorFragment
import com.hellfish.evemento.R
import com.hellfish.evemento.event.Event
import com.hellfish.evemento.event.poll.PollFragment
import com.hellfish.evemento.event.task.TaskListFragment
import kotlinx.android.synthetic.main.event_detail_elements.*
import kotlinx.android.synthetic.main.event_detail_tool_bar.*

class EventDetailFragment : NavigatorFragment() {

    private lateinit var selectedEvent: Event

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        selectedEvent = arguments?.getParcelable("event")!!
        return EventDetailLayout(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view as EventDetailLayout).loadEvent(selectedEvent)
        taskElement.setOnClickListener { listener.replaceFragment(TaskListFragment()) }
        pollElement.setOnClickListener { listener.replaceFragment(PollFragment()) }
    }

    override fun setupToolbar() {
        listener.setCustomToolbar(eventDetailToolbar, false)
        eventDetailAppbar.setExpanded(true)
        eventTitle.text = getString(R.string.default_event_title)
    }

}

