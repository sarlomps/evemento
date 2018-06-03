package com.hellfish.evemento.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hellfish.evemento.NavigatorFragment
import com.hellfish.evemento.event.time.DateTimePickerDialogBuilder
import kotlinx.android.synthetic.main.event_element_time.*
import kotlinx.android.synthetic.main.event_tool_bar.*
import java.util.Calendar

class EventFragment : NavigatorFragment(), ViewMode, EditMode, DateTimePickerDialogBuilder {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return EventLayout(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendar = Calendar.getInstance()

        val startDatePicker = buildDatePickerDialog(context, calendar, startDateElement)
        val endDatePicker = buildDatePickerDialog(context, calendar, endDateElement)
        val startTimePicker = buildTimePickerDialog(context, calendar, startTimeElement)
        val endTimePicker = buildTimePickerDialog(context, calendar, endTimeElement)

        startDateElement.setOnClickListener { startDatePicker.show() }
        endDateElement.setOnClickListener { endDatePicker.show() }
        startTimeElement.setOnClickListener { startTimePicker.show() }
        endTimeElement.setOnClickListener { endTimePicker.show() }

        val event = arguments?.getParcelable<Event>("event")
        if (event != null) viewingEvent(event, view as EventLayout)
    }

    override fun setupToolbar() {
        navigatorListener.setCustomToolbar(eventToolbar)
        eventAppbar.setExpanded(true)
    }

}

