package com.digitaldream.linkskool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.models.ClassNameTable
import com.digitaldream.linkskool.models.TeachersTable
import com.digitaldream.linkskool.utils.FunctionUtils.capitaliseFirstLetter

class AdminELearningCourseOutlineAdapter(
    private val sClassList: MutableList<ClassNameTable>?,
    private val sTeacherList: MutableList<TeachersTable>?,
    private val sOnTagClick: OnTagItemListener,
) : RecyclerView.Adapter<AdminELearningCourseOutlineAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.bottom_sheet_vendor_account_names_item, parent, false
        )

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (sClassList == null) {
            val model = sTeacherList!![position]
            model.staffFullName = "${model.staffSurname} ${model.staffMiddlename}" +
                    " ${model.staffFirstname}"
            holder.itemName.text = capitaliseFirstLetter(model.staffFullName)
            holder.itemFirstLetter.text = model.staffFullName.substring(0, 1).uppercase()
        } else {
            val model = sClassList[position]
            if (model.className.isNotBlank()) {
                holder.itemName.text = model.className.toString()
                holder.itemFirstLetter.text = model.className.substring(0, 1).uppercase()
            }
        }

        sOnTagClick.onTagClick(holder)
    }


    override fun getItemCount() = sClassList?.size ?: sTeacherList!!.size


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.item_name)
        val itemTextLayout: LinearLayout = itemView.findViewById(R.id.layout_text)
        val itemImageLayout: LinearLayout = itemView.findViewById(R.id.item_image_layout)
        val itemFirstLetter: TextView = itemView.findViewById(R.id.item_first_letter)

    }

}

interface OnTagItemListener {
    fun onTagClick(holder: AdminELearningCourseOutlineAdapter.ViewHolder)
}