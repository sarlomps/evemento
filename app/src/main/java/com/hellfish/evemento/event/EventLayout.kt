package com.hellfish.evemento.event

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v4.content.res.ResourcesCompat
import android.widget.TextView
import com.hellfish.evemento.R
import kotlinx.android.synthetic.main.event_elements.view.*
import kotlinx.android.synthetic.main.event_tool_bar.view.*

class EventLayout(context: Context?) : CoordinatorLayout(context) {

    init { inflate(context, R.layout.fragment_event, this) }

    fun loadEvent(event: Event?) {
        eventTitle.text = event?.title
        descriptionElement.text = event?.description
        timeElement.text = event?.time
        locationElement.text = event?.location
        guestElement.text = event?.guests?.joinToString()
        rideElement.text = event?.rides?.joinToString()
        taskElement.text = event?.tasks?.joinToString()
        pollElement.text = event?.polls?.joinToString()
        commentElement.text = event?.comments?.joinToString()

    }

    fun changeTextColor(colorId: Int) {
        descriptionElement.setTextColorId(colorId)
        timeElement.setTextColorId(colorId)
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