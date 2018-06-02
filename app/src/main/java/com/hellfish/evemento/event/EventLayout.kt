package com.hellfish.evemento.event

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v4.content.res.ResourcesCompat
import android.widget.TextView
import com.hellfish.evemento.R
import kotlinx.android.synthetic.main.event_element_time.view.*
import kotlinx.android.synthetic.main.event_elements.view.*
import kotlinx.android.synthetic.main.event_tool_bar.view.*

class EventLayout(context: Context?) : CoordinatorLayout(context) {

    init { inflate(context, R.layout.fragment_event, this) }

    fun edit(event: Event) = event.copy(
            title = eventTitle.text.toString(),
            time = EventTime(
                    startDate = startTimeElement.text.toString(),
                    endDate = endTimeElement.text.toString()
            ),
            description = descriptionElement.text.toString(),
            location = locationElement.text.toString()
    )

    fun load(event: Event?) {
        eventTitle.setText(event?.title)
        descriptionElement.setText(event?.description)
        startTimeElement.text = event?.time?.startDate
        endTimeElement.text = event?.time?.endDate
        locationElement.text = event?.location
    }

    fun enabledEditableElements(enabled: Boolean) {
        descriptionElement.isEnabled = enabled
        eventTitle.isEnabled = enabled
        eventTitle.requestFocus()
    }

    fun listsVisibility(visibility: Int) {
        guestElement.visibility = visibility
        rideElement.visibility = visibility
        taskElement.visibility = visibility
        pollElement.visibility = visibility
        commentElement.visibility = visibility
    }

    fun changeTextColor(colorId: Int) {
        descriptionElement.setTextColorId(colorId)
        startTimeElement.setTextColorId(colorId)
        endTimeElement.setTextColorId(colorId)
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
