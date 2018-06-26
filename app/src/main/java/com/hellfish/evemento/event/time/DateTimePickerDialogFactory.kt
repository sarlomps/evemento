package com.hellfish.evemento.event.time

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.TextView
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormatter

interface DateTimePickerDialogFactory {

    var dateFormatter: DateTimeFormatter
    var timeFormatter: DateTimeFormatter

    fun createLinkedDatePickerDialogs(context: Context?,
                                      startDateView: TextView,
                                      endDateView: TextView,
                                      startTimeView: TextView,
                                      endTimeView: TextView): Pair<DatePickerDialog, DatePickerDialog> =
            Pair(createDatePickerDialog(context, createDateListener(startDateView, startTimeView, endTimeView, endText = endDateView)),
                    createDatePickerDialog(context, createDateListener(endDateView, startTimeView, endTimeView, startText = startDateView)))

    fun createDateListener(textView: TextView,
                           startTimeView: TextView,
                           endTimeView: TextView,
                           startText: TextView? = null,
                           endText: TextView? = null) =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                val date = LocalDate(year, month + 1, day)
                startText?.updateDateIfAfter(date, dateFormatter)
                endText?.updateDateIfBefore(date, dateFormatter)
                textView.text = dateFormatter.print(date)
                checkTimeConsistency(startText ?: textView, endText ?: textView, startTimeView, endTimeView)
            }

    fun createDatePickerDialog(context: Context?, onDateSetListener: DatePickerDialog.OnDateSetListener?): DatePickerDialog = with(DateTime.now()) {
        return DatePickerDialog(context, onDateSetListener, year, monthOfYear, dayOfMonth)
    }

    private fun TextView.updateDateIfAfter(date: LocalDate, formatter: DateTimeFormatter) {
        if (formatter.parseLocalDate(text.toString()).isAfter(date)) text = formatter.print(date)
    }

    private fun TextView.updateDateIfBefore(date: LocalDate, formatter: DateTimeFormatter) {
        if (formatter.parseLocalDate(text.toString()).isBefore(date)) text = formatter.print(date)
    }

    fun DatePickerDialog.updateDate(textView: TextView, formatter: DateTimeFormatter) = apply {
        val date = formatter.parseLocalDate(textView.text.toString())
        updateDate(date.year, date.monthOfYear -1, date.dayOfMonth)
    }

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
                checkTimeConsistency(startDateView, endDateView, startText, endText, localTime)
                textView.text= timeFormatter.print(localTime)
            }

    fun checkTimeConsistency(startDateView: TextView, endDateView: TextView, startText: TextView?, endText: TextView?, localTime: LocalTime? = null) {
        if (startDateView.text == endDateView.text){
            startText?.updateTimeIfAfter(localTime ?: timeFormatter.parseLocalTime(endText?.text.toString()), timeFormatter)
            endText?.updateTimeIfBefore(localTime ?: timeFormatter.parseLocalTime(startText?.text.toString()), timeFormatter)
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