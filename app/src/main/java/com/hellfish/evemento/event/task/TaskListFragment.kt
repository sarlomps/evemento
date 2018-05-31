package com.hellfish.evemento.event.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellfish.evemento.R
import com.hellfish.evemento.R.string.title_fragment_task_list
import com.hellfish.evemento.NavigatorFragment

class TaskListFragment : NavigatorFragment() {

    override val titleId = title_fragment_task_list

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_task_list, container, false)

}
