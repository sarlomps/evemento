package com.hellfish.evemento.event.list

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellfish.evemento.event.Event
import com.hellfish.evemento.event.EventListAdapter
import com.hellfish.evemento.event.EventFragment
import android.support.design.widget.FloatingActionButton
import com.hellfish.evemento.*
import com.hellfish.evemento.extensions.showSnackbar
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import kotlinx.android.synthetic.main.activity_main.*



class EventListFragment : NavigatorFragment() {

    lateinit var eventViewModel: EventViewModel
    lateinit var eventListViewModel: EventListViewModel
    lateinit var eventsRecyclerView: RecyclerView
    lateinit var dateTimeFormatter: DateTimeFormatter
    var events: ArrayList<Event> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dateTimeFormatter = DateTimeFormat.forPattern(getString(R.string.DateTimeFormat))
        eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)
        eventListViewModel = ViewModelProviders.of(activity!!).get(EventListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_event_list, container, false)

        eventsRecyclerView = view.findViewById(R.id.events_recycler_view)

        val manager = LinearLayoutManager(context)
        eventsRecyclerView.setLayoutManager(manager)

        val newEvents: ArrayList<Event>? = arguments?.getParcelableArrayList<Event>("events")
        if (newEvents != null) {
            events = newEvents
        }
        eventsRecyclerView.adapter = EventListAdapter(this, events)

        eventListViewModel.events.observe(this, Observer { newEvents ->
            newEvents?.let {
                refresh(newEvents)
            }
        })

        val fab = view.findViewById<FloatingActionButton>(R.id.event_list_fab)
        fab.setOnClickListener {
            eventViewModel.select(null)
            navigatorListener.replaceFragment(EventFragment())
        }

        // Fetch user events
        eventListViewModel.fetchEventsForCurrentUser { errorMessage ->
            // Let user know if there was an error
            errorMessage?.let {
                showSnackbar(it, main_container)
            }
        }

        return view

    }

    fun refresh(newEvents: List<Event>) {
        events = ArrayList(newEvents)
        (eventsRecyclerView.adapter as EventListAdapter).refresh(events)

    }

    fun onSelectedEvent(event: Event) {
        eventViewModel.select(event)
        navigatorListener.replaceFragment(EventFragment())
    }
}
