package com.hellfish.evemento.event

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hellfish.evemento.NavigatorFragment
import kotlinx.android.synthetic.main.event_element_time.*
import kotlinx.android.synthetic.main.event_element_time.view.*
import kotlinx.android.synthetic.main.event_tool_bar.*
import java.util.Calendar

class EventFragment : NavigatorFragment(), ViewMode, EditMode {

    private lateinit var startDatePicker: DatePickerDialog
    private lateinit var endDatePicker: DatePickerDialog
    private lateinit var startTimePicker: TimePickerDialog
    private lateinit var endTimePicker: TimePickerDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return EventLayout(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendar = Calendar.getInstance()

        startDatePicker = buildDatePickerDialog(calendar, startDateElement)
        endDatePicker = buildDatePickerDialog(calendar, endDateElement)
        startTimePicker = buildTimePickerDialog(calendar, startTimeElement)
        endTimePicker = buildTimePickerDialog(calendar, endTimeElement)

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

    fun buildDateListener(textView: TextView) = DatePickerDialog.OnDateSetListener { _, year, month, day ->
        textView.text= String.format("%d/%d/%d", day, month, year)
    }

    fun buildDatePickerDialog(calendar: Calendar, textView: TextView) =
            DatePickerDialog(context, buildDateListener(textView), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

    fun buildTimeListener(textView: TextView) = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
        textView.text= String.format("%d:%d", hour, minute)
    }

    fun buildTimePickerDialog(calendar: Calendar, textView: TextView) =
            TimePickerDialog(context, buildTimeListener(textView), calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true)


}

