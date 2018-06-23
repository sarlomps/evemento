package com.hellfish.evemento.event

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import android.widget.TextView
import com.hellfish.evemento.R
import com.hellfish.evemento.extensions.toVisibility
import kotlinx.android.synthetic.main.event_element_time.view.*
import kotlinx.android.synthetic.main.event_elements.view.*
import kotlinx.android.synthetic.main.event_tool_bar.view.*
import org.joda.time.format.DateTimeFormatter

class EventLayout(context: Context?) : CoordinatorLayout(context) {

    init { inflate(context, R.layout.fragment_event, this) }

    fun event(dateTimeFormatter: DateTimeFormatter) = Event(
            title = eventTitle.text.toString(),
            startDate = dateTimeFormatter.parseDateTime("${startDateElement.text} ${startTimeElement.text}"),
            endDate = dateTimeFormatter.parseDateTime("${endDateElement.text} ${endTimeElement.text}"),
            description = descriptionElement.text.toString(),
            location = locationElement.text.toString()
    )

    fun mode(editing: Boolean) {
        if (editing) {
            notEditableElementsVisibility(View.GONE)
            changeTextColor(R.color.grey)
            enabledEditableElements(true)
        } else {
            notEditableElementsVisibility(View.VISIBLE)
            changeTextColor(R.color.text)
            enabledEditableElements(false)
        }
    }

    private fun enabledEditableElements(enabled: Boolean) {
        locationElement.isEnabled = enabled
        editTimeElement.visibility = enabled.toVisibility()
        if (enabled) descriptionElement.visibility = View.VISIBLE
        descriptionElement.isEnabled = enabled
        eventTitle.isEnabled = enabled
        eventTitle.requestFocus()
    }

    private fun notEditableElementsVisibility(visibility: Int) {
        timeElement.visibility = visibility
        guestElement.visibility = visibility
        rideElement.visibility = visibility
        taskElement.visibility = visibility
        pollElement.visibility = visibility
        commentElement.visibility = visibility
    }

    private fun changeTextColor(colorId: Int) {
        descriptionElement.setTextColorId(colorId)
        locationElement.setTextColorId(colorId)
    }

    private fun TextView.setTextColorId(colorId: Int) {
        setTextColor(ResourcesCompat.getColor(resources, colorId, null))
    }

}
