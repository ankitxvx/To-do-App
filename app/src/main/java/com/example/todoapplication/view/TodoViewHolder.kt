package com.example.todoapplication.view
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapplication.R

class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val taskData = itemView.findViewById<TextView>(R.id.checkBox)
    val card = itemView.findViewById<CardView>(R.id.card_view)
}
