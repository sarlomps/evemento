package com.hellfish.evemento.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellfish.evemento.NavigatorFragment
import com.hellfish.evemento.event.time.DatePickerDialogFactory
import com.hellfish.evemento.event.time.TimePickerDialogFactory
import kotlinx.android.synthetic.main.event_element_time.*
import kotlinx.android.synthetic.main.event_tool_bar.*

class EventFragment : NavigatorFragment(), ViewMode, EditMode, DatePickerDialogFactory, TimePickerDialogFactory {

    override lateinit var event: Event

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return EventLayout(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val (startDatePicker, endDatePicker) = createLinkedDatePickerDialogs(context, startDateElement, endDateElement)
        val (startTimePicker, endTimePicker) = createLinkedTimePickerDialogs(context, startTimeElement, endTimeElement)

        startDateElement.run { setOnClickListener { startDatePicker.updateDate(this, onlyDateFormatter).show() } }
        endDateElement.run { setOnClickListener { endDatePicker.updateDate(this, onlyDateFormatter).show() } }
        startTimeElement.run { setOnClickListener { startTimePicker.updateTime(this, onlyTimeFormatter).show() } }
        endTimeElement.run { setOnClickListener { endTimePicker.updateTime(this, onlyTimeFormatter).show() } }

        val argEvent = savedInstanceState?.getParcelable<Event>("event") ?: arguments?.getParcelable<Event>("event")
        if (argEvent != null) {
            event = argEvent
            viewingEvent(view as EventLayout)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("event", event)
    }

    override fun setupToolbar() {
        navigatorListener.setCustomToolbar(eventToolbar)
        eventAppbar.setExpanded(true)
    }

}

