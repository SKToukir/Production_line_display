package com.walton.productionlinedisplay.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.walton.productionlinedisplay.R

class ListAdapter(private val items: List<String>) : BaseAdapter() {

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.grid_item, parent, false)
        val textView = view.findViewById<TextView>(R.id.text_view)
        textView.text = items[position]
        return view
    }
}