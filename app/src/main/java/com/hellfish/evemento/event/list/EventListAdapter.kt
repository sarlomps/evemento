package com.hellfish.evemento.event
import com.hellfish.evemento.R
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.hellfish.evemento.event.list.EventListFragment


import com.hellfish.evemento.extensions.inflate

import kotlinx.android.synthetic.main.fragment_event_list_item.view.*

class EventListAdapter(private val fragment: EventListFragment, private val events: ArrayList<Event>) : RecyclerView.Adapter<EventListAdapter.EventHolder>() {

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holder: EventListAdapter.EventHolder, position: Int) {
        val event = events[position]
        holder.bindEvent(event, fragment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventListAdapter.EventHolder{
        val inflatedView = parent.inflate(R.layout.fragment_event_list_item)
        return EventHolder(inflatedView)
    }

    class EventHolder(v:View) : RecyclerView.ViewHolder(v) {

        private var view : View = v
        private var event : Event? = null

        fun bindEvent(event: Event, fragment: EventListFragment) {
            this.event = event
            view.text_title.text = event.title
            view.text_desc.text = event.description
            view.text_date.text = event.time
            // TODO: Bindear todos los valores del evento que faltan cuando este completo el layout.

            view.setOnClickListener {
                fragment.onSelectedEvent(event)
            }

        }
    }

}

