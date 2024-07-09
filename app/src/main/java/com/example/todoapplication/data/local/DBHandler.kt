package com.example.todoapplication.data.local

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.todoapplication.model.TodoData
import com.example.todoapplication.utilities.log
import java.time.Instant
import java.time.format.DateTimeFormatter


class DBHandler(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

        companion object{
            private val DB_NAME = "todo_database"
            private val DB_VERSION = 1
            private val TABLE_NAME = "todo_table"
            private val KEY_DATA = "task"
            private val KEY_PRIORITY  = "priority"
            private val KEY_TIMESTAMP = "time"
        }

    override fun onCreate(db: SQLiteDatabase?) {
        val createQuery = ("CREATE TABLE IF NOT EXISTS $TABLE_NAME(" +
                "$KEY_TIMESTAMP TEXT, " +
                "$KEY_DATA TEXT, " +
                "$KEY_PRIORITY TEXT);")
        db?.execSQL(createQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addTask(data: TodoData){
        val db = writableDatabase
        val values = ContentValues().apply {
            val timeStamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
            put(KEY_DATA, data.data)
            put(KEY_PRIORITY, data.priority)
            put(KEY_TIMESTAMP, timeStamp.toString())
        }
        db.insert(TABLE_NAME, null, values)
        log("Insert Query: ", "Successfully inserted")
        db.close()
    }

    fun getAllTask(): MutableList<TodoData>{
        val getQuery = ("SELECT * FROM $TABLE_NAME;")
        val datalist = mutableListOf<TodoData>()
        val db = readableDatabase
        val cursor = db.rawQuery(getQuery, null)
        cursor.use {
            if(it.moveToFirst()){
                do{
                    val task = it.getString(it.getColumnIndexOrThrow(KEY_DATA))
                    val priority = it.getString(it.getColumnIndexOrThrow(KEY_PRIORITY))

                    val tData = TodoData(priority, task)
                    datalist.add(tData)
                    log("item: ", task + "priority: " + priority)
                }while(it.moveToNext())
            }
        }
        return datalist
    }

    fun deleteData(){
        val deleteQuery = ("DELETE FROM $TABLE_NAME;")
        val db = writableDatabase
        db.execSQL(deleteQuery)
    }

    fun getSize(): Int{
        val query = "SELECT COUNT(*) FROM $TABLE_NAME;"
        val db = readableDatabase
        val count = DatabaseUtils.queryNumEntries(db, TABLE_NAME)
        db.close()
        return count.toInt()
    }

    fun deleteTask(taskName: String, taskPriority: String){
        val query = "DELETE FROM $TABLE_NAME WHERE $KEY_DATA = '$taskName' AND $KEY_PRIORITY = '$taskPriority';"
        val db = writableDatabase
        db.execSQL(query)
    }

    fun editTask(
        taskName: String,
        taskPriority: String,
        newTaskName: String,
        newTaskPriority: String)
    {
        val query = "UPDATE $TABLE_NAME SET $KEY_PRIORITY = '$newTaskPriority', $KEY_DATA = " +
                "'$newTaskName' WHERE $KEY_PRIORITY = '$taskPriority' AND $KEY_DATA = '$taskName';"
        val db = writableDatabase
        db.execSQL(query)
    }
}