package com.digitaldream.linkskool.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.StudentELearningActivity
import com.digitaldream.linkskool.models.ContentModel
import com.digitaldream.linkskool.utils.FunctionUtils
import com.digitaldream.linkskool.utils.FunctionUtils.formatDate2
import com.digitaldream.linkskool.utils.VolleyCallback

class StudentELearningClassWorkAdapter(
    private val itemList: MutableList<ContentModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_TOPIC = 1
        private const val VIEW_TYPE_ASSIGNMENT = 2
        private const val VIEW_TYPE_MATERIAL = 3
        private const val VIEW_TYPE_QUESTION = 4
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_TOPIC -> {
                val view = inflater.inflate(R.layout.item_student_topic_layout, parent, false)
                TopicViewHolder(view)
            }

            VIEW_TYPE_ASSIGNMENT -> {
                val view = inflater.inflate(R.layout.item_student_content_layout, parent, false)
                AssignmentViewHolder(view)
            }

            VIEW_TYPE_MATERIAL -> {
                val view = inflater.inflate(R.layout.item_student_content_layout, parent, false)
                MaterialViewHolder(view)
            }

            VIEW_TYPE_QUESTION -> {
                val view = inflater.inflate(R.layout.item_student_content_layout, parent, false)
                QuestionViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val content = itemList[position]

        when (holder) {
            is TopicViewHolder -> holder.bind(content)

            is AssignmentViewHolder -> holder.bind(content)

            is MaterialViewHolder -> holder.bind(content)

            is QuestionViewHolder -> holder.bind(content)
        }
    }

    override fun getItemCount() = itemList.size

    override fun getItemViewType(position: Int): Int {
        val viewType = itemList[position]

        return when (viewType.viewType) {
            "topic" -> VIEW_TYPE_TOPIC
            "assignment" -> VIEW_TYPE_ASSIGNMENT
            "material" -> VIEW_TYPE_MATERIAL
            "question" -> VIEW_TYPE_QUESTION
            else -> position
        }
    }

    inner class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val topicTxt: TextView = itemView.findViewById(R.id.topicTxt)

        fun bind(topic: ContentModel) {
            topicTxt.text = topic.title
        }
    }

    inner class AssignmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val descriptionTxt: TextView = itemView.findViewById(R.id.descriptionTxt)
        private val dateTxt: TextView = itemView.findViewById(R.id.dateTxt)

        fun bind(assignment: ContentModel) {
            descriptionTxt.text = assignment.title
            val date = formatDate2(assignment.date, "custom")
            "Posted $date".let { dateTxt.text = it }

            itemViewAction(itemView, assignment, "assignment")

        }
    }

    inner class MaterialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val materialImage: ImageView = itemView.findViewById(R.id.imageType)
        private val descriptionTxt: TextView = itemView.findViewById(R.id.descriptionTxt)
        private val dateTxt: TextView = itemView.findViewById(R.id.dateTxt)

        fun bind(material: ContentModel) {
            materialImage.setImageDrawable(
                ContextCompat.getDrawable(itemView.context, R.drawable.ic_material)
            )

            descriptionTxt.text = material.title
            val date = formatDate2(material.date, "custom")
            "Posted $date".let { dateTxt.text = it }

            itemViewAction(itemView, material, "material")

        }
    }

    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val materialImage: ImageView = itemView.findViewById(R.id.imageType)
        private val descriptionTxt: TextView = itemView.findViewById(R.id.descriptionTxt)
        private val dateTxt: TextView = itemView.findViewById(R.id.dateTxt)

        fun bind(question: ContentModel) {
            materialImage.setImageDrawable(
                ContextCompat.getDrawable(itemView.context, R.drawable.ic_question)
            )

            descriptionTxt.text = question.title
            val date = formatDate2(question.date, "custom")
            "Posted $date".let { dateTxt.text = it }

            itemViewAction(itemView, question, "question")
        }
    }


    private fun itemViewAction(itemView: View, contentModel: ContentModel, from: String) {
        itemView.setOnClickListener {
            viewContentDetails(itemView, contentModel, from)
        }
    }

    private fun viewContentDetails(
        itemView: View,
        contentModel: ContentModel,
        from: String
    ) {
        val url =
            "${itemView.context.getString(R.string.base_url)}/getContent.php?" +
                    "id=${contentModel.id}&type=${contentModel.type}"

        sendRequest(url, itemView) { response ->
            launchActivity(itemView, from, response)
        }
    }

    private fun sendRequest(
        url: String,
        itemView: View,
        onResponse: (String) -> Unit
    ) {
        FunctionUtils.sendRequestToServer(
            Request.Method.GET,
            url,
            itemView.context,
            null,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    onResponse(response)
                }

                override fun onError(error: VolleyError) {
                    Toast.makeText(
                        itemView.context, "Something went wrong please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun launchActivity(itemView: View, from: String, response: String) {
        itemView.context.startActivity(
            Intent(itemView.context, StudentELearningActivity::class.java)
                .putExtra("from", from)
                .putExtra("json", response)
        )
    }

}