package com.example.bd_mobile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter : RecyclerView.Adapter<TaskViewHolder>() {

    private val taskList: MutableList<Task> = mutableListOf()
    lateinit var onTaskClickListener: (Task, Int) -> Unit
    lateinit var onLongTaskClickListener: (Task, Int) -> Unit
    private var sortMode = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.task_holder, parent, false))
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val taskDetail = taskList[position]
        holder.bindData(taskDetail, position, onTaskClickListener, onLongTaskClickListener)
    }

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
        this.taskList.sortBy { it.name.toUpperCase() }
        notifyDataSetChanged()
        sortMode = 1
    }

    fun sortListByChecked() {
        this.taskList.sortBy { !it.isChecked }
        notifyDataSetChanged()
        sortMode = 2
    }

    fun sortListByCreation() {
        this.taskList.sortByDescending { it.createdAt }
        notifyDataSetChanged()
        sortMode = 3
    }

    fun sortListByUpdate() {
        this.taskList.sortByDescending { it.updatedAt }
        notifyDataSetChanged()
        sortMode = 4
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

}