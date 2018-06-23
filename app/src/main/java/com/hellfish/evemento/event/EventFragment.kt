package com.hellfish.evemento.event

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellfish.evemento.EventViewModel
import com.hellfish.evemento.NavigatorFragment
import com.hellfish.evemento.OnBackPressedListener
import com.hellfish.evemento.R
import com.hellfish.evemento.event.poll.PollFragment
import com.hellfish.evemento.event.task.TaskListFragment
import com.hellfish.evemento.event.time.DatePickerDialogFactory
import com.hellfish.evemento.event.time.TimePickerDialogFactory
import com.hellfish.evemento.event.transport.TransportFragment
import kotlinx.android.synthetic.main.event_element_time.*
import kotlinx.android.synthetic.main.event_elements.*
import kotlinx.android.synthetic.main.event_tool_bar.*
import kotlinx.android.synthetic.main.fragment_event.view.*
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class EventFragment : NavigatorFragment(), DatePickerDialogFactory, TimePickerDialogFactory {


    lateinit var dateTimeFormatter: DateTimeFormatter
    override lateinit var dateFormatter: DateTimeFormatter
    override lateinit var timeFormatter: DateTimeFormatter

    lateinit var eventViewModel: EventViewModel
    var editing: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dateTimeFormatter = DateTimeFormat.forPattern(getString(R.string.DateTimeFormat))
        dateFormatter = DateTimeFormat.forPattern(getString(R.string.DateFormat))
        timeFormatter = DateTimeFormat.forPattern(getString(R.string.TimeFormat))

        eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)
        eventViewModel.selectedEvent.observe(this, Observer { event ->
            if (!editing) {
                eventTitle.setText(event?.title)
                descriptionElement.setText(event?.description)
                descriptionElement.run { visibility = if (text.toString() == "") View.GONE else View.VISIBLE }
                event?.let {
                    val (startDateString, startTimeString) = it.startDate.replace(" ", "").split("-")
                    val (endDateString, endTimeString) = it.endDate.replace(" ", "").split("-")
                    timeElement.text = String.format("%s\n%s", it.startDate, it.endDate)
                    startDateElement.text = startDateString
                    endDateElement.text = endDateString
                    startTimeElement.text = startTimeString
                    endTimeElement.text = endTimeString
                }
                locationElement.text = event?.location
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return EventLayout(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val (startDatePicker, endDatePicker) = createLinkedDatePickerDialogs(context, startDateElement, endDateElement)
        val (startTimePicker, endTimePicker) = createLinkedTimePickerDialogs(context, startDateElement, endDateElement, startTimeElement, endTimeElement)

        taskElement.setOnClickListener { navigatorListener.replaceFragment(TaskListFragment()) }
        pollElement.setOnClickListener { navigatorListener.replaceFragment(PollFragment()) }
        rideElement.setOnClickListener { navigatorListener.replaceFragment(TransportFragment()) }

        startDateElement.run { setOnClickListener { startDatePicker.updateDate(this, dateFormatter).show() } }
        endDateElement.run { setOnClickListener { endDatePicker.updateDate(this, dateFormatter).show() } }
        startTimeElement.run { setOnClickListener { startTimePicker.updateTime(this, timeFormatter).show() } }
        endTimeElement.run { setOnClickListener { endTimePicker.updateTime(this, timeFormatter).show() } }

        editing = savedInstanceState?.getBoolean("editing") ?: false
        if (eventViewModel.selected() != null) decideViewMode()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("editing", editing)
    }


    override fun setupToolbar() {
        navigatorListener.setCustomToolbar(eventToolbar)
        eventAppbar.setExpanded(true)
    }

    fun toggleViewMode() {
        editing = !editing
        decideViewMode()
    }

    fun decideViewMode() {
        if (editing) setViewMode(
                { toggleViewMode(); eventViewModel.updateView()},
                R.drawable.ic_check_white_24dp,
                { toggleViewMode(); eventViewModel.select((view as EventLayout).event()) }
        )
        else setViewMode(null, R.drawable.ic_edit_white_24dp) { toggleViewMode() }
    }

    private fun setViewMode(backPressedListener: OnBackPressedListener?, drawable: Int, onClickListener: () -> Unit) = (view as EventLayout).run {
        this.mode(editing)
        navigatorListener.onBackPressedListener = backPressedListener
        eventFab.withDrawable(drawable).setOnClickListener { onClickListener() }
    }

    fun FloatingActionButton.withDrawable(drawableId: Int): FloatingActionButton =
            apply { setImageDrawable(ResourcesCompat.getDrawable(resources, drawableId, null)) }

}

