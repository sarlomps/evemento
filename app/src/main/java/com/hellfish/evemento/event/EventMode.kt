package com.hellfish.evemento.event

import android.support.design.widget.FloatingActionButton
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import com.hellfish.evemento.Navigator
import com.hellfish.evemento.R
import com.hellfish.evemento.event.poll.PollFragment
import com.hellfish.evemento.event.task.TaskListFragment
import com.hellfish.evemento.event.transport.TransportFragment
import kotlinx.android.synthetic.main.event_elements.view.*
import kotlinx.android.synthetic.main.fragment_event.view.*

interface EventMode {

    var navigatorListener: Navigator
    var event: Event

    fun editingEvent(view: EventLayout): Unit
    fun viewingEvent(view: EventLayout): Unit

    fun FloatingActionButton.withDrawable(drawableId: Int): FloatingActionButton =
            apply { setImageDrawable(ResourcesCompat.getDrawable(resources, drawableId, null)) }

}

interface ViewMode : EventMode {

    override fun viewingEvent(view: EventLayout): Unit = view.run {
        load(event)
        notEditableElementsVisibility(View.VISIBLE)
        changeTextColor(R.color.text)
        enabledEditableElements(false)

        taskElement.setOnClickListener { navigatorListener.replaceFragment(TaskListFragment()) }
        pollElement.setOnClickListener { navigatorListener.replaceFragment(PollFragment()) }
        rideElement.setOnClickListener { navigatorListener.replaceFragment(TransportFragment()) }

        eventFab.withDrawable(R.drawable.ic_edit_white_24dp).setOnClickListener { editingEvent(view) }
    }

}

interface EditMode : EventMode {

    override fun editingEvent(view: EventLayout): Unit = view.run {
        load(event)
        notEditableElementsVisibility(View.GONE)
        changeTextColor(R.color.grey)
        enabledEditableElements(true)

        eventFab.withDrawable(R.drawable.ic_check_white_24dp).setOnClickListener {
            event = view.edit(event)
            viewingEvent(view)
        }
    }

}