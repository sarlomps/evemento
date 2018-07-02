package com.hellfish.evemento.event.task

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import com.hellfish.evemento.EventViewModel
import com.hellfish.evemento.R
import com.hellfish.evemento.R.string.title_fragment_task_list
import com.hellfish.evemento.NavigatorFragment
import com.hellfish.evemento.SessionManager
import kotlinx.android.synthetic.main.fragment_task_list.*
import kotlinx.android.synthetic.main.fragment_task_list_item_add.view.*

class TaskListFragment : NavigatorFragment() {

    override val titleId = title_fragment_task_list
    lateinit var eventViewModel: EventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)
        //eventViewModel.loadComments { _ -> showToast(R.string.errorLoadingTasks) }
        eventViewModel.tasks.observe(this, Observer { tasks ->
            tasks?.let { taskRecyclerView.adapter = TaskListAdapter(tasks, editTaskItem()) }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskListAdd.setOnClickListener { itemDialogBehaviour() }

        if(eventViewModel.selected()!!.user == SessionManager.getCurrentUser()!!.userId) taskListAdd.visibility = View.VISIBLE
        else taskListAdd.visibility = View.GONE

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        eventViewModel.add(TaskItem("","Much wow, much fun!", "Ringo"))

        taskRecyclerView.apply { layoutManager = LinearLayoutManager(context) }
    }

    private fun itemDialogBehaviour() {
        val input = activity!!.layoutInflater.inflate(R.layout.fragment_task_list_item_add, null)

        addPossibleResponsiblesTo(input)
        createAlertDialog(R.string.addTask,
                input,
                Pair(R.string.accept, { dialog, _ ->
                    addItemIfCorrect(
                            input.addTaskDescription.text.toString(),
                            input.addTaskResponsible.selectedItem.toString(),
                            dialog) }),
                Pair(R.string.cancel, { dialog, _ -> dialog.cancel()})).show()
    }

    private fun addItemIfCorrect(description: String, responsible: String, dialog: DialogInterface) {
        if (description == "") dialog.dismiss()
        else {
            val item = TaskItem("", description, responsible)
            eventViewModel.add(item)
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
                val guest = eventGuests.map{ user -> user.displayName } as MutableList<String>
                guest.add(currentUser!!.displayName)
                guest
            }
        }

        val adapter = ArrayAdapter<String>(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                users
        )
        val spinner = addTaskItemView.findViewById<Spinner>(R.id.addTaskResponsible)
        spinner.adapter = adapter

    }

    private fun editTaskItem() = { item: TaskItem ->
        View.OnClickListener {
            if(eventViewModel.selected()?.user == SessionManager.getCurrentUser()!!.userId){
                editDeleteDialog(item)
            }else{
                itemResponsibility(item)
            }
        }
    }

    private fun editDeleteDialog(item: TaskItem){
        val editDeleteDialog = AlertDialog.Builder(context)
        editDeleteDialog.setPositiveButton("Edit") { _, _ ->
            editItem(item)
        }

        editDeleteDialog.setNegativeButton("Delete") { _, _ ->
            eventViewModel.remove(item)
        }

        editDeleteDialog.show()
    }

    private fun editItem(item: TaskItem){
        val editItemDialog = AlertDialog.Builder(context)
        editItemDialog.setTitle("Modify Item")

        var editTextView = EditText(context)
        editTextView.text = SpannableStringBuilder(item.description)
        editItemDialog.setView(editTextView)

        editItemDialog.setPositiveButton("Ok"){ _, _ ->
            eventViewModel.edit(item.copy(description = editTextView.text.toString()))
        }

        editItemDialog.setNegativeButton("Cancel"){ dialog, _ ->
            dialog.dismiss()
        }

        editItemDialog.show()
    }

    private fun itemResponsibility(item: TaskItem){
        val takeOwnershipQuestionDialog = AlertDialog.Builder(context)
        if(item.responsible == "No one in charge"){
            takeOwnershipQuestionDialog.setTitle("Do you want to be in charge?")
            takeOwnershipQuestionDialog.setNegativeButton("No"){ dialog, _ ->
                dialog.dismiss()
            }
            takeOwnershipQuestionDialog.setPositiveButton("Yes"){ _, _ ->
                eventViewModel.edit(item.copy(responsible = SessionManager.getCurrentUser()!!.displayName))
            }
        }else{
            takeOwnershipQuestionDialog.setTitle("Someone is already in charge")
            takeOwnershipQuestionDialog.setPositiveButton("Ok"){ dialog, _ ->
                dialog.dismiss()
            }
        }
        takeOwnershipQuestionDialog.show()
    }

}