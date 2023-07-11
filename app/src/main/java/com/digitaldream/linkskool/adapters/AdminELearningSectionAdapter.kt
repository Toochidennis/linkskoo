package com.digitaldream.linkskool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.dialog.AdminELearningSectionDialog
import com.digitaldream.linkskool.interfaces.ItemTouchHelperAdapter
import com.digitaldream.linkskool.models.SectionItem
import com.digitaldream.linkskool.utils.ItemTouchHelperCallback
import java.util.Collections

class AdminELearningSectionAdapter(
    private val fragmentManager: FragmentManager,
    private val sectionItems: MutableList<SectionItem>
) : RecyclerView.Adapter<AdminELearningSectionAdapter.SectionViewHolder>(), ItemTouchHelperAdapter {

    private val recyclerViewList = mutableListOf<RecyclerView>()
    private lateinit var viewType: RecyclerView.ViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_section, parent,
            false
        )
        return SectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val sectionItem = sectionItems[position]
        holder.bind(sectionItem)
        viewType = holder
    }

    override fun getItemCount() = sectionItems.size

    inner class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sectionTxt: TextView = itemView.findViewById(R.id.sectionTxt)
        private val sectionBtn: ImageView = itemView.findViewById(R.id.sectionButton)
        private val sectionBottomBorder: LinearLayout = itemView.findViewById(R.id.separator)
        private val sectionTopBorder: LinearLayout = itemView.findViewById(R.id.separator2)
        private val questionRecyclerView: RecyclerView =
            itemView.findViewById(R.id.questionRecyclerView)

        fun bind(sectionItem: SectionItem) {
            if (sectionItem.title.isEmpty()) {
                sectionTxt.isVisible = false
                sectionBtn.isVisible = false
                sectionBottomBorder.isVisible = false
                sectionTopBorder.isVisible = false
            } else {
                sectionTxt.text = sectionItem.title
                sectionTxt.isVisible = true
                sectionBtn.isVisible = true
                sectionBottomBorder.isVisible = true
                sectionTopBorder.isVisible = true
            }

            sectionAction(sectionBtn, sectionItem, adapterPosition)

            setUpQuestionAdapter(sectionItem, questionRecyclerView)

            itemView.setOnLongClickListener {
                recyclerViewList.forEach { recyclerView ->
                    recyclerView.isVisible = false
                }

                true
            }
        }
    }

    private fun sectionAction(
        sectionBtn: ImageView,
        item: SectionItem,
        position: Int
    ) {
        sectionBtn.setOnClickListener { view ->
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.inflate(R.menu.section_menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.editSection -> {

                        item.title.let {
                            AdminELearningSectionDialog(view.context, it) { updateSection ->
                                item.title = updateSection
                                notifyItemChanged(position)
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
                        sectionItems.removeAt(position)
                        notifyDataSetChanged()
                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()
        }
    }


    private fun setUpQuestionAdapter(
        sectionItem: SectionItem,
        recyclerView: RecyclerView
    ) {
        if (sectionItem.questions.isNotEmpty()) {
            val questionAdapter =
                AdminELearningQuestionAdapter(fragmentManager, sectionItem.questions)

            recyclerView.apply {
                hasFixedSize()
                layoutManager = LinearLayoutManager(context)
                adapter = questionAdapter

                val questionTouchHelperCallback = ItemTouchHelperCallback(questionAdapter)
                val questionTouchHelper = ItemTouchHelper(questionTouchHelperCallback)
                questionTouchHelper.attachToRecyclerView(recyclerView)
            }

            recyclerViewList.add(recyclerView)
        }

    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < 0 || fromPosition >= sectionItems.size || toPosition < 0 || toPosition >= sectionItems.size) {
            // Invalid positions, do nothing or handle the error as needed
            return
        }
        val fromSection = sectionItems[fromPosition]
        val toSection = sectionItems[toPosition]

        if (fromPosition != toPosition) {
            // reordering of sections
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(sectionItems, i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap(sectionItems, i, i - 1)
                }
            }

            println("Sections: $sectionItems")

            notifyItemMoved(fromPosition, toPosition)
        } else {
            //reordering of questions from one section to another
            val fromQuestions = fromSection.questions
            val toQuestions = toSection.questions

            if (fromQuestions.isEmpty() || fromPosition >= fromQuestions.size) {
                return
            }

            val questionItem = fromQuestions.removeAt(fromPosition)
            toQuestions.add(questionItem)

            println("Section 2: $sectionItems")

            notifyItemChanged(fromPosition)
            notifyItemChanged(toPosition)
        }

    }

    override fun onItemDismiss(position: Int) {
        recyclerViewList.forEach { recyclerView ->
            recyclerView.isVisible = true
        }
    }
}