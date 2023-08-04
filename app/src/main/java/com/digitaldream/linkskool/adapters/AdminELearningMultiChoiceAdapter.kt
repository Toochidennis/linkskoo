package com.digitaldream.linkskool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.models.MultipleChoiceOption

class AdminELearningMultiChoiceAdapter(
    private val itemList: MutableList<MultipleChoiceOption>
) : RecyclerView.Adapter<AdminELearningMultiChoiceAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout
                .fragment_admin_e_learning_multi_choice_item, parent, false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val options = itemList[position]
        holder.bind(options)

    }

    override fun getItemCount() = itemList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(option: MultipleChoiceOption) {

        }
    }
}