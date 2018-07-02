package com.hellfish.evemento.event.task

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.hellfish.evemento.*
import com.hellfish.evemento.R.string.title_fragment_task_list
import kotlinx.android.synthetic.main.fragment_task_list.*
import kotlinx.android.synthetic.main.fragment_task_list_item_add.view.*

class TaskListFragment : NavigatorFragment() {

    override val titleId = title_fragment_task_list
    lateinit var eventViewModel: EventViewModel
    lateinit var createDialog: AlertDialog
    lateinit var createDialogInput: View
    lateinit var takeOwnershipQuestionDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)
        eventViewModel.loadGuests { _ -> showToast(R.string.errorLoadingTasks) }
        eventViewModel.loadTasks { _ -> showToast(R.string.errorLoadingTasks) }
        eventViewModel.tasks.observe(this, Observer { tasks ->
            tasks?.let { taskRecyclerView.adapter = TaskListAdapter(tasks, getString(R.string.noOneInCharge), editTaskItem()) }
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

        takeOwnershipQuestionDialog = AlertDialog.Builder(activity!!)
                .setPositiveButton(getString(R.string.yes)) { _, _ -> Unit }
                .setNegativeButton(getString(R.string.no)) { _, _ -> Unit }
                .create()

        taskRecyclerView.apply { layoutManager = LinearLayoutManager(context) }
    }

    private fun addItemIfCorrect() {
        validatingTask {
            val newTask = TaskItem("", createDialogInput.addTaskDescription.text.toString(), createDialogInput.addTaskResponsible.selectedItem.toString())
            NetworkManager.pushTask(eventViewModel.selected()!!.eventId, newTask) { id, errorMessage ->
                id?.let { eventViewModel.add(newTask.copy(taskId = id)); return@pushTask }
                showToast(errorMessage ?: R.string.api_error_pushing_data)
            }
            createDialog.cancel()
        }
    }

    private fun validatingTask(action: () -> Unit) {
        validateTextInput(createDialogInput.addTaskDescriptionLayout, createDialogInput.addTaskDescription, getString(R.string.taskValidation))
        if (createDialogInput.addTaskDescription.text.isNotEmpty()) action()
    }

    private fun addPossibleResponsiblesTo(first: String? = null) {
        val users: MutableList<String?> = if (first != null) mutableListOf(first) else mutableListOf()
        users.add(getString(R.string.noOneInCharge))
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
        val first = task.responsible
        addPossibleResponsiblesTo(first)
        createDialog.run {
            show()
            createDialogInput.addTaskDescriptionLayout.error = null
            getButton(Dialog.BUTTON_NEUTRAL).run {
                visibility = View.VISIBLE
                setOnClickListener {
                    withConfirmationDialog(R.string.deleteTaskConfirmation) {
                        NetworkManager.deleteTask(task) { success, errorMessage ->
                            if (success) eventViewModel.remove(task)
                            else showToast(errorMessage ?: R.string.api_error_deleting_data)
                            createDialog.cancel()
                        }
                    }
                }
            }
            getButton(Dialog.BUTTON_POSITIVE).run {
                text = getString(R.string.accept)
                setOnClickListener {
                    validatingTask {
                        editTask(task.copy(description = createDialogInput.addTaskDescription.text.toString(),
                                           responsible = createDialogInput.addTaskResponsible.selectedItem.toString()))
                        createDialog.cancel()
                    }
                }
            }
        }
    }

    private fun showOwnershipDialog(title: Int, acceptAction: () -> Unit, noVisibility: Int = View.VISIBLE, buttonText: Int = R.string.yes) {
        takeOwnershipQuestionDialog.run {
            setTitle(getString(title))
            show()
            getButton(Dialog.BUTTON_NEGATIVE).visibility = noVisibility
            getButton(Dialog.BUTTON_POSITIVE).let {
                it.text = getString(buttonText)
                it.setOnClickListener {
                    acceptAction()
                    this.cancel()
                }
            }
        }
    }

    private fun itemResponsibility(item: TaskItem){
        when(item.responsible) {
            getString(R.string.noOneInCharge) -> showOwnershipDialog(R.string.beInCharge, {
                editTask(item.copy(responsible = SessionManager.getCurrentUser()!!.displayName))
            })

            SessionManager.getCurrentUser()!!.displayName -> showOwnershipDialog(R.string.abandonTask, {
                editTask(item.copy(responsible = getString(R.string.noOneInCharge)))
            })

            else -> showOwnershipDialog(R.string.alreadyInCharge, { takeOwnershipQuestionDialog.cancel() }, View.GONE, R.string.ok)
        }
    }

    private fun editTask(task: TaskItem) {
        NetworkManager.updateTask(eventViewModel.selected()!!.eventId, task) { updated, erroMessage ->
            updated?.let { eventViewModel.edit(updated); return@updateTask }
            showToast(erroMessage ?: R.string.network_unknown_error)
        }
    }

}