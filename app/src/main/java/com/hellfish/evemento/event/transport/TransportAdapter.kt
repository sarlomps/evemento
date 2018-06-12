package com.hellfish.evemento.event.transport;

import android.content.Context
import android.support.v7.widget.CardView
import com.hellfish.evemento.Navigator
import com.hellfish.evemento.R
import com.hellfish.evemento.RecyclerAdapter
import kotlinx.android.synthetic.main.transport_item.view.*

class TransportAdapter(val transportList: ArrayList<TransportItem>, private val navigatorListener: Navigator): RecyclerAdapter<CardView, TransportItem>(transportList){

    override fun layout(item: Int): Int = R.layout.transport_item

    override fun doOnItemOnBindViewHolder(view: CardView, item: TransportItem, context: Context) {
        view.txtDriverName.text = item.driverName()
        view.txtAvailableSlots.text = item.availableSlots().toString()
        view.setOnClickListener { navigatorListener.replaceFragment(TransportDetailFragment())}
    }

}
