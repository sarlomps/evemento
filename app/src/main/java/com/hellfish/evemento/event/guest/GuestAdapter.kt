package com.hellfish.evemento.event.guest

import android.content.Context
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.*
import android.view.View
import android.widget.TextView
import com.hellfish.evemento.R
import com.hellfish.evemento.RecyclerAdapter
import com.hellfish.evemento.api.Guest
import kotlinx.android.synthetic.main.guest_content.view.*

class GuestAdapter(guests: MutableList<Guest>, private val deleteListener: (Guest) -> View.OnClickListener) : RecyclerAdapter<CardView, Guest>(guests), UserColor {
    override fun doOnEmptyOnBindViewHolder(): (view: TextView, context: Context?) -> Unit {
        return { view, _ ->
            view.text = "No guests yet..."
        }
    }

    override fun doOnItemOnBindViewHolder(): (view: CardView, item: Guest, context: Context?) -> Unit {
        return { view, item, _ ->
            DrawableCompat.setTint(view.guestCircle.drawable, userColor(item.userId, item.displayName))
            view.guestInitial.text = item.displayName.first().toUpperCase().toString()

            view.guestName.text = item.displayName
            view.guestMail.text = String.format(" - %s", item.email)
            view.setOnClickListener(deleteListener(item))
        }
    }

    override fun layout(item : Int): Int {
        return when (item) {
            EMPTY_VIEW -> R.layout.fragment_event_list_empty
            else -> R.layout.guest_content
        }
    }
}
