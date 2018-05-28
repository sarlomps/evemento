package com.hellfish.evemento.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellfish.evemento.NavigatorFragment
import com.hellfish.evemento.R
import com.hellfish.evemento.event.poll.PollFragment
import com.hellfish.evemento.event.task.TaskListFragment
import kotlinx.android.synthetic.main.event_detail_elements.*
import kotlinx.android.synthetic.main.event_detail_tool_bar.*

class EventDetailFragment : NavigatorFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_event_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskElement.setOnClickListener { listener.replaceFragment(TaskListFragment()) }
        pollElement.setOnClickListener { listener.replaceFragment(PollFragment()) }
    }

    override fun setupToolbar() {
        listener.setCustomToolbar(eventDetailToolbar, false)
        eventTitle.text = getString(R.string.default_event_title)
    }

}

