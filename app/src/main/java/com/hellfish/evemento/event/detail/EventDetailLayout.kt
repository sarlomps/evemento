package com.hellfish.evemento.event.detail

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import com.hellfish.evemento.R
import com.hellfish.evemento.event.Event
import kotlinx.android.synthetic.main.event_detail_elements.view.*
import kotlinx.android.synthetic.main.event_detail_tool_bar.view.*

class EventDetailLayout(context: Context?) : CoordinatorLayout(context) {

    init { inflate(context, R.layout.fragment_event_detail, this) }

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

}
