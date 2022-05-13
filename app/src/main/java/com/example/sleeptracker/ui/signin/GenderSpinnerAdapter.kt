package com.example.sleeptracker.ui.signin

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.sleeptracker.R

class GenderSpinnerAdapter(private val ctx : Context?, private val items: List<String>) : BaseAdapter() {
    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v : View = convertView ?: LayoutInflater.from(ctx).inflate(R.layout.my_spinner_item,parent,false)
        val textView : TextView = v.findViewById(R.id.spinner_text)
        textView.text = items[position]
        if (position == 0){
            textView.isEnabled = false
            textView.setTextColor(Color.parseColor("#999999"))
        }else{
            textView.isEnabled = true
            textView.setTextColor(Color.parseColor("#000000"))
        }
        return v
    }

}