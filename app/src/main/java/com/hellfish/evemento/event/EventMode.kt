package com.hellfish.evemento.event

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.SupportMapFragment
import com.hellfish.evemento.Navigator
import com.hellfish.evemento.R
import com.hellfish.evemento.event.poll.PollFragment
import com.hellfish.evemento.event.task.TaskListFragment
import com.hellfish.evemento.event.transport.TransportFragment
import kotlinx.android.synthetic.main.event_element_time.view.*
import kotlinx.android.synthetic.main.event_elements.view.*
import kotlinx.android.synthetic.main.fragment_event.view.*

interface EventMode {

    var navigatorListener: Navigator

    fun editingEvent(event: Event, view: EventLayout): Unit
    fun viewingEvent(event: Event, view: EventLayout): Unit

    fun FloatingActionButton.withDrawable(drawableId: Int): FloatingActionButton =
            apply { setImageDrawable(ResourcesCompat.getDrawable(resources, drawableId, null)) }

}

interface ViewMode : EventMode {

    override    fun viewingEvent(event: Event, view: EventLayout): Unit = view.run {
        load(event)
        notEditableElementsVisibility(View.VISIBLE)
        changeTextColor(R.color.text)
        enabledEditableElements(false)

        taskElement.setOnClickListener { navigatorListener.replaceFragment(TaskListFragment()) }
        pollElement.setOnClickListener { navigatorListener.replaceFragment(PollFragment()) }
        rideElement.setOnClickListener { navigatorListener.replaceFragment(TransportFragment()) }

        eventFab.withDrawable(R.drawable.ic_edit_white_24dp).setOnClickListener { editingEvent(event, view) }
    }

}

interface EditMode : EventMode {

    override fun editingEvent(event: Event, view: EventLayout): Unit = view.run {
        load(event)
        notEditableElementsVisibility(View.GONE)
        changeTextColor(R.color.grey)
        enabledEditableElements(true)

        eventFab.withDrawable(R.drawable.ic_check_white_24dp).setOnClickListener { viewingEvent(view.edit(event), view) }
    }

}