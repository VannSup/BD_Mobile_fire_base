package com.example.bd_mobile

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val cardView: CardView = itemView.findViewById(R.id.task_holder_card)
    private val name: TextView = itemView.findViewById(R.id.task_holder_name)
    private val isChecked: ImageView = itemView.findViewById(R.id.task_holder_checked)

    fun bindData(
        task: Task,
        position: Int,
        listener: (Task, Int) -> Unit,
        longListener: (Task, Int) -> Unit
    ) {
        cardView.setOnClickListener { listener(task, position) }
        cardView.setOnLongClickListener {
            longListener(task, position)
            return@setOnLongClickListener true
        }
        name.text = task.name
        if (task.isChecked) {
            isChecked.show()
        } else {
            isChecked.hide()
        }
    }

}