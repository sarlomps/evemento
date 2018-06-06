package com.hellfish.evemento.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellfish.evemento.NavigatorFragment
import com.hellfish.evemento.event.poll.PollFragment
import com.hellfish.evemento.event.task.TaskListFragment
import com.hellfish.evemento.event.time.DatePickerDialogFactory
import com.hellfish.evemento.event.time.TimePickerDialogFactory
import com.hellfish.evemento.event.transport.TransportFragment
import kotlinx.android.synthetic.main.event_element_time.*
import kotlinx.android.synthetic.main.event_elements.*
import kotlinx.android.synthetic.main.event_tool_bar.*

class EventFragment : NavigatorFragment(), ViewAndEditEvent, DatePickerDialogFactory, TimePickerDialogFactory {

    override lateinit var event: Event
    override var editing: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return EventLayout(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val (startDatePicker, endDatePicker) = createLinkedDatePickerDialogs(context, startDateElement, endDateElement)
        val (startTimePicker, endTimePicker) = createLinkedTimePickerDialogs(context, startDateElement, endDateElement, startTimeElement, endTimeElement)

        taskElement.setOnClickListener { navigatorListener.replaceFragment(TaskListFragment()) }
        pollElement.setOnClickListener { navigatorListener.replaceFragment(PollFragment()) }
        rideElement.setOnClickListener { navigatorListener.replaceFragment(TransportFragment()) }

        startDateElement.run { setOnClickListener { startDatePicker.updateDate(this, onlyDateFormatter).show() } }
        endDateElement.run { setOnClickListener { endDatePicker.updateDate(this, onlyDateFormatter).show() } }
        startTimeElement.run { setOnClickListener { startTimePicker.updateTime(this, onlyTimeFormatter).show() } }
        endTimeElement.run { setOnClickListener { endTimePicker.updateTime(this, onlyTimeFormatter).show() } }

        editing = savedInstanceState?.getBoolean("editing") ?: false
        val argEvent = savedInstanceState?.getParcelable<Event>("event") ?: arguments?.getParcelable<Event>("event")
        if (argEvent != null) {
            event = argEvent
            if(editing) editingEvent(view as EventLayout) else viewingEvent(view as EventLayout)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("editing", editing)
        outState.putParcelable("event", event)
    }


    override fun setupToolbar() {
        navigatorListener.setCustomToolbar(eventToolbar)
        eventAppbar.setExpanded(true)
    }

}

