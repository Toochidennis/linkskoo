package com.digitaldream.linkskool.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.models.TagModel
import com.digitaldream.linkskool.utils.FunctionUtils.flipAnimation

class AdminELearningCreateClassAdapter(
    private val context: Context,
    private val selectedItems: HashMap<String, String>,
    private val itemList: MutableList<TagModel>,
    private val button: Button,
) : RecyclerView.Adapter<AdminELearningCreateClassAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.bottom_sheet_vendor_account_names_item, parent, false
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
        private val itemName: TextView = itemView.findViewById(R.id.item_name)
        val itemTextLayout: LinearLayout = itemView.findViewById(R.id.layout_text)
        val itemImageLayout: LinearLayout = itemView.findViewById(R.id.item_image_layout)
        private val itemFirstLetter: TextView = itemView.findViewById(R.id.item_first_letter)

        fun bindItem(tag: TagModel) {
            itemName.text = tag.tagName
            itemFirstLetter.text = tag.tagName.substring(0, 1).uppercase()
            itemView.isSelected = tag.isSelected

            if (itemView.isSelected) {
                flipAnimation(context, itemTextLayout, itemImageLayout, "right")
                selectedItems[tag.tagId] = tag.tagName

                if (selectedItems.size == itemList.size) {
                    button.apply {
                        setBackgroundResource(R.drawable.ripple_effect10)
                        setTextColor(Color.WHITE)
                        isSelected = true
                    }
                }
            } else {
                flipAnimation(context, itemTextLayout, itemImageLayout, "left")
            }

            itemView.setOnClickListener {
                tag.isSelected = !tag.isSelected
                itemView.isSelected = tag.isSelected

                if (itemView.isSelected) {
                    flipAnimation(context, itemTextLayout, itemImageLayout, "right")
                    selectedItems[tag.tagId] = tag.tagName
                } else {
                    if (tag.tagId.isNotEmpty() && selectedItems.contains(tag.tagId)) {
                        selectedItems.remove(tag.tagId)
                        flipAnimation(context, itemTextLayout, itemImageLayout, "left")

                        button.apply {
                            setBackgroundResource(R.drawable.ripple_effect6)
                            isSelected = false
                            setTextColor(Color.BLACK)
                        }
                    }
                }
            }
        }
    }

    private fun selectAll(holder: ViewHolder) {
        selectedItems.clear()
        itemList.forEach { item ->
            item.isSelected = true
            flipAnimation(context, holder.itemTextLayout, holder.itemImageLayout, "right")
            selectedItems[item.tagId] = item.tagName
        }
        notifyDataSetChanged()
    }

    private fun deselectAll(holder: ViewHolder) {
        itemList.forEach { item ->
            if (item.isSelected) {
                item.isSelected = false
                flipAnimation(context, holder.itemTextLayout, holder.itemImageLayout, "left")
            }
        }
        selectedItems.clear()
        notifyDataSetChanged()
    }
}