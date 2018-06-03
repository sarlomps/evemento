package com.hellfish.evemento.event.time

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.TextView
import java.util.*

interface DateTimePickerDialogBuilder {

    fun buildDateListener(textView: TextView) = DatePickerDialog.OnDateSetListener { _, year, month, day ->
        textView.text= String.format("%d/%d/%d", day, month, year)
    }

    fun buildDatePickerDialog(context: Context?, calendar: Calendar, textView: TextView) =
            DatePickerDialog(context, buildDateListener(textView), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

    fun buildTimeListener(textView: TextView) = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
        textView.text= String.format("%d:%d", hour, minute)
    }

    fun buildTimePickerDialog(context: Context?, calendar: Calendar, textView: TextView) =
            TimePickerDialog(context, buildTimeListener(textView), calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true)

}