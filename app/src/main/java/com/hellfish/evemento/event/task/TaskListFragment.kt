package com.hellfish.evemento.event.task

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import com.google.gson.internal.bind.MapTypeAdapterFactory
import com.hellfish.evemento.EventViewModel
import com.hellfish.evemento.R
import com.hellfish.evemento.R.string.title_fragment_task_list
import com.hellfish.evemento.NavigatorFragment
import com.hellfish.evemento.SessionManager
import kotlinx.android.synthetic.main.fragment_task_list.*
import kotlinx.android.synthetic.main.fragment_task_list_item_add.*
import kotlinx.android.synthetic.main.fragment_task_list_item_add.view.*
import kotlinx.android.synthetic.main.notification_template_lines_media.view.*

class TaskListFragment : NavigatorFragment() {

    override val titleId = title_fragment_task_list
    lateinit var eventViewModel: EventViewModel
    lateinit var createDialog: AlertDialog
    lateinit var createDialogInput: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)
        eventViewModel.loadGuests { _ -> showToast(R.string.errorLoadingGuests) }
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

        taskListAdd.setOnClickListener {
            createDialogInput.addTaskDescription.setText("")
            addPossibleResponsiblesTo()
            createDialog.run {
                show()
                createDialogInput.addTaskDescriptionLayout.error = null
                getButton(Dialog.BUTTON_NEUTRAL).visibility = View.GONE
                getButton(Dialog.BUTTON_POSITIVE).let {
                    it.text = getString(R.string.accept)
                    it.setOnClickListener { addItemIfCorrect() }
                }
            }
        }

        if(eventViewModel.selected()!!.user == SessionManager.getCurrentUser()!!.userId) taskListAdd.visibility = View.VISIBLE
        else taskListAdd.visibility = View.GONE

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        createDialogInput = activity!!.layoutInflater.inflate(R.layout.fragment_task_list_item_add, null)
        createDialog = createAlertDialog(R.string.addTask,
                createDialogInput,
                Pair(R.string.cancel, { _, _ -> Unit }),
                Pair(R.string.cancel, { _, _ -> Unit }),
                Pair(R.string.delete, { _, _ -> Unit }))

        eventViewModel.add(TaskItem("","Much wow, much fun!", "Ringo"))

        taskRecyclerView.apply { layoutManager = LinearLayoutManager(context) }
    }

    private fun addItemIfCorrect() {
        validatingTask {
            eventViewModel.add(TaskItem("", createDialogInput.addTaskDescription.text.toString(), createDialogInput.addTaskResponsible.selectedItem.toString()))
            createDialog.cancel()
        }
    }

    private fun validatingTask(action: () -> Unit) {
        validateTextInput(createDialogInput.addTaskDescriptionLayout, createDialogInput.addTaskDescription, getString(R.string.taskValidation))
        if (createDialogInput.addTaskDescription.text.isNotEmpty()) action()
    }

    private fun addPossibleResponsiblesTo(first: String? = null) {
        val users: MutableList<String?> = if (first != null) mutableListOf(first) else mutableListOf()
        users.add("No one in charge")
        users.add(SessionManager.getCurrentUser()!!.displayName)

        users.addAll(eventViewModel.guests.value!!
                .filterNot { user -> user.displayName == first ?: ""}
                .map { user -> user.displayName } as MutableList<String>)

        createDialogInput.addTaskResponsible.adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, users.distinct())
    }

    private fun editTaskItem() = { task: TaskItem ->
        View.OnClickListener {
            if(eventViewModel.selected()?.user == SessionManager.getCurrentUser()!!.userId) editItem(task)
            else itemResponsibility(task)
        }
    }

    private fun editItem(task: TaskItem) {
        createDialogInput.addTaskDescription.setText(task.description)
        val first = createDialogInput.addTaskResponsible.selectedItem.toString()
        addPossibleResponsiblesTo(first)
        createDialog.run {
            show()
            createDialogInput.addTaskDescriptionLayout.error = null
            getButton(Dialog.BUTTON_NEUTRAL).run {
                visibility = View.VISIBLE
                setOnClickListener { withConfirmationDialog(R.string.deleteTaskConfirmation) {
                    eventViewModel.remove(task); createDialog.cancel() }
                }
            }
            getButton(Dialog.BUTTON_POSITIVE).run {
                text = getString(R.string.accept)
                setOnClickListener {
                    validatingTask {
                        eventViewModel.edit(task.copy(description = createDialogInput.addTaskDescription.text.toString(),
                                                      responsible = SessionManager.getCurrentUser()!!.displayName))
                        createDialog.cancel()
                    }
                }
            }
        }
    }

    private fun itemResponsibility(item: TaskItem){
        val takeOwnershipQuestionDialog = AlertDialog.Builder(context!!)
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