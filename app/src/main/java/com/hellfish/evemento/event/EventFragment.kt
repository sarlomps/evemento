package com.hellfish.evemento.event

import android.app.Activity.RESULT_OK
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlaceAutocomplete
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
import android.content.Intent
import com.google.android.gms.location.places.ui.PlaceAutocomplete.RESULT_ERROR
import com.hellfish.evemento.extensions.withDrawable


class EventFragment : NavigatorFragment(), DatePickerDialogFactory, TimePickerDialogFactory {


    private lateinit var eventViewModel: EventViewModel
    private var editing: Boolean = false

    private val autocompleteRequestCode = 42

    lateinit var dateTimeFormatter: DateTimeFormatter
    override lateinit var dateFormatter: DateTimeFormatter
    override lateinit var timeFormatter: DateTimeFormatter


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
                    timeElement.text = String.format("%s\n%s", dateTimeFormatter.print(it.startDate), dateTimeFormatter.print(it.endDate))
                    startDateElement.text = dateFormatter.print(it.startDate)
                    endDateElement.text = dateFormatter.print(it.endDate)
                    startTimeElement.text = timeFormatter.print(it.startDate)
                    endTimeElement.text = timeFormatter.print(it.endDate)
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
        setLocationListener()
        setDateTimeListeners()
        setListsListeners()

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
                { toggleViewMode(); eventViewModel.select((view as EventLayout).event(dateTimeFormatter)) }
        )
        else setViewMode(null, R.drawable.ic_edit_white_24dp) { toggleViewMode() }
    }

    private fun setViewMode(backPressedListener: OnBackPressedListener?, drawable: Int, onClickListener: () -> Unit) = (view as EventLayout).run {
        mode(editing)
        navigatorListener.onBackPressedListener = backPressedListener
        eventFab.withDrawable(drawable).setOnClickListener { onClickListener() }
    }

    private fun setLocationListener() = locationElement.setOnClickListener {
        try {
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(activity)
            startActivityForResult(intent, autocompleteRequestCode)
        } catch (e: Exception) {
            when (e) {
                is GooglePlayServicesRepairableException, is GooglePlayServicesNotAvailableException -> showAutocompleteErrorToast()
                else -> throw e
            }
        }
    }

    private fun showAutocompleteErrorToast() = Toast.makeText(activity, getString(R.string.autocompleteError), Toast.LENGTH_LONG).show()
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            requestCode == autocompleteRequestCode && resultCode == RESULT_OK -> locationElement.text = PlaceAutocomplete.getPlace(activity, data).name.toString()
            requestCode == autocompleteRequestCode && resultCode == RESULT_ERROR-> showAutocompleteErrorToast()
            else -> Unit
        }
    }

    private fun setDateTimeListeners() {
        val (startDatePicker, endDatePicker) = createLinkedDatePickerDialogs(context, startDateElement, endDateElement)
        val (startTimePicker, endTimePicker) = createLinkedTimePickerDialogs(context, startDateElement, endDateElement, startTimeElement, endTimeElement)

        startDateElement.run { setOnClickListener { startDatePicker.updateDate(this, dateFormatter).show() } }
        endDateElement.run { setOnClickListener { endDatePicker.updateDate(this, dateFormatter).show() } }
        startTimeElement.run { setOnClickListener { startTimePicker.updateTime(this, timeFormatter).show() } }
        endTimeElement.run { setOnClickListener { endTimePicker.updateTime(this, timeFormatter).show() } }
    }

    private fun setListsListeners() {
        taskElement.setOnClickListener { navigatorListener.replaceFragment(TaskListFragment()) }
        pollElement.setOnClickListener { navigatorListener.replaceFragment(PollFragment()) }
        rideElement.setOnClickListener { navigatorListener.replaceFragment(TransportFragment()) }
    }

}

