package com.hellfish.evemento.event.time

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.TextView
import org.joda.time.DateTime
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

interface DateTimePickerDialogFactory {

    val onlyDateFormatter: DateTimeFormatter
        get() = DateTimeFormat.forPattern("dd/MM/yyyy")

    val onlyTimeFormatter: DateTimeFormatter
        get() = DateTimeFormat.forPattern("HH:mm")

    fun createLinkedDatePickerDialogs(context: Context?, startDateView: TextView, endDateView: TextView): Pair<DatePickerDialog, DatePickerDialog> =
        Pair(createDatePickerDialog(context, createDateListener(startDateView, endText = endDateView)),
                createDatePickerDialog(context, createDateListener(endDateView, startText = startDateView)))

    fun createDateListener(textView: TextView, startText: TextView? = null, endText: TextView? = null) =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                val date = DateTime(year, month + 1, day, 0, 0, 0)
                startText?.updateDateIfAfter(date, onlyDateFormatter)
                endText?.updateDateIfBefore(date, onlyDateFormatter)
                textView.text= onlyDateFormatter.print(date)
            }

    fun createDatePickerDialog(context: Context?, onDateSetListener: DatePickerDialog.OnDateSetListener?): DatePickerDialog = with(DateTime.now()) {
        return DatePickerDialog(context, onDateSetListener, year, monthOfYear, dayOfMonth)
    }

    fun createTimeListener(textView: TextView) = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
        val date = LocalTime(hour, minute, 0)
        textView.text= onlyTimeFormatter.print(date)
    }

    fun createTimePickerDialog(context: Context?, textView: TextView): TimePickerDialog = with(DateTime.now()) {
        return TimePickerDialog(context, createTimeListener(textView), hourOfDay, minuteOfHour, true)
    }

    private fun TextView.updateDateIfAfter(date: DateTime, formatter: DateTimeFormatter) {
        if (formatter.parseDateTime(text.toString()).isAfter(date)) text = formatter.print(date)
    }

    private fun TextView.updateDateIfBefore(date: DateTime, formatter: DateTimeFormatter) {
        if (formatter.parseDateTime(text.toString()).isBefore(date)) text = formatter.print(date)
    }

    fun DatePickerDialog.updateDate(textView: TextView, formatter: DateTimeFormatter) = apply {
        val date = formatter.parseDateTime(textView.text.toString())
        updateDate(date.year, date.monthOfYear -1, date.dayOfMonth)
    }
}


