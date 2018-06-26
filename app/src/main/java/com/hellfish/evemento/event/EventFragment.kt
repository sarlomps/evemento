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
import com.hellfish.evemento.event.poll.PollFragment
import com.hellfish.evemento.event.task.TaskListFragment
import com.hellfish.evemento.event.time.DateTimePickerDialogFactory
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
import com.squareup.picasso.Picasso
import com.squareup.picasso.Callback
import android.support.v7.app.AlertDialog
import android.widget.EditText
import com.hellfish.evemento.*
import kotlinx.android.synthetic.main.fragment_event.*
import org.joda.time.DateTime


class EventFragment : NavigatorFragment(), DateTimePickerDialogFactory {


    private lateinit var eventViewModel: EventViewModel
    private var editing: Boolean = false

    private lateinit var imageDialogInput: EditText
    private lateinit var imageDialog: AlertDialog

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

                loadImage(event?.imageUrl)

                descriptionElement.setText(event?.description)
                descriptionElement.run { visibility = if (text.toString() == "") View.GONE else View.VISIBLE }

                event?.let {
                    timeElement.text = String.format("%s\n%s", dateTimeFormatter.print(it.startDate), dateTimeFormatter.print(it.endDate))
                    startDateElement.text = dateFormatter.print(it.startDate)
                    endDateElement.text = dateFormatter.print(it.endDate)
                    startTimeElement.text = timeFormatter.print(it.startDate)
                    endTimeElement.text = timeFormatter.print(it.endDate)
                }

                locationElement.setText(event?.location)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        imageDialogInput = EditText(activity!!)
        imageDialog = AlertDialog.Builder(activity!!)
                .setTitle("Add image url")
                .setView(imageDialogInput)
                .setPositiveButton(getString(R.string.accept)) { _, _ -> loadImage(imageDialogInput.text.toString()) }
                .setNegativeButton(getString(R.string.cancel)) { _, _ -> Unit }
                .setNeutralButton(getString(R.string.removeImage)) { _, _ -> loadImage("") }
                .create()
        return EventLayout(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setImageListener()
        setLocationListener()
        setDateTimeListeners()
        setListsListeners()

        if (eventViewModel.selected() != null) {
            if (eventViewModel.selected()?.user == SessionManager.getCurrentUser()?.userId) {
                eventFab.visibility = View.VISIBLE
                editing = savedInstanceState?.getBoolean("editing") ?: false
            } else {
                eventFab.visibility = View.GONE
                editing = false
            }
            decideViewMode()
        } else {
            editing = true
            setEditDateTimeFieldsToday()
            setViewMode(null, R.drawable.ic_check_white_24dp) {
                validatingEvent { validatedEvent ->
                    NetworkManager.pushEvent(validatedEvent) { newEventId, errorMessage ->
                        newEventId?.let {
                            eventViewModel.select(validatedEvent.copy(eventId = newEventId))
                            return@pushEvent
                        }
                        showToast(errorMessage ?: R.string.network_unknown_error)
                    }
                }
            }
        }
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
                { validatingEvent { validatedEvent -> eventViewModel.select(validatedEvent) } }
        )
        else setViewMode(null, R.drawable.ic_edit_white_24dp) { toggleViewMode() }
    }

    private fun setViewMode(backPressedListener: OnBackPressedListener?, drawable: Int, onClickListener: () -> Unit) = (view as EventLayout).run {
        mode(editing)
        navigatorListener.onBackPressedListener = backPressedListener
        eventFab.withDrawable(drawable).setOnClickListener { onClickListener() }
    }

    private fun validatingEvent(action: (Event) -> Unit) {
        if (eventTitle.text.isEmpty()) eventTitleLayout.setError(getString(R.string.titleValidation))
        if (locationElement.text.isEmpty()) locationElementLayout.setError(getString(R.string.locationValidation))

        if (eventTitle.text.isNotEmpty() && locationElement.text.isNotEmpty()) {
            toggleViewMode()
            val event = (view as EventLayout).event(dateTimeFormatter)
            action(event)
        }
    }


    private fun showToast(stringId: Int) = Toast.makeText(activity, getString(stringId), Toast.LENGTH_LONG).show()

    private fun setImageListener() = eventImage.setOnClickListener {
        imageDialogInput.setText(imageUrl.text)
        imageDialog.show()
    }

    fun loadImage(url: String?) {
        imageUrl.text = url
        if (imageUrl.text != "") Picasso.get().load(url).fit().into(eventImage, object: Callback {
            override fun onError(e: java.lang.Exception?) { showToast(R.string.errorLoadingImage) }
            override fun onSuccess() { }
        })
        else eventImage.setImageResource(0)
    }

    private fun setLocationListener() = locationElement.setOnClickListener {
        try {
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(activity)
            startActivityForResult(intent, autocompleteRequestCode)
        } catch (e: Exception) {
            when (e) {
                is GooglePlayServicesRepairableException, is GooglePlayServicesNotAvailableException -> showToast(R.string.autocompleteError)
                else -> throw e
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            requestCode == autocompleteRequestCode && resultCode == RESULT_OK -> locationElement.setText(PlaceAutocomplete.getPlace(activity, data).name)
            requestCode == autocompleteRequestCode && resultCode == RESULT_ERROR-> showToast(R.string.autocompleteError)
            else -> Unit
        }
    }

    private fun setDateTimeListeners() {
        val (startDatePicker, endDatePicker) = createLinkedDatePickerDialogs(context, startDateElement, endDateElement, startTimeElement, endTimeElement)
        val (startTimePicker, endTimePicker) = createLinkedTimePickerDialogs(context, startDateElement, endDateElement, startTimeElement, endTimeElement)

        startDateElement.run { setOnClickListener { startDatePicker.updateDate(this, dateFormatter).show() } }
        endDateElement.run { setOnClickListener { endDatePicker.updateDate(this, dateFormatter).show() } }
        startTimeElement.run { setOnClickListener { startTimePicker.updateTime(this, timeFormatter).show() } }
        endTimeElement.run { setOnClickListener { endTimePicker.updateTime(this, timeFormatter).show() } }
    }

    private fun setEditDateTimeFieldsToday() {
        val start = DateTime.now().plusHours(2)
        val end = DateTime.now().plusHours(4)
        startDateElement.text = dateFormatter.print(start)
        endDateElement.text = dateFormatter.print(end)
        startTimeElement.text = timeFormatter.print(start)
        endTimeElement.text = timeFormatter.print(end)
    }

    private fun setListsListeners() {
        taskElement.setOnClickListener { navigatorListener.replaceFragment(TaskListFragment()) }
        pollElement.setOnClickListener { navigatorListener.replaceFragment(PollFragment()) }
        rideElement.setOnClickListener { navigatorListener.replaceFragment(TransportFragment()) }
    }

}

