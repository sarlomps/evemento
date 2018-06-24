package com.hellfish.evemento.event.transport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        }
    }
}
