package com.hellfish.evemento.event.list

import android.content.Context
import android.support.v7.widget.CardView
import com.hellfish.evemento.R
import android.view.View
import android.widget.TextView
import com.hellfish.evemento.RecyclerAdapter
import com.hellfish.evemento.event.Event
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.fragment_event_list_item.view.*

class EventListAdapter(val events: MutableList<Event>, private val fragment: EventListFragment) :  RecyclerAdapter<CardView, Event>(events) {

    override fun layout(item: Int): Int {
        return when (item) {
            EMPTY_VIEW -> R.layout.fragment_event_list_empty
            else -> R.layout.fragment_event_list_item
        }
    }

    override fun doOnEmptyOnBindViewHolder(): (view: TextView, context: Context?) -> Unit {
        return { view, _ ->
            view.text = "No events yet!"
            view.setOnClickListener { fragment.createNewEvent() }
        }
    }

    override fun doOnItemOnBindViewHolder(): (view: CardView, item: Event, context: Context?) -> Unit {
        return { view, item, _ ->
            if (item.imageUrl != "") {
                view.eventCardScrim.visibility = View.VISIBLE
                view.eventCardImage.layoutParams.height = view.resources.getDimension(R.dimen.eventCardImageLarge).toInt()
                Picasso.get().load(item.imageUrl).placeholder(R.color.colorPrimaryDark).into(view.eventCardImage)
            } else {
                view.eventCardScrim.visibility = View.GONE
                view.eventCardImage.layoutParams.height = view.resources.getDimension(R.dimen.eventCardImageShort).toInt()
                view.eventCardImage.setImageResource(0)
            }

            view.eventCardTitle.text = item.title

            view.eventCardDescription.text = item.description
            view.eventCardDescription.visibility = if (item.description != "") View.VISIBLE else View.GONE


            view.eventCardStartsDate.text = String.format("Starts: %s", fragment.dateTimeFormatter.print(item.startDate))
            view.eventCardEndsDate.text = String.format("Ends: %s", fragment.dateTimeFormatter.print(item.endDate))

            view.setOnClickListener { fragment.onSelectedEvent(item) }

        }
    }
}