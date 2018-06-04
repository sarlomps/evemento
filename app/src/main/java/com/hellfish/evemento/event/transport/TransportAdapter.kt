package com.hellfish.evemento.event.transport;

import android.content.Context
import android.widget.LinearLayout
import com.hellfish.evemento.Navigator
import com.hellfish.evemento.R
import com.hellfish.evemento.RecyclerAdapter
import kotlinx.android.synthetic.main.transport_item.view.*

class TransportAdapter(val transportList: ArrayList<TransportItem>, private val navigatorListener: Navigator): RecyclerAdapter<LinearLayout, TransportItem>(transportList){

    override fun layout(item: Int): Int = R.layout.transport_item

    override fun doOnItemOnBindViewHolder(view: LinearLayout, item: TransportItem, context: Context) {
        view.txtDriverName.text = item.driver
        view.txtAvailableSlots.text = item.availableSlots
        view.setOnClickListener { navigatorListener.replaceFragment(TransportDetailFragment())}
    }

}
