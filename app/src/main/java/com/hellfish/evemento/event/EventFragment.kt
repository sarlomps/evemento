package com.hellfish.evemento.event

import android.app.Activity.RESULT_OK
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import android.os.Build
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import com.google.android.gms.location.places.ui.PlaceAutocomplete.RESULT_ERROR
import com.hellfish.evemento.extensions.withDrawable
import com.squareup.picasso.Picasso
import com.squareup.picasso.Callback
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.EditText
import android.widget.PopupMenu
import com.hellfish.evemento.*
import com.hellfish.evemento.event.comment.CommentListFragment
import com.hellfish.evemento.event.guest.GuestListFragment
import kotlinx.android.synthetic.main.fragment_event.*
import org.joda.time.DateTime


class EventFragment : NavigatorFragment(), DateTimePickerDialogFactory {


    private lateinit var eventListViewModel: EventListViewModel
    private lateinit var eventViewModel: EventViewModel
    private var editing: Boolean = false

    private lateinit var imageDialogInput: EditText
    private lateinit var imageDialog: AlertDialog

    private lateinit var menu: PopupMenu

    private val autocompleteRequestCode = 42

    lateinit var dateTimeFormatter: DateTimeFormatter
    override lateinit var dateFormatter: DateTimeFormatter
    override lateinit var timeFormatter: DateTimeFormatter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dateTimeFormatter = DateTimeFormat.forPattern(getString(R.string.DateTimeFormat))
        dateFormatter = DateTimeFormat.forPattern(getString(R.string.DateFormat))
        timeFormatter = DateTimeFormat.forPattern(getString(R.string.TimeFormat))

        eventListViewModel = ViewModelProviders.of(activity!!).get(EventListViewModel::class.java)
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
        imageDialogInput = EditText(activity)
        imageDialog = createImageDialog()
        return EventLayout(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTextInputWatchers()
        setMenuListener()
        setImageListener()
        setLocationListener()
        setDateTimeListeners()
        setListsListeners()

        if (eventViewModel.selected() != null) {
            if (eventViewModel.selected()?.user == SessionManager.getCurrentUser()?.userId) {
                eventFab.visibility = View.VISIBLE
                eventMenu.visibility = View.VISIBLE
                editing = savedInstanceState?.getBoolean("editing") ?: false
            } else {
                eventFab.visibility = View.GONE
                eventMenu.visibility = View.GONE
                editing = false
            }
            decideViewMode()
        } else {
            editing = true
            setEditDateTimeFieldsToday()
            setViewMode(null, R.drawable.ic_check_white_24dp) { validatingEvent { validatedEvent -> upsertEvent(validatedEvent) } }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        loadImage(imageUrl.text.toString())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("editing", editing)
    }

    override fun setupToolbar() {
        navigatorListener.setCustomToolbar(eventToolbar, homeEnabled = false)
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
                { validatingEvent { validatedEvent -> upsertEvent(validatedEvent.copy(eventId = eventViewModel.selected()?.eventId!!))} }
        )
        else setViewMode(null, R.drawable.ic_edit_white_24dp) { toggleViewMode() }
    }

    private fun upsertEvent(event: Event) {
        if (event.eventId != "") NetworkManager.updateEvent(event) { updatedEvent, errorMessage ->
            updatedEvent?.let { eventViewModel.select(updatedEvent); return@updateEvent }
            showToast(errorMessage ?: R.string.network_unknown_error)
        }
        else NetworkManager.pushEvent(event) { newEventId, errorMessage ->
            newEventId?.let {
                val newEvent = event.copy(eventId = newEventId)
                eventViewModel.select(newEvent)
                eventListViewModel.addEvent(newEvent)
                return@pushEvent
            }
            showToast(errorMessage ?: R.string.network_unknown_error)
        }
    }

