package com.sarlomps.evemento.event.transport;

import android.content.Context
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.CardView
import android.widget.TextView
import com.sarlomps.evemento.Navigator
import com.sarlomps.evemento.R
import com.sarlomps.evemento.RecyclerAdapter
import com.sarlomps.evemento.api.User
import com.sarlomps.evemento.event.guest.CircleColor
import kotlinx.android.synthetic.main.transport_item.view.*

class TransportAdapter(val transportList: List<TransportItem>, private val navigatorListener: Navigator, val transportViewModel: TransportViewModel): RecyclerAdapter<CardView, TransportItem>(transportList), CircleColor{
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
            view.setOnClickListener {
                transportViewModel.selectDriver(item.driver)
                navigatorListener.replaceFragment(TransportDetailFragment())
            }
        }
    }

    override fun layout(item: Int): Int {
        return when (item) {
            EMPTY_VIEW -> R.layout.fragment_event_list_empty
            else -> R.layout.transport_item
        }
    }

    private fun drawDriverCircle(view: CardView, item: User) {
        DrawableCompat.setTint(view.driverCircle.drawable, circleColor(item.userId, item.displayName))
        view.driverInitial.text = item.displayName.first().toUpperCase().toString()
    }

}
