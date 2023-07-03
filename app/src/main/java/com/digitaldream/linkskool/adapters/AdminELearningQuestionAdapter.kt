package com.digitaldream.linkskool.adapters

import android.content.ClipData
import android.content.Context
import android.os.Build
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.dialog.AdminELearningSectionDialog
import com.digitaldream.linkskool.interfaces.ItemTouchHelperAdapter
import com.digitaldream.linkskool.models.GroupItem
import com.digitaldream.linkskool.models.QuestionItem
import java.util.Collections

class AdminELearningQuestionAdapter(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val groupItems: MutableList<GroupItem<String, QuestionItem?>>,
    private val listener: OnQuestionDragListener,
    private val listener2: AdminELearningQuestionItemAdapter.OnQuestionDragListener
) : RecyclerView.Adapter<AdminELearningQuestionAdapter.SectionViewHolder>(),
    ItemTouchHelperAdapter {

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(groupItems, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        println("group: $groupItems")

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.fragment_e_learning_question_item, parent, false)

        return SectionViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val question = groupItems[position]
        holder.bind(question)

        holder.itemView.setOnDragListener { _, event ->
            if (event.action == DragEvent.ACTION_DROP) {
                val draggedQuestion = event.localState as QuestionItem
                val targetPosition = holder.adapterPosition
                listener.onQuestionDropped(draggedQuestion, position, targetPosition)
            }
            true
        }

        holder.itemView.setOnLongClickListener { view ->
            val dragData = ClipData.newPlainText("", "")
            val shadowBuilder = View.DragShadowBuilder(view)
            view.startDragAndDrop(dragData, shadowBuilder, view, 0)
            true
        }
    }

    override fun getItemCount() = groupItems.size

    inner class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sectionTxt: TextView = itemView.findViewById(R.id.sectionTxt)
        private val sectionBtn: ImageView = itemView.findViewById(R.id.sectionButton)
        private val sectionBottomBorder: LinearLayout = itemView.findViewById(R.id.separator)
        private val sectionTopBorder: LinearLayout = itemView.findViewById(R.id.separator2)
        private val questionRecyclerView: RecyclerView =
            itemView.findViewById(R.id.questionRecyclerView)

        fun bind(groupItems: GroupItem<String, QuestionItem?>) {
            if (groupItems.title.isNullOrEmpty()) {
                sectionTxt.isVisible = false
                sectionBtn.isVisible = false
                sectionBottomBorder.isVisible = false
                sectionTopBorder.isVisible = false
            } else {
                sectionTxt.text = groupItems.title
                sectionTxt.isVisible = true
                sectionBtn.isVisible = true
                sectionBottomBorder.isVisible = true
                sectionTopBorder.isVisible = true
            }

            setupQuestionRecyclerView(questionRecyclerView, groupItems)

            sectionAction(sectionBtn, groupItems, adapterPosition)

        }

    }


    private fun sectionAction(
        sectionBtn: ImageView,
        item: GroupItem<String, QuestionItem?>,
        position: Int
    ) {
        sectionBtn.setOnClickListener { view ->
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.inflate(R.menu.section_menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.editSection -> {

                        item.title?.let {
                            AdminELearningSectionDialog(context, it) { updateSection ->
                                item.title = updateSection
                                notifyDataSetChanged()
                            }.apply {
                                setCancelable(true)
                                show()
                            }.window?.setLayout(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                        }

                        true
                    }

                    R.id.deleteSection -> {
                        if (groupItems[position].itemList.isEmpty()) {
                            groupItems.removeAt(position)
                        } else {
                            item.title = null
                        }

                        notifyDataSetChanged()
                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()
        }
    }


    private fun setupQuestionRecyclerView(
        recyclerView: RecyclerView,
        groupItem: GroupItem<String, QuestionItem?>
    ) {
        if (groupItem.itemList.isNotEmpty()) {
            recyclerView.apply {
                hasFixedSize()
                layoutManager = LinearLayoutManager(context)
                adapter = AdminELearningQuestionItemAdapter(
                    fragmentManager, groupItem.itemList,
                    listener2
                )
            }
        }

    }

    interface OnQuestionDragListener {
        fun onQuestionDropped(
            question: QuestionItem?,
            fromPosition: Int,
            toPosition: Int
        )
    }

}


