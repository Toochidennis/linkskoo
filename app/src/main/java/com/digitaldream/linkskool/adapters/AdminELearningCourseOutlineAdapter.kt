package com.digitaldream.linkskool.adapters

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.ELearningActivity
import com.digitaldream.linkskool.interfaces.ItemTouchHelperAdapter
import com.digitaldream.linkskool.models.ContentModel
import com.digitaldream.linkskool.utils.FunctionUtils.formatDate2
import java.util.Collections

class AdminELearningCourseOutlineAdapter(
    private val itemList: MutableList<ContentModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperAdapter {

    companion object {
        private const val VIEW_TYPE_TOPIC = 1
        private const val VIEW_TYPE_ASSIGNMENT = 2
        private const val VIEW_TYPE_MATERIAL = 3
        private const val VIEW_TYPE_QUESTION = 4
    }

    private val viewHolderList = mutableListOf<RecyclerView.ViewHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_TOPIC -> {
                val view = inflater.inflate(R.layout.item_course_topic, parent, false)
                TopicViewHolder(view)
            }

            VIEW_TYPE_ASSIGNMENT -> {
                val view = inflater.inflate(R.layout.item_course_fragment, parent, false)
                AssignmentViewHolder(view)
            }

            VIEW_TYPE_MATERIAL -> {
                val view = inflater.inflate(R.layout.item_course_fragment, parent, false)
                MaterialViewHolder(view)
            }

            VIEW_TYPE_QUESTION -> {
                val view = inflater.inflate(R.layout.item_course_fragment, parent, false)
                QuestionViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val content = itemList[position]

        when (holder) {
            is TopicViewHolder -> holder.bind(content)

            is AssignmentViewHolder -> {
                holder.bind(content)
                viewHolderList.add(holder)
            }

            is MaterialViewHolder -> {
                holder.bind(content)
                viewHolderList.add(holder)
            }

            is QuestionViewHolder -> {
                holder.bind(content)
                viewHolderList.add(holder)
            }
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
        private val optionBtn: ImageButton = itemView.findViewById(R.id.moreBtn)

        fun bind(topic: ContentModel) {
            topicTxt.text = topic.title

            itemView.setOnLongClickListener {
                viewHolderList.forEach { viewHolder ->
                    viewHolder.itemView.visibility = View.GONE
                    val layoutParams = viewHolder.itemView.layoutParams
                    layoutParams.height = 0
                    viewHolder.itemView.layoutParams = layoutParams
                }

                true
            }
        }
    }

    inner class AssignmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val descriptionTxt: TextView = itemView.findViewById(R.id.descriptionTxt)
        private val dateTxt: TextView = itemView.findViewById(R.id.dateTxt)
        private val optionBtn: ImageView = itemView.findViewById(R.id.moreBtn)

        fun bind(assignment: ContentModel) {
            descriptionTxt.text = assignment.title
            val date = formatDate2(assignment.date, "custom")
            "Posted $date".let { dateTxt.text = it }
        }
    }

    inner class MaterialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val materialImage: ImageView = itemView.findViewById(R.id.imageType)
        private val descriptionTxt: TextView = itemView.findViewById(R.id.descriptionTxt)
        private val dateTxt: TextView = itemView.findViewById(R.id.dateTxt)
        private val optionBtn: ImageView = itemView.findViewById(R.id.moreBtn)

        fun bind(material: ContentModel) {
            materialImage.setImageDrawable(
                ContextCompat
                    .getDrawable(itemView.context, R.drawable.ic_material)
            )

            descriptionTxt.text = material.title
            val date = formatDate2(material.date, "custom")
            "Posted $date".let { dateTxt.text = it }
        }
    }

    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val materialImage: ImageView = itemView.findViewById(R.id.imageType)
        private val descriptionTxt: TextView = itemView.findViewById(R.id.descriptionTxt)
        private val dateTxt: TextView = itemView.findViewById(R.id.dateTxt)
        private val optionBtn: ImageView = itemView.findViewById(R.id.moreBtn)

        fun bind(question: ContentModel) {
            materialImage.setImageDrawable(
                ContextCompat
                    .getDrawable(itemView.context, R.drawable.ic_question)
            )

            descriptionTxt.text = question.title
            val date = formatDate2(question.date, "custom")
            "Posted $date".let { dateTxt.text = it }
        }
    }

    private fun optionsAction(
        from: String,
        optionBtn: ImageView,
        topicModel: ContentModel,
        itemView: View,
        position: Int
    ) {
        optionBtn.setOnClickListener {
            val popupMenu = PopupMenu(it.context, it)
            popupMenu.inflate(R.menu.section_menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.editSection -> {
                        when (from) {
                            "assignment" -> {
                                val url =
                                    "${itemView.context.getString(R.string.base_url)}/getContent.php?" +
                                            "id=${topicModel.id}&type=${topicModel.type}"

                                getContent(url, itemView) { response ->
                                    if (response.isBlank()) {
                                        launchActivity(itemView, from, response)
                                    }
                                }
                            }

                            "material" -> {
                                val url =
                                    "${itemView.context.getString(R.string.base_url)}/getContent.php?" +
                                            "id=${topicModel.id}&type=${topicModel.type}"

                                getContent(url, itemView) { response ->
                                    if (response.isBlank()) {
                                        launchActivity(itemView, from, response)
                                    }
                                }
                            }

                            "question" -> {
                                val url =
                                    "${itemView.context.getString(R.string.base_url)}/getContent.php?" +
                                            "id=${topicModel.id}&type=${topicModel.type}"

                                getContent(url, itemView) { response ->
                                    if (response.isBlank()) {
                                        launchActivity(itemView, from, response)
                                    }
                                }
                            }

                            "topic" -> {

                            }
                        }

                        true
                    }

                    R.id.deleteSection -> {
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun getContent(
        url: String,
        itemView: View,
        onResponse: (response: String) -> Unit
    ) {
        val stringRequest = object : StringRequest(
            Method.GET,
            url,
            { response: String ->
                onResponse(response)
            },
            {
                onResponse("")
            }
        ) {}

        newRequestQueue(itemView.context).add(stringRequest)
    }


    private fun launchActivity(itemView: View, from: String, response: String) {
        itemView.context.startActivity(
            Intent(itemView.context, ELearningActivity::class.java)
                .putExtra("from", from)
                .putExtra("json", response)
        )
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val draggedItem = itemList[fromPosition]
        val targetItem = itemList[toPosition]

        println("1")

        if (draggedItem.viewType == "topic" && targetItem.viewType == "topic") {
            val hasContentsBelowDraggedTopic = hasQuestionsBelowTopic(fromPosition)
            val hasContentsBelowTargetTopic = hasQuestionsBelowTopic(toPosition)
            println("2")

            if (hasContentsBelowTargetTopic || hasContentsBelowDraggedTopic) {
                swapTopicsWithAssociatedItems(fromPosition, toPosition)
                println("3")
            } else {
                println("4")
                swapTopics(fromPosition, toPosition)
            }
        } else if (draggedItem.viewType == "topic" &&
            (targetItem.viewType == "assignment" || targetItem.viewType == "material" ||
                    targetItem.viewType == "question")
        ) {
            println("5")
            return
        } else {
            swapTopics(fromPosition, toPosition)
            println("6")
        }

        println("7")
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(
        recyclerView: RecyclerView
    ) {
        viewHolderList.forEach {
            it.itemView.isVisible = true
            val layoutParams = it.itemView.layoutParams
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.itemView.layoutParams = layoutParams
        }

        Handler(Looper.getMainLooper()).postDelayed({
            recyclerView.post {
//                draggedItemDecoration.let {
//                    it.setDragging(false)
//                    recyclerView.invalidateItemDecorations()
//                }

                notifyDataSetChanged()
            }
        }, 500)

    }

    private fun hasQuestionsBelowTopic(topicPosition: Int): Boolean {
        for (i in topicPosition + 1 until itemList.size) {
            val item = itemList[i]
            if (item.viewType == "assignment" || item.viewType == "material"
                || item.viewType == "question"
            ) {
                return true
            } else if (item.viewType == "topic") {
                return false
            }
        }
        return false
    }

    private fun swapTopicsWithAssociatedItems(fromPosition: Int, toPosition: Int) {
        val draggedTopic = itemList[fromPosition]
        val targetTopic = itemList[toPosition]

        // swap the associated items
        val draggedTopicItems = getAssociatedItems(draggedTopic)
        val targetTopicItems = getAssociatedItems(targetTopic)

        // swap topics
        itemList[fromPosition] = targetTopic
        itemList[toPosition] = draggedTopic

        itemList.removeAll(draggedTopicItems)
        itemList.removeAll(targetTopicItems)

        itemList.addAll(itemList.indexOf(draggedTopic) + 1, draggedTopicItems)
        itemList.addAll(itemList.indexOf(targetTopic) + 1, targetTopicItems)
    }


    private fun getAssociatedItems(topicModel: ContentModel): MutableList<ContentModel> {
        val topicPosition = itemList.indexOf(topicModel)
        val associatedItems = mutableListOf<ContentModel>()

        for (i in topicPosition + 1 until itemList.size) {
            val item = itemList[i]
            if (item.viewType == "assignment" || item.viewType == "material"
                || item.viewType == "question"
            ) {
                associatedItems.add(item)
            } else if (item.viewType == "topic") {
                break
            }
        }

        return associatedItems
    }

    private fun swapTopics(fromPosition: Int, toPosition: Int) {
        Collections.swap(itemList, fromPosition, toPosition)
    }
}