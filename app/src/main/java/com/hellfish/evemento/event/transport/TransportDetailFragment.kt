package com.hellfish.evemento.event.transport

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hellfish.evemento.NavigatorFragment
import com.hellfish.evemento.R
import kotlinx.android.synthetic.main.fragment_transport_detail.view.*

class TransportDetailFragment : NavigatorFragment(){

    lateinit var transport: TransportItem

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_transport_detail, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val argTransport = arguments?.getParcelable<TransportItem>("transport")
        if (argTransport != null) {
            transport = argTransport
            view.txtDriverName.text = transport.driver.nickname
            transport.passangers
                    .map { createPassanger(view.context, it) }
                    .forEach { view.llTransportDetail.addView(it) }
        }
    }

    private fun createPassanger(context: Context, passanger: UserMiniDetail): TextView {
        return TextView(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setTextAppearance(R.style.Base_TextAppearance_AppCompat_Medium)
            } else {
                setTextAppearance(context, R.style.Base_TextAppearance_AppCompat_Medium)
            }
            text = passanger.nickname
        }
    }
}