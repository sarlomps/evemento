package com.hellfish.evemento.event.time

import android.app.DatePickerDialog
import android.content.Context
import android.widget.TextView
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

interface DatePickerDialogFactory {

    val onlyDateFormatter: DateTimeFormatter
        get() = DateTimeFormat.forPattern("dd/MM/yyyy")

    fun createLinkedDatePickerDialogs(context: Context?, startDateView: TextView, endDateView: TextView): Pair<DatePickerDialog, DatePickerDialog> =
        Pair(createDatePickerDialog(context, createDateListener(startDateView, endText = endDateView)),
                createDatePickerDialog(context, createDateListener(endDateView, startText = startDateView)))

    fun createDateListener(textView: TextView, startText: TextView? = null, endText: TextView? = null) =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                val date = LocalDate(year, month + 1, day)
                startText?.updateDateIfAfter(date, onlyDateFormatter)
                endText?.updateDateIfBefore(date, onlyDateFormatter)
                textView.text = onlyDateFormatter.print(date)
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

}


