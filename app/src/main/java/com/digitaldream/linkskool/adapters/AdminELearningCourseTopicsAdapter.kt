package com.digitaldream.linkskool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R

class AdminELearningCourseTopicsAdapter:
    RecyclerView.Adapter<AdminELearningCourseTopicsAdapter.ContentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course_topics,
            parent, false)

        return ContentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }


    inner class ContentViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
}