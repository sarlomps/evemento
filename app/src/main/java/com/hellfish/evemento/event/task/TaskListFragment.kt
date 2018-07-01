package com.hellfish.evemento.event.task

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.hellfish.evemento.EventViewModel
import com.hellfish.evemento.R
import com.hellfish.evemento.R.string.title_fragment_task_list
import com.hellfish.evemento.NavigatorFragment
import com.hellfish.evemento.SessionManager
import kotlinx.android.synthetic.main.fragment_poll_list.*
import kotlinx.android.synthetic.main.fragment_task_list.*
import kotlinx.android.synthetic.main.fragment_task_list_item_add.view.*

class TaskListFragment : NavigatorFragment() {

    override val titleId = title_fragment_task_list
    private val taskItems : MutableList<TaskItem> = mutableListOf()
    lateinit var eventViewModel: EventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)
        //TODO: Load Tasks
        eventViewModel.tasks.observe(
                this,
                Observer { items -> items?.let{task_recycler_view.adapter = TaskListAdapter(taskItems, editTaskItem()) }
                }
        )
    }

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
        }
    }

    private fun itemDialogBehaviour() {
        val itemDialog = AlertDialog.Builder(context)
        itemDialog.setTitle("Add Item")

        val viewItemToModify = activity!!.layoutInflater.inflate(
                R.layout.fragment_task_list_item_add,
                null
        )

        addPossibleResponsiblesTo(viewItemToModify)

        itemDialog.setView(viewItemToModify)

        itemDialog.setPositiveButton("Ok") { dialog, _ ->
            addItemIfCorrect(
                    viewItemToModify.add_item_description.text.toString(),
                    dialog,
                    viewItemToModify.add_item_responsible.selectedItem.toString()
            )
        }

        itemDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        val dialog = itemDialog.create()
        dialog.show()
    }

    private fun addItemIfCorrect(description: String, dialog: DialogInterface, responsible: String) {
        if (description == "") {
            dialog.dismiss()
        } else {
            val item = TaskItem(
                    description,
                    responsible
            )
            taskItems.add(item)
            refreshView()
        }
    }

    private fun addPossibleResponsiblesTo(addTaskItemView: View){
        val owner = eventViewModel.selected()?.user
        val eventGuests = eventViewModel.guests.value
        val users: MutableList<String?> = mutableListOf("No one in charge")

        val currentUser = SessionManager.getCurrentUser()
        if(eventGuests != null) {
            users += if(owner != currentUser?.userId){
                val guest = eventGuests.filter { user -> user.userId == currentUser?.userId }
                guest.map { user -> user.displayName } as MutableList<String>
            }else{
                eventGuests.map{ user -> user.displayName } as MutableList<String>
            }
        }

        val adapter = ArrayAdapter<String>(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                users
        )
        val spinner = addTaskItemView.findViewById<Spinner>(R.id.add_item_responsible)
        spinner.adapter = adapter

    }

    private fun editTaskItem() = { item: TaskItem ->
        View.OnClickListener {
            if(eventViewModel.selected()?.user == SessionManager.getCurrentUser()!!.userId){
//                editDeleteDialog(item)
                itemResponsibility(item)
            }else{
                itemResponsibility(item)
            }
        }
    }

    private fun editDeleteDialog(item: TaskItem){
        val editDeleteDialog = AlertDialog.Builder(context)
        editDeleteDialog.setPositiveButton("Edit") { _, _ ->
            //Edit item
            //view?.findViewById<FloatingActionButton>(R.id.task_list_add)?.callOnClick()
//            refreshView()
        }

        editDeleteDialog.setNegativeButton("Delete") { _, _ ->
            taskItems.remove(item)
            refreshView()
        }

        editDeleteDialog.show()
    }

    private fun itemResponsibility(item: TaskItem){
        val takeOwnershipQuestionDialog = AlertDialog.Builder(context)
        if(item.responsible == "No one in charge"){
            takeOwnershipQuestionDialog.setTitle("Do you want to be in charge?")
            takeOwnershipQuestionDialog.setNegativeButton("No"){ dialog, _ ->
                dialog.dismiss()
            }
            takeOwnershipQuestionDialog.setPositiveButton("Yes"){ _, _ ->
                item.responsible = SessionManager.getCurrentUser()!!.displayName
                refreshView()
            }
        }else{
            takeOwnershipQuestionDialog.setTitle("Someone is already in charge")
            takeOwnershipQuestionDialog.setPositiveButton("Ok"){ dialog, _ ->
                dialog.dismiss()
            }
        }
        takeOwnershipQuestionDialog.show()
    }

    private fun refreshView(){
        task_recycler_view.adapter.notifyDataSetChanged()
    }

}