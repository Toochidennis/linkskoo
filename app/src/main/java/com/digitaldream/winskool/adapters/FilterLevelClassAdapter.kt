package com.digitaldream.winskool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.ClassNameTable
import com.digitaldream.winskool.models.LevelTable

class FilterLevelClassAdapter(
    private val sLevelList: MutableList<LevelTable>?,
    private val sClassList: MutableList<ClassNameTable>?,
    private val sOnItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<FilterLevelClassAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FilterLevelClassAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dialog_filter_level_class_item, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: FilterLevelClassAdapter.ViewHolder, position: Int) {
        if (sLevelList == null) {
            holder.name.text = sClassList!![position].className
        } else {
            holder.name.text = sLevelList[position].levelName
        }

    }

    override fun getItemCount(): Int {
        return sLevelList?.size ?: sClassList!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.level_name)

        init {
            itemView.setOnClickListener {
                sOnItemClickListener.onItemClick(adapterPosition)
            }
        }

    }
}