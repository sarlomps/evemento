package com.hellfish.evemento.event.task

import android.content.Context
import android.support.v7.widget.CardView
import com.hellfish.evemento.RecyclerAdapter

class TaskListAdapter(private val taskItems: MutableList<TaskItem>) : RecyclerAdapter<CardView, TaskItem>(taskItems) {
    override fun layout(item: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun doOnItemOnBindViewHolder(view: CardView, item: TaskItem, context: Context) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        return taskItems.size
    }
}