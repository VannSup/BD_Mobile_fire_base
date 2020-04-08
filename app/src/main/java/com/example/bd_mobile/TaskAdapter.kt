package com.example.bd_mobile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter : RecyclerView.Adapter<TaskViewHolder>() {

    private val taskList: MutableList<Task> = mutableListOf()
    lateinit var onTaskClickListener: (Task, Int) -> Unit
    lateinit var onLongTaskClickListener: (Task, Int) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.task_holder, parent, false)
        return TaskViewHolder(v)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val taskDetail = taskList[position]
        holder.bindData(taskDetail, position, onTaskClickListener, onLongTaskClickListener)
    }

    fun setTaskList(songList: List<Task>) {
        this.taskList.clear()
        this.taskList.addAll(songList)
        notifyDataSetChanged()
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