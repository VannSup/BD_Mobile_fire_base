package com.example.bd_mobile.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bd_mobile.R
import com.example.bd_mobile.data.model.Task
import com.example.bd_mobile.ui.widget.holder.TaskViewHolder
import java.util.*

class TaskAdapter : RecyclerView.Adapter<TaskViewHolder>() {

    private val taskList: MutableList<Task> = mutableListOf()
    lateinit var onTaskClickListener: (Task, Int) -> Unit
    lateinit var onLongTaskClickListener: (Task, Int) -> Unit
    private var sortMode = 0

    public var nameDescending = true
    public var checkedDescending = true
    public var creationDescending = true
    public var updateDescending = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.task_holder, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val taskDetail = taskList[position]
        holder.bindData(taskDetail, position, onTaskClickListener, onLongTaskClickListener)
    }

    fun addItem(task: Task) {
        this.taskList.add(task)
        notifyItemChanged(this.taskList.lastIndex)
    }

    fun removeAt(position: Int) {
        this.taskList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getTaskAt(position: Int): Task {
        return this.taskList[position]
    }

    fun search(searchRequest: String) {
        setTaskList(this.taskList.filter { it.name.contains(searchRequest) })
    }

    // region sort
    fun setTaskList(taskList: List<Task>) {
        this.taskList.clear()
        this.taskList.addAll(taskList)
        when (sortMode) {
            0 -> notifyDataSetChanged()
            1 -> sortListByName()
            2 -> sortListByChecked()
            3 -> sortListByCreation()
            4 -> sortListByUpdate()
        }
    }

    fun sortListByName() {
        nameDescending = !nameDescending
        if(nameDescending)
            this.taskList.sortByDescending { it.name.toUpperCase(Locale.getDefault()) }
        else
            this.taskList.sortBy { it.name.toUpperCase(Locale.getDefault()) }
        notifyDataSetChanged()
        sortMode = 1
    }

    fun sortListByChecked() {
        checkedDescending = !checkedDescending
        if(checkedDescending)
            this.taskList.sortByDescending { !it.isChecked }
        else
            this.taskList.sortBy { !it.isChecked }
        notifyDataSetChanged()
        sortMode = 2
    }

    fun sortListByCreation() {
        creationDescending = !creationDescending
        if(creationDescending)
            this.taskList.sortBy { it.createdAt }
        else
            this.taskList.sortByDescending { it.createdAt }
        notifyDataSetChanged()
        sortMode = 3
    }

    fun sortListByUpdate() {
        updateDescending = !updateDescending
        if(updateDescending)
            this.taskList.sortBy { it.updatedAt }
        else
            this.taskList.sortByDescending { it.updatedAt }
        notifyDataSetChanged()
        sortMode = 4
    }
    // endregion

}