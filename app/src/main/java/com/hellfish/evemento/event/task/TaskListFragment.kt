package com.hellfish.evemento.event.task

import android.app.AlertDialog
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.hellfish.evemento.R
import com.hellfish.evemento.R.string.title_fragment_task_list
import com.hellfish.evemento.NavigatorFragment
import com.hellfish.evemento.event.poll.ClosedAnswersAdapter
import kotlinx.android.synthetic.main.fragment_poll_list.*

class TaskListFragment : NavigatorFragment() {

    override val titleId = title_fragment_task_list

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(
                R.layout.fragment_task_list,
                container,
                false
        )

        val taskItems = mutableListOf<TaskItem>(TaskItem("Much wow, much fun!", "Ringo"))

        val taskListAdd = view.findViewById<FloatingActionButton>(R.id.task_list_add)
        taskListAdd.setOnClickListener {
            val itemDialog = AlertDialog.Builder(context)
            itemDialog.setTitle("What")
            itemDialog.setMessage("TheFuck")

            val newItem = EditText(context)
            newItem.compoundPaddingStart
            newItem.compoundPaddingEnd

            itemDialog.setView(newItem)

            itemDialog.setPositiveButton("This?"){dialog, which ->
                // Do something when user press the positive button
                val item = TaskItem(newItem.text.toString(),"")
                taskItems.add(item)
            }

            itemDialog.setNegativeButton("is"){dialog, which ->
                // Display a negative button on alert dialog
            }

            val dialog = itemDialog.create()
            dialog.show()


        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = TaskListAdapter(taskItems)
        }
        return view
    }
}
