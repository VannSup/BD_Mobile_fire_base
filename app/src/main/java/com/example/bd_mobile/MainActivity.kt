package com.example.bd_mobile

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var adapter: TaskAdapter
    private val database = FirebaseDatabase.getInstance()
    private val taskReference = database.getReference("Tasks")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRecyclerView()
        retrieveData()
        setupActionButton()
    }

    private fun initRecyclerView() {
        task_recycler_view.layoutManager = LinearLayoutManager(this)
        adapter = TaskAdapter()
        adapter.onTaskClickListener = this::onTaskClickListener
        adapter.onLongTaskClickListener = this::onLongTaskClickListener
        task_recycler_view.adapter = adapter
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val taskToRemove = adapter.getTaskAt(viewHolder.adapterPosition)
                adapter.removeAt(viewHolder.adapterPosition)
                taskReference.child(taskToRemove.firebaseId).removeValue()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(task_recycler_view)
    }

    private fun retrieveData() {
        taskReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.d("FirebaseError", error.message)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val taskList = mutableListOf<Task>()
                val taskMap = dataSnapshot.value as? HashMap<*, *>
                taskMap?.map { entry ->
                    val task = entry.value as HashMap<*, *>
                    val id = entry.key as String
                    val name = task["name"] as String
                    val isChecked = task["isChecked"] as Boolean
                    taskList.add(Task(id, name, isChecked))
                }
                setupRecyclerView(taskList)
            }

        })
    }

    private fun onTaskClickListener(task: Task, position: Int) {
        task.isChecked = !task.isChecked
        val updatedTaskMap: MutableMap<String, Any> = HashMap()
        updatedTaskMap["isChecked"] = task.isChecked
        taskReference.child(task.firebaseId).updateChildren(updatedTaskMap)
        adapter.notifyItemChanged(position)
    }

    private fun onLongTaskClickListener(task: Task, position: Int) {
        MaterialDialog(this).show {
            input(prefill = task.name) { _, text ->
                task.name = text.toString()
                val updatedTaskMap: MutableMap<String, Any> = HashMap()
                updatedTaskMap["name"] = text.toString()
                taskReference.child(task.firebaseId).updateChildren(updatedTaskMap)
                adapter.notifyItemChanged(position)
            }
        }

    }

    private fun setupRecyclerView(taskList: MutableList<Task>) {
        adapter.setTaskList(taskList)
    }

    private fun setupActionButton() {
        add_task_button.setOnClickListener {
            MaterialDialog(this).show {
                input { _, text ->
                    val task = HashMap<String, Any>()
                    task["name"] = text.toString()
                    task["isChecked"] = false
                    val taskRef = taskReference.push()
                    taskRef.setValue(task)
                    val id = taskRef.key
                    val name = task["name"] as String
                    val isChecked = task["isChecked"] as Boolean
                    id?.let { it1 -> Task(it1, name, isChecked) }?.let { it2 ->
                        adapter.addItem(it2)
                    }
                }
                positiveButton(R.string.add)
                title(R.string.add_title)
            }
        }
    }
}