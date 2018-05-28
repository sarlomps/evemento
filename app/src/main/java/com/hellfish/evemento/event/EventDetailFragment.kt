package com.hellfish.evemento.event

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellfish.evemento.R
import com.hellfish.evemento.event.task.TaskListFragment
import kotlinx.android.synthetic.main.event_detail_elements.*
import kotlinx.android.synthetic.main.event_detail_tool_bar.*

class EventDetailFragment : Fragment() {

    private lateinit var listener: Navigator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_event_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskElement.setOnClickListener { listener.replaceFragment(TaskListFragment()) }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listener.setCustomToolbar(eventDetailToolbar, false)
        eventTitle.text = getString(R.string.default_event_title)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is Navigator) listener = context else throw ClassCastException(context.toString() + " must implement Navigator.")
    }

}

interface Navigator {
    fun replaceFragment(fragment: Fragment): Unit
    fun setCustomToolbar(customToolbar: Toolbar, displayTitle: Boolean): Unit
}