    private fun setViewMode(backPressedListener: OnBackPressedListener?, drawable: Int, onClickListener: () -> Unit) = (view as EventLayout).run {
        mode(editing)
        navigatorListener.onBackPressedListener = backPressedListener
        eventFab.withDrawable(drawable).setOnClickListener { onClickListener() }
    }

    private fun validatingEvent(action: (Event) -> Unit) {
        validateTextInput(eventTitleLayout, eventTitle, getString(R.string.titleValidation))
        validateTextInput(locationElementLayout, locationElement, getString(R.string.locationValidation))

        if (eventTitle.text.isNotEmpty() && locationElement.text.isNotEmpty()) {
            toggleViewMode()
            val event = (view as EventLayout).event(dateTimeFormatter)
            action(event)
        }
    }

    private fun setTextInputWatchers() {
        eventTitle.addTextChangedListener(textInputValidator { validateTextInput(eventTitleLayout, eventTitle, getString(R.string.titleValidation)) })
        locationElement.addTextChangedListener(textInputValidator { validateTextInput(locationElementLayout, locationElement, getString(R.string.locationValidation)) })
    }

    private fun setMenuListener() {
        menu = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) PopupMenu(activity, eventMenuContainer, Gravity.NO_GRAVITY, R.attr.actionOverflowMenuStyle, 0)
               else PopupMenu(activity, eventMenuContainer)
        menu.inflate(R.menu.event_menu)
        menu.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.deleteEvent -> withConfirmationDialog(R.string.deleteEventConfirmation) { deleteEvent() }
                else -> Unit
            }
            true
        }
        eventMenu.setOnClickListener {
            menu.show()
        }
    }

    private fun deleteEvent() {
        NetworkManager.deleteEvent(eventViewModel.selected()!!.eventId) { success, errorMessage ->
            if (success) {
                eventListViewModel.removeEvent(eventViewModel.selected()!!){ error -> error?.let { showToast(error) } }
                eventViewModel.select(null)
                fragmentManager?.popBackStack()
            }
            else showToast(errorMessage ?: R.string.errorDeleteEvent)
        }
    }

    private fun textInputValidator(validation: () -> Unit) = object: TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { validation() }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}

    }

    private fun validateTextInput(textInputLayout: TextInputLayout, textInputEditText: TextInputEditText, errorMessage: String) {
        if (textInputEditText.text.toString() == "") textInputLayout.error = errorMessage
        else textInputLayout.error = null
    }

    private fun setImageListener() = eventImage.setOnClickListener {
        imageDialogInput.setText(imageUrl.text)
        imageDialog.show()
    }

    private fun loadImage(url: String?) {
        imageUrl.text = url
        eventMenuContainer.bringToFront()
        if (url != "") Picasso.get().load(url).fit().into(eventImage, object: Callback {
            override fun onError(e: java.lang.Exception?) { showToast(R.string.errorLoadingImage); toolbarScrim.visibility = View.GONE }
            override fun onSuccess() { toolbarScrim.visibility = View.VISIBLE }
        })
        else {
            toolbarScrim.visibility = View.GONE
            eventImage.setImageResource(0)
        }
    }

    private fun createImageDialog(): AlertDialog {
        return createAlertDialog(R.string.imageDialogTitle,
                imageDialogInput,
                Pair(R.string.accept, { _, _ -> loadImage(imageDialogInput.text.toString()) }),
                Pair(R.string.cancel, { _, _ -> Unit }),
                Pair(R.string.removeImage, { _, _ -> loadImage("") }))
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
        guestElement.setOnClickListener { navigatorListener.replaceFragment(GuestListFragment()) }
        taskElement.setOnClickListener { navigatorListener.replaceFragment(TaskListFragment()) }
        pollElement.setOnClickListener { navigatorListener.replaceFragment(PollFragment()) }
        rideElement.setOnClickListener { navigatorListener.replaceFragment(TransportFragment()) }
        commentElement.setOnClickListener { navigatorListener.replaceFragment(CommentListFragment()) }
    }

}



