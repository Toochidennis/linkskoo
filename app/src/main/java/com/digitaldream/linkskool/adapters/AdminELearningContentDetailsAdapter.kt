package com.digitaldream.linkskool.adapters

import android.widget.ImageView
import android.widget.TextView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.models.AttachmentModel

class AdminELearningContentDetailsAdapter {
    fun filesAdapter(
        itemList: MutableList<AttachmentModel>
    ) {

        GenericAdapter(
            itemList,
            R.layout.item_file_layout,
            bindItem = { itemView, model, _ ->
                val fileImageView: ImageView = itemView.findViewById(R.id.fileImage)
                val fileTxt: TextView = itemView.findViewById(R.id.fileTxt)

            }
        ) {

        }

    }

    private fun loadImage(model: AttachmentModel, imageView: ImageView){

    }
}