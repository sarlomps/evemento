package com.hellfish.evemento.event

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v4.content.res.ResourcesCompat
import android.widget.TextView
import com.hellfish.evemento.R
import com.hellfish.evemento.extensions.toVisibility
import kotlinx.android.synthetic.main.event_element_time.view.*
import kotlinx.android.synthetic.main.event_elements.view.*
import kotlinx.android.synthetic.main.event_tool_bar.view.*

class EventLayout(context: Context?) : CoordinatorLayout(context) {

    init { inflate(context, R.layout.fragment_event, this) }

    fun edit(event: Event) = event.copy(
            title = eventTitle.text.toString(),
            time = EventTime(
                    startDate = startDateElement.text.toString(),
                    endDate = endDateElement.text.toString()
            ),
            description = descriptionElement.text.toString(),
            location = locationElement.text.toString()
    )

    fun load(event: Event?) {
        eventTitle.setText(event?.title)
        descriptionElement.setText(event?.description)
        event?.time?.let {
            val (startDateString, startTimeString) = it.startDate.replace(" ", "").split("-")
            val (endDateString, endTimeString) = it.endDate.replace(" ", "").split("-")
            timeElement.text = String.format("%s\n%s", it.startDate, it.endDate)
            startDateElement.text = startDateString
            endDateElement.text = endDateString
            startTimeElement.text = startTimeString
            endTimeElement.text = endTimeString
        }
        locationElement.text = event?.location
    }

    fun enabledEditableElements(enabled: Boolean) {
        editTimeElement.visibility = enabled.toVisibility()
        descriptionElement.isEnabled = enabled
        eventTitle.isEnabled = enabled
        eventTitle.requestFocus()
    }

    fun notEditableElementsVisibility(visibility: Int) {
        timeElement.visibility = visibility
        guestElement.visibility = visibility
        rideElement.visibility = visibility
        taskElement.visibility = visibility
        pollElement.visibility = visibility
        commentElement.visibility = visibility
    }

    fun changeTextColor(colorId: Int) {
        descriptionElement.setTextColorId(colorId)
        locationElement.setTextColorId(colorId)
        guestElement.setTextColorId(colorId)
        rideElement.setTextColorId(colorId)
        taskElement.setTextColorId(colorId)
        pollElement.setTextColorId(colorId)
        commentElement.setTextColorId(colorId)
    }

    fun TextView.setTextColorId(colorId: Int) {
        setTextColor(ResourcesCompat.getColor(resources, colorId, null))
    }

}
