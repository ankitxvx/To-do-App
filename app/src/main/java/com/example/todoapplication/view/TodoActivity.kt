package com.example.todoapplication.view
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapplication.adapter.MyAdapter
import com.example.todoapplication.R
import com.example.todoapplication.data.local.DBHandler
import com.example.todoapplication.model.TodoData
import com.example.todoapplication.utilities.log
import com.example.todoapplication.utilities.toast


class TodoActivity : AppCompatActivity () {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter
    private val db = DBHandler(this)
    private lateinit var todoList: MutableList<TodoData>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_main)
        todoList = db.getAllTask()
        recyclerView = findViewById(R.id.recycler_view)
        adapter = MyAdapter(this, todoList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        val addButton: Button = findViewById(R.id.btnAddItem)
        addButton.setOnClickListener {
            customDialog()
        }
        val deleteBtn: Button = findViewById(R.id.btnDeleteItem)
        deleteBtn.setOnClickListener{
            if(db.getSize() == 0){
                alertBox("Nothing to Delete")
            }
            else {
                alertBox()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        updateRecyclerView()
    }
    private fun updateRecyclerView() {
        val newTodoList = db.getAllTask()
        adapter.updateData(newTodoList)
        adapter.notifyDataSetChanged()
    }
    private fun alertBox(){
        var check: Boolean = false
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Warning!!")
        builder.setMessage("This action cannot be undone. Are you sure ?")

        builder.setPositiveButton("YES") { dialog, which ->
            deleteAllTask()
        }

        builder.setNegativeButton("NO") { dialog, which ->

        }

        builder.setNeutralButton("Cancel") { dialog, which ->
        }
        builder.show()
    }
    private fun alertBox(text: String){
        val builder = AlertDialog.Builder(this)
        builder.setMessage(text)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, id ->
                log("OK", "pressed")
            }
        val alert = builder.create()
        alert.show()
    }
    private fun deleteAllTask(){
        log("Initial Size of the Table: ", db.getSize().toString())
        db.deleteData()
        updateRecyclerView()
        log("Size of the Table: ", db.getSize().toString())
        toast(this, "All task delete successfully")
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun customDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.input)

        val btnSubmit: Button = dialog.findViewById(R.id.btnSubmit)
        val taskDataEditText: EditText = dialog.findViewById(R.id.task_data)
        val spinner: Spinner = dialog.findViewById(R.id.spinner)

        btnSubmit.setOnClickListener {
            val taskData = taskDataEditText.text.toString()
            val taskPriority = spinner.selectedItem.toString()
            if (taskData.trim().isEmpty()) {
                alertBox("Input task is empty")
            } else {
                val data = TodoData(taskPriority, taskData)
                todoList.add(data)
                addDataToDB(taskPriority, taskData)
            }
            updateRecyclerView()
            dialog.dismiss()
        }
        dialog.show()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun addDataToDB(taskPriority: String, taskText: String){
        val db = DBHandler(this)
        val todo = TodoData(taskPriority, taskText)
        db.addTask(todo)
        toast(this, "added successfully")
    }
}
