package com.hellfish.evemento.event

import android.support.design.widget.FloatingActionButton
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import com.hellfish.evemento.Navigator
import com.hellfish.evemento.R
import com.hellfish.evemento.event.poll.PollFragment
import com.hellfish.evemento.event.task.TaskListFragment
import kotlinx.android.synthetic.main.event_elements.view.*
import kotlinx.android.synthetic.main.fragment_event.view.*

interface EventModes {

    var navigatorListener: Navigator

    fun viewingEvent(event: Event, view: EventLayout): Unit = view.run {
                load(event)
                listsVisibility(View.VISIBLE)
                changeTextColor(R.color.text)
                editablesEnabled(false)

                taskElement.setOnClickListener { navigatorListener.replaceFragment(TaskListFragment()) }
                pollElement.setOnClickListener { navigatorListener.replaceFragment(PollFragment()) }

                eventFab.withDrawable(R.drawable.ic_edit_white_24dp).setOnClickListener { editingEvent(event, view) }
            }


    fun editingEvent(event: Event, view: EventLayout): Unit = view.run {
            load(event)
            listsVisibility(View.GONE)
            changeTextColor(R.color.grey)
            editablesEnabled(true)

            eventFab.withDrawable(R.drawable.ic_check_white_24dp).setOnClickListener { viewingEvent(view.edit(event), view) }
        }


    private fun FloatingActionButton.withDrawable(drawableId: Int): FloatingActionButton =
            apply { setImageDrawable(ResourcesCompat.getDrawable(resources, drawableId, null)) }

}