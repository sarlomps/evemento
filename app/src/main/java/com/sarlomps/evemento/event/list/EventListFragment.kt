package com.sarlomps.evemento.event.list

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sarlomps.evemento.event.Event
import com.sarlomps.evemento.event.EventFragment
import com.sarlomps.evemento.*
import com.sarlomps.evemento.extensions.showSnackbar
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_event_list.*


class EventListFragment : NavigatorFragment() {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var eventListViewModel: EventListViewModel
    lateinit var dateTimeFormatter: DateTimeFormatter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dateTimeFormatter = DateTimeFormat.forPattern(getString(R.string.DateTimeFormat))

        eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)
        eventListViewModel = ViewModelProviders.of(activity!!).get(EventListViewModel::class.java)

        eventListViewModel.fetchEventsForCurrentUser { errorMessage -> errorMessage?.let { showSnackbar(it, main_container) } }
        eventListViewModel.events.observe(this, Observer { newEvents ->
            newEvents?.let {
                eventListRecyclerView.adapter = EventListAdapter(it.toMutableList(), this)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event_list, container, false)
    }

    override fun onResume() {
        super.onResume()
        eventListViewModel.run { refresh() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        eventListRecyclerView.layoutManager = LinearLayoutManager(context)

        eventListFab.setOnClickListener {
            createNewEvent()
        }
    }

    fun createNewEvent() {
        eventViewModel.select(null)
        navigatorListener.replaceFragment(EventFragment())

    }

    fun onSelectedEvent(event: Event) {
        eventViewModel.select(event)
        navigatorListener.replaceFragment(EventFragment())
    }
}
