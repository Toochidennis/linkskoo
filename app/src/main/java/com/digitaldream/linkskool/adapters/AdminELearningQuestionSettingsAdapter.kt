package com.digitaldream.linkskool.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.models.TagModel

class AdminELearningQuestionSettingsAdapter(
    private val selectedItems: HashMap<String, String>,
    private val itemList: MutableList<TagModel>,
    private val button: Button,
) : RecyclerView.Adapter<AdminELearningQuestionSettingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_admin_e_learning_question_settings_class_item, parent, false
        )

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemModel = itemList[position]
        holder.bindItem(itemModel)

        button.setOnClickListener {
            if (!button.isSelected) {
                selectAll(holder)

                button.apply {
                    setBackgroundResource(R.drawable.ripple_effect10)
                    setTextColor(Color.WHITE)
                    isSelected = true
                }
            } else {
                deselectAll(holder)

                button.apply {
                    setBackgroundResource(R.drawable.ripple_effect6)
                    setTextColor(Color.BLACK)
                    isSelected = false
                }
            }
        }
    }


    override fun getItemCount() = itemList.size


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: Button = itemView.findViewById(R.id.itemName)


        fun bindItem(tag: TagModel) {
            itemName.text = tag.tagName
            itemView.isSelected = tag.isSelected

            if (itemView.isSelected) {
                buttonBackground(itemName, "select")
                selectedItems[tag.tagId] = tag.tagName
                if (selectedItems.size == itemList.size){
                    buttonBackground(button, "select")
                }
            } else {
                buttonBackground(itemName, "deselect")
            }


            itemView.setOnClickListener {
                tag.isSelected = !tag.isSelected
                itemView.isSelected = tag.isSelected

                if (itemView.isSelected) {
                    buttonBackground(itemName, "select")
                    selectedItems[tag.tagId] = tag.tagName
                } else {
                    if (tag.tagId.isNotEmpty() && selectedItems.contains(tag.tagId)) {
                        selectedItems.remove(tag.tagId)

                        buttonBackground(itemName, "deselect")
                        buttonBackground(button, "deselect")
                    }
                }
            }
        }
    }

    private fun selectAll(holder: ViewHolder) {
        selectedItems.clear()
        itemList.forEach { item ->
            item.isSelected = true
            buttonBackground(holder.itemName, "select")
            buttonBackground(button, "select")
            selectedItems[item.tagId] = item.tagName
        }
        notifyDataSetChanged()
    }

    private fun deselectAll(holder: ViewHolder) {
        itemList.forEach { item ->
            if (item.isSelected) {
                item.isSelected = false
                buttonBackground(holder.itemName, "deselect")
                buttonBackground(button, "deselect")
            }
        }
        selectedItems.clear()
        notifyDataSetChanged()
    }

    private fun buttonBackground(button: Button, from: String) {
        if (from == "select") {
            button.apply {
                setBackgroundResource(R.drawable.ripple_effect10)
                isSelected = true
                setTextColor(Color.WHITE)
            }
        } else {
            button.apply {
                setBackgroundResource(R.drawable.ripple_effect6)
                isSelected = false
                setTextColor(Color.BLACK)
            }
        }
    }
}
