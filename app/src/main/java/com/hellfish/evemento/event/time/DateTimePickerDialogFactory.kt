package com.hellfish.evemento.event.time

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.TextView
import org.joda.time.DateTime
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormatter
import java.util.*
import org.joda.time.format.DateTimeFormat



interface DateTimePickerDialogFactory {

    val onlyDateFormatter: DateTimeFormatter
        get() = DateTimeFormat.forPattern("MM/dd/yyyy")

    val onlyTimeFormatter: DateTimeFormatter
        get() = DateTimeFormat.forPattern("HH:mm")


    fun createDateListener(textView: TextView) = DatePickerDialog.OnDateSetListener { _, year, month, day ->
        val date = DateTime(year, month, day, 0, 0, 0)
        textView.text= onlyDateFormatter.print(date)
    }

    fun createDatePickerDialog(context: Context?, calendar: Calendar, textView: TextView) =
            DatePickerDialog(context, createDateListener(textView), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

    fun createTimeListener(textView: TextView) = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
        val date = LocalTime(hour, minute, 0)
        textView.text= onlyTimeFormatter.print(date)
    }

    fun createTimePickerDialog(context: Context?, calendar: Calendar, textView: TextView) =
            TimePickerDialog(context, createTimeListener(textView), calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true)
    
}