package com.hellfish.evemento.event.list


import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellfish.evemento.NavigatorFragment
import com.hellfish.evemento.R
import com.hellfish.evemento.event.Event
import com.hellfish.evemento.event.EventListAdapter
import com.hellfish.evemento.event.detail.EventDetailFragment
import kotlinx.android.synthetic.main.fragment_event_list.*



class EventListFragment : NavigatorFragment() {

    lateinit var eventsRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_event_list, container, false)

        eventsRecyclerView = view.findViewById(R.id.events_recycler_view)

        val manager = LinearLayoutManager(context)
        eventsRecyclerView.setLayoutManager(manager)

        // TODO: Usar los eventos pasados como argumento del fragment, por ahora usa eventos hardcoded
        eventsRecyclerView.adapter = EventListAdapter(this, arrayListOf(
                Event("Mock Title 1",
                        "Mock Description 1",
                        "Mock Time 1",
                        "Mock Location",
                        listOf("Juan", "Juan", "Juan"),
                        listOf("rides"),
                        listOf("tasks"),
                        listOf("polls"),
                        listOf("comments")),
                Event("Mock Title 2",
                        "Mock Description 2",
                        "Mock Time 2",
                        "Mock Location",
                        listOf("Juan", "Juan", "Juan"),
                        listOf("rides"),
                        listOf("tasks"),
                        listOf("polls"),
                        listOf("comments")),
                Event("Mock Title 3",
                        "Mock Description 3",
                        "Mock Time 3",
                        "Mock Location",
                        listOf("Juan", "Juan", "Juan"),
                        listOf("rides"),
                        listOf("tasks"),
                        listOf("polls"),
                        listOf("comments")),
                Event("Mock Title 4",
                        "Mock Description 4",
                        "Mock Time 4",
                        "Mock Location",
                        listOf("Juan", "Juan", "Juan"),
                        listOf("rides"),
                        listOf("tasks"),
                        listOf("polls"),
                        listOf("comments")),
                Event("Mock Title 5",
                        "Mock Description 5",
                        "Mock Time 5",
                        "Mock Location",
                        listOf("Juan", "Juan", "Juan"),
                        listOf("rides"),
                        listOf("tasks"),
                        listOf("polls"),
                        listOf("comments")),
                Event("Mock Title 6",
                        "Mock Description 6",
                        "Mock Time 6",
                        "Mock Location",
                        listOf("Juan", "Juan", "Juan"),
                        listOf("rides"),
                        listOf("tasks"),
                        listOf("polls"),
                        listOf("comments"))))

        return view

    }
    fun onSelectedEvent(event:Event) {
        val eventDetailFragment = EventDetailFragment()
        val args = Bundle()
        // TODO: Validar si hace falta algo mas para inicializar bien el EventDetailFragment
        args.putParcelable("event", event)
        eventDetailFragment.arguments = args
        listener.replaceFragment(eventDetailFragment)
    }
}
