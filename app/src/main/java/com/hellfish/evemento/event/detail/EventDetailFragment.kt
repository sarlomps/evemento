package com.hellfish.evemento.event.detail

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.res.ResourcesCompat
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
import kotlinx.android.synthetic.main.fragment_event_detail.*

class EventDetailFragment : NavigatorFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return EventDetailLayout(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val event = arguments?.getParcelable<Event>("event")
        if (event != null) viewingEvent(event, view as EventDetailLayout)
    }

    override fun setupToolbar() {
        listener.setCustomToolbar(eventDetailToolbar, false)
        eventDetailAppbar.setExpanded(true)
    }

    private fun viewingEvent(event: Event, view: EventDetailLayout) {
        view.loadEvent(event)
        taskElement.setOnClickListener { listener.replaceFragment(TaskListFragment()) }
        pollElement.setOnClickListener { listener.replaceFragment(PollFragment()) }

        eventDetailFab.withDrawable(R.drawable.ic_edit_white_24dp).setOnClickListener { editingEvent(event, view) }
    }

    private fun editingEvent(event: Event, view: EventDetailLayout) {
        view.loadEvent(event)

        eventDetailFab.withDrawable(R.drawable.ic_check_white_24dp).setOnClickListener { viewingEvent(event, view) }
    }

    private fun FloatingActionButton.withDrawable(drawableId: Int): FloatingActionButton {
        setImageDrawable(ResourcesCompat.getDrawable(resources, drawableId, null))
        return this
    }


}

