package com.hellfish.evemento.event.time

import android.app.TimePickerDialog
import android.content.Context
import android.widget.TextView
import org.joda.time.DateTime
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

interface TimePickerDialogFactory {

    var timeFormatter: DateTimeFormatter

    fun createLinkedTimePickerDialogs(context: Context?,
                                      startDateView: TextView,
                                      endDateView: TextView,
                                      startTimeView: TextView,
                                      endTimeView: TextView): Pair<TimePickerDialog, TimePickerDialog> =
            Pair(createTimePickerDialog(context, createTimeListener(startDateView, endDateView, startTimeView, endText = endTimeView)),
                    createTimePickerDialog(context, createTimeListener(startDateView, endDateView, endTimeView, startText = startTimeView)))

    fun createTimeListener(startDateView: TextView,
                           endDateView: TextView,
                           textView: TextView,
                           startText: TextView? = null,
                           endText: TextView? = null) =
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                val localTime = LocalTime(hour, minute, 0)
                checkTimeConsistency(localTime, startDateView, endDateView, startText, endText)
                textView.text= timeFormatter.print(localTime)
            }

    fun checkTimeConsistency(localTime: LocalTime, startDateView: TextView, endDateView: TextView, startText: TextView?, endText: TextView?) {
        if (startDateView.text == endDateView.text){
            startText?.updateTimeIfAfter(localTime, timeFormatter)
            endText?.updateTimeIfBefore(localTime, timeFormatter)
        }

    }

    fun createTimePickerDialog(context: Context?, onTimeSetListener: TimePickerDialog.OnTimeSetListener?): TimePickerDialog = with(DateTime.now()) {
        return TimePickerDialog(context, onTimeSetListener, hourOfDay, minuteOfHour, true)
    }

    private fun TextView.updateTimeIfAfter(date: LocalTime, formatter: DateTimeFormatter) {
        if (formatter.parseLocalTime(text.toString()).isAfter(date)) text = formatter.print(date)
    }

    private fun TextView.updateTimeIfBefore(date: LocalTime, formatter: DateTimeFormatter) {
        if (formatter.parseLocalTime(text.toString()).isBefore(date)) text = formatter.print(date)
    }

    fun TimePickerDialog.updateTime(textView: TextView, formatter: DateTimeFormatter) = apply {
        val time = formatter.parseLocalTime(textView.text.toString())
        updateTime(time.hourOfDay, time.minuteOfHour)
    }

}