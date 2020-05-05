package com.example.bd_mobile.ui.activity

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BasicGridItem
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.gridItems
import com.afollestad.materialdialogs.input.input
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.example.bd_mobile.R
import com.example.bd_mobile.utils.SwipeToDeleteCallback
import com.example.bd_mobile.ui.adapter.TaskAdapter
import com.example.bd_mobile.data.model.Task


class MainActivity : AppCompatActivity() {

    private lateinit var adapter: TaskAdapter
    private lateinit var searchView: SearchView
    private val database = FirebaseDatabase.getInstance()
    private val taskReference = database.getReference("Tasks")
    private var currentSearch = ""
    private var originalList = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRecyclerView()
        retrieveData()
        setupActionButton()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu);
        val manager = this.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu!!.findItem(R.id.search_item)
        val searchView = searchItem.actionView as SearchView
        this.searchView = searchView
        val searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_button) as ImageView
        searchIcon.setImageDrawable(ContextCompat.getDrawable(this,
            R.drawable.ic_search
        ))

        searchView.queryHint = "enter your search here"

        searchView.setSearchableInfo(manager.getSearchableInfo(this.componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    currentSearch = query
                    searchView.clearFocus()
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newSearch: String?): Boolean {
                return if (newSearch != null) {
                    adapter.setTaskList(originalList);
                    currentSearch = newSearch
                    adapter.search(newSearch)
                    true
                } else {
                    false
                }
            }
        })

        searchView.setOnCloseListener {
            currentSearch = ""
            adapter.setTaskList(originalList);
            return@setOnCloseListener false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val items = listOf(
            BasicGridItem(
                if(adapter.nameDescending)
                    R.drawable.ic_arrow_downward
                else
                    R.drawable.ic_arrow_upward
                ,"Name"),
            BasicGridItem(
                if(adapter.checkedDescending)
                    R.drawable.ic_arrow_downward
                else
                    R.drawable.ic_arrow_upward
                , "Checked"),
            BasicGridItem(
                if(adapter.creationDescending)
                    R.drawable.ic_arrow_downward
                else
                    R.drawable.ic_arrow_upward
                , "Creation"),
            BasicGridItem(
                if(adapter.updateDescending)
                    R.drawable.ic_arrow_downward
                else
                    R.drawable.ic_arrow_upward
                , "Update")
        )

        if (item.itemId == R.id.main_toolbar_filter_icon) {
            MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                gridItems(items) { _, _, item ->
                    when (item.title) {
                        "Name" -> {
                            adapter.sortListByName()
                        }
                        "Checked" -> {
                            adapter.sortListByChecked()
                        }
                        "Creation" -> {
                            adapter.sortListByCreation()
                        }
                        "Update" -> {
                            adapter.sortListByUpdate()
                        }
                    }
                }
                title(R.string.sort_by)
            }
        }
        return super.onOptionsItemSelected(item)
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
        taskReference.addValueEventListener(object : ValueEventListener {
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
                    val createdAt = task["createdAt"] as Long
                    val updatedAt = task["updatedAt"] as Long
                    taskList.add(
                        Task(
                            id,
                            name,
                            isChecked,
                            createdAt,
                            updatedAt
                        )
                    )
                }
                setupRecyclerView(taskList)
                originalList.clear()
                originalList.addAll(taskList)
            }

        })
    }

    private fun onTaskClickListener(task: Task, position: Int) {
        task.isChecked = !task.isChecked
        task.updatedAt = System.currentTimeMillis()
        val updatedTaskMap: MutableMap<String, Any> = HashMap()
        updatedTaskMap["isChecked"] = task.isChecked
        updatedTaskMap["updatedAt"] = task.updatedAt
        taskReference.child(task.firebaseId).updateChildren(updatedTaskMap)
        adapter.notifyItemChanged(position)
    }

    private fun onLongTaskClickListener(task: Task, position: Int) {
        MaterialDialog(this).show {
            input(prefill = task.name) { _, text ->
                task.name = text.toString()
                task.updatedAt = System.currentTimeMillis()
                val updatedTaskMap: MutableMap<String, Any> = HashMap()
                updatedTaskMap["name"] = text.toString()
                updatedTaskMap["updatedAt"] = task.updatedAt
                taskReference.child(task.firebaseId).updateChildren(updatedTaskMap)
                adapter.notifyItemChanged(position)
            }
            title(R.string.title_update_task)
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
                    task["createdAt"] = System.currentTimeMillis()
                    task["updatedAt"] = System.currentTimeMillis()
                    val taskRef = taskReference.push()
                    taskRef.setValue(task)
                    val id = taskRef.key
                    val name = task["name"] as String
                    val isChecked = task["isChecked"] as Boolean
                    val createdAt = task["createdAt"] as Long
                    val updatedAt = task["updatedAt"] as Long
                    id?.let { it1 ->
                        Task(
                            it1,
                            name,
                            isChecked,
                            createdAt,
                            updatedAt
                        )
                    }
                        ?.let { it2 ->
                            adapter.addItem(it2)
                        }
                }
                title(R.string.title_new_task)
            }
        }
    }
}