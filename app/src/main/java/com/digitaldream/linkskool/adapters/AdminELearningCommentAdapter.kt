package com.digitaldream.linkskool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.models.CommentModel
import de.hdodenhof.circleimageview.CircleImageView

class AdminELearningCommentAdapter(
    val itemList: MutableList<CommentModel>
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

        fun bind(commentModel: CommentModel) {
            authorNameTxt.text = commentModel.authorName
            dateTxt.text = commentModel.date
            commentTxt.text = commentModel.commentText
        }
    }
}