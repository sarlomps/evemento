package com.hellfish.evemento.event.transport;

import android.content.Context
import android.os.Bundle
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.CardView
import android.widget.TextView
import com.hellfish.evemento.Navigator
import com.hellfish.evemento.R
import com.hellfish.evemento.RecyclerAdapter
import com.hellfish.evemento.api.User
import com.hellfish.evemento.event.guest.UserColor
import kotlinx.android.synthetic.main.transport_item.view.*

class TransportAdapter(val transportList: List<TransportItem>, private val navigatorListener: Navigator): RecyclerAdapter<CardView, TransportItem>(transportList), UserColor{
    override fun doOnEmptyOnBindViewHolder(): (view: TextView, context: Context?) -> Unit {
        return { view, _ ->
            view.text = "No rides yet"
        }

    }

    override fun doOnItemOnBindViewHolder(): (view: CardView, item: TransportItem, context: Context?) -> Unit {
        return { view, item, _ ->
            view.txtDriverName.text = item.driverName()
            view.txtAvailableSlots.text = item.availableSlots().toString()
            drawDriverCircle(view, item.driver)
            val transportDetailFragment = TransportDetailFragment()
            val args = Bundle()
            args.putParcelable("driver", item.driver)
            transportDetailFragment.arguments = args
            view.setOnClickListener { navigatorListener.replaceFragment(transportDetailFragment)}
        }
    }

    override fun layout(item: Int): Int {
        return when (item) {
            EMPTY_VIEW -> R.layout.fragment_event_list_empty
            else -> R.layout.transport_item
        }
    }

    private fun drawDriverCircle(view: CardView, item: User) {
        DrawableCompat.setTint(view.driverCircle.drawable, userColor(item.userId, item.displayName))
        view.driverInitial.text = item.displayName.first().toUpperCase().toString()
    }

}
