package com.hellfish.evemento.event

import android.support.design.widget.FloatingActionButton
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import com.hellfish.evemento.Navigator
import com.hellfish.evemento.R
import kotlinx.android.synthetic.main.fragment_event.view.*

interface ViewAndEditEvent {

    var event: Event
    var editing: Boolean
    val navigatorListener: Navigator

    fun viewingEvent(view: EventLayout): Unit = view.run {
        navigatorListener.onBackPressedListener = null
        editing = false
        load(event)
        notEditableElementsVisibility(View.VISIBLE)
        changeTextColor(R.color.text)
        enabledEditableElements(false)

        eventFab.withDrawable(R.drawable.ic_edit_white_24dp).setOnClickListener {
            editingEvent(view)
        }
    }

    fun editingEvent(view: EventLayout): Unit = view.run {
        navigatorListener.onBackPressedListener =   { viewingEvent(view) }
        editing = true
        notEditableElementsVisibility(View.GONE)
        changeTextColor(R.color.grey)
        enabledEditableElements(true)

        eventFab.withDrawable(R.drawable.ic_check_white_24dp).setOnClickListener {
            event = view.edit(event)
            viewingEvent(view)
        }
    }

    fun FloatingActionButton.withDrawable(drawableId: Int): FloatingActionButton =
            apply { setImageDrawable(ResourcesCompat.getDrawable(resources, drawableId, null)) }

}