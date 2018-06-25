package com.hellfish.evemento.event.task

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellfish.evemento.R
import com.hellfish.evemento.R.string.title_fragment_task_list
import com.hellfish.evemento.NavigatorFragment
import kotlinx.android.synthetic.main.fragment_task_list.*
import kotlinx.android.synthetic.main.fragment_task_list_item_add.view.*

class TaskListFragment : NavigatorFragment() {

    override val titleId = title_fragment_task_list
    val taskItems : MutableList<TaskItem> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(
                R.layout.fragment_task_list,
                container,
                false
        )

        val taskListAdd = view.findViewById<FloatingActionButton>(R.id.task_list_add)
        taskListAdd.setOnClickListener {
            itemDialogBehaviour()
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        taskItems.add(TaskItem("Much wow, much fun!", "Ringo"))

        task_recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = TaskListAdapter(taskItems)
        }
    }

    private fun itemDialogBehaviour() {
        val itemDialog = AlertDialog.Builder(context)
        itemDialog.setTitle("Add Item")

        val viewItemToModify = activity!!.layoutInflater.inflate(
                R.layout.fragment_task_list_item_add,
                null
        )
        itemDialog.setView(viewItemToModify)

        itemDialog.setPositiveButton("Ok") { dialog, which ->
            // Do something when user press the positive button
            addItemIfCorrect(
                    viewItemToModify.item_description.text.toString(),
                    dialog,
                    viewItemToModify.item_owner.text.toString()
            )
        }

        itemDialog.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }

        val dialog = itemDialog.create()
        dialog.show()
    }

    private fun addItemIfCorrect(description: String, dialog: DialogInterface, owner: String) {
        var owner1 = owner
        if (description.equals("Item_Description")) {
            dialog.dismiss()
        } else {
            if (owner1.equals("Owner")) {
                owner1 = ""
            }
            var item = TaskItem(
                    description,
                    owner1
            )
            taskItems.add(item)
        }
    }

}
