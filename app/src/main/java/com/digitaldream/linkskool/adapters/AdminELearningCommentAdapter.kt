package com.digitaldream.linkskool.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.models.CommentDataModel

class AdminELearningCommentAdapter(
    val itemList: MutableList<CommentDataModel>
) : RecyclerView.Adapter<AdminELearningCommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_comment_layout,
            parent, false
        )

        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val itemModel = itemList[position]

        holder.bind(itemModel)
    }

    override fun getItemCount() = itemList.size

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //  private val profileImage: CircleImageView = itemView.findViewById(R.id.profileImageView)
        private val authorNameTxt: TextView = itemView.findViewById(R.id.authorNameTxt)
        private val dateTxt: TextView = itemView.findViewById(R.id.dateTxt)
        private val commentTxt: TextView = itemView.findViewById(R.id.commentTxt)

        fun bind(sCommentDataModel: CommentDataModel) {
            authorNameTxt.text = sCommentDataModel.authorName
            dateTxt.text = sCommentDataModel.date
            commentTxt.text = sCommentDataModel.commentText

            setUpPopMenu(itemView, adapterPosition)
        }
    }


    private fun setUpPopMenu(itemView: View, position: Int) {
        itemView.setOnClickListener {
            PopupMenu(it.context, it, Gravity.END).apply {
                inflate(R.menu.delete_class_menu)

                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.delete_menu -> {
                            itemList.removeAt(position)

                            notifyDataSetChanged()
                            true
                        }

                        else -> false
                    }
                }

            }.show()

        }
    }
}