package com.hellfish.evemento.event

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hellfish.evemento.NavigatorFragment
import kotlinx.android.synthetic.main.event_element_time.*
import kotlinx.android.synthetic.main.event_tool_bar.*
import java.util.Calendar

class EventFragment : NavigatorFragment(), ViewMode, EditMode {

    override lateinit var startDatePicker: DatePickerDialog
    override lateinit var endDatePicker: DatePickerDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return EventLayout(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendar = Calendar.getInstance()

        startDatePicker = DatePickerDialog(context, buildDateListener(startTimeElement), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        endDatePicker = DatePickerDialog(context, buildDateListener(endTimeElement), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

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

}

