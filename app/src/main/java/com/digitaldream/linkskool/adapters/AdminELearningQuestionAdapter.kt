package com.digitaldream.linkskool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.dialog.AdminELearningShortAnswerDialogFragment
import com.digitaldream.linkskool.fragments.AdminELearningMultiChoiceDialogFragment
import com.digitaldream.linkskool.interfaces.ItemTouchHelperAdapter
import com.digitaldream.linkskool.models.MultiChoiceQuestion
import com.digitaldream.linkskool.models.QuestionItem
import com.digitaldream.linkskool.models.ShortAnswerModel
import java.util.Collections

class AdminELearningQuestionAdapter(
    private val fragmentManager: FragmentManager,
    private val questionItems: MutableList<QuestionItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperAdapter {

    private companion object {
        private const val VIEW_TYPE_MULTI_CHOICE_QUESTION = 1
        private const val VIEW_TYPE_SHORT_QUESTION = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_MULTI_CHOICE_QUESTION -> {
                val view = inflater.inflate(
                    R.layout.item_multi_choice_option, parent,
                    false
                )
                MultiChoiceViewHolder(view)
            }

            VIEW_TYPE_SHORT_QUESTION -> {
                val view = inflater.inflate(
                    R.layout.item_short_answer, parent,
                    false
                )
                ShortQuestionViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type $viewType")

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val questionItem = questionItems[position]
        when (holder) {
            is MultiChoiceViewHolder -> {
                val question = (questionItem as QuestionItem.MultiChoice).question
                holder.bind(question)
            }

            is ShortQuestionViewHolder -> {
                val question = (questionItem as QuestionItem.ShortAnswer).question
                holder.bind(question)
            }
        }

    }

    override fun getItemCount() = questionItems.size

    override fun getItemViewType(position: Int): Int {
        return when (questionItems[position]) {
            is QuestionItem.MultiChoice -> VIEW_TYPE_MULTI_CHOICE_QUESTION
            is QuestionItem.ShortAnswer -> VIEW_TYPE_SHORT_QUESTION
        }
    }

    inner class MultiChoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionLayout: RelativeLayout = itemView.findViewById(R.id.multiLayout)
        private val questionTextView: TextView = itemView.findViewById(R.id.questionTxt)
        private val questionCountTextView: TextView = itemView.findViewById(R.id.questionCountTxt)
        private val answerTextView: TextView = itemView.findViewById(R.id.answerTxt)
        private val questionButton: ImageView = itemView.findViewById(R.id.questionButton)

        fun bind(multiItem: MultiChoiceQuestion) {
            val questionCount = (adapterPosition + 1).toString()
            val numberOfOptions = "Options (${multiItem.options?.size})"
            questionTextView.text = multiItem.questionText
            questionCountTextView.text = questionCount
            answerTextView.text = numberOfOptions

            questionLayout.setOnClickListener {
                multiChoiceItemClick(multiItem, adapterPosition)
            }

            questionButtonAction(
                questionButton, ShortAnswerModel(), multiItem,
                adapterPosition, "multi"
            )

        }
    }

    inner class ShortQuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionLayout: RelativeLayout = itemView.findViewById(R.id.shortLayout)
        private val questionTextView: TextView = itemView.findViewById(R.id.questionTxt)
        private val questionCountTextView: TextView = itemView.findViewById(R.id.questionCountTxt)
        private val answerTextView: TextView = itemView.findViewById(R.id.answerTxt)
        private val questionButton: ImageView = itemView.findViewById(R.id.questionButton)

        fun bind(shortAnswer: ShortAnswerModel) {
            val questionCount = (adapterPosition + 1).toString()
            questionTextView.text = shortAnswer.questionText
            questionCountTextView.text = questionCount
            answerTextView.text = shortAnswer.answerText

            questionLayout.setOnClickListener {
                shortAnswerItemClick(shortAnswer, adapterPosition)
            }

            questionButtonAction(
                questionButton, shortAnswer, MultiChoiceQuestion(),
                adapterPosition, "short"
            )

        }
    }

    private fun questionButtonAction(
        questionBtn: ImageView,
        shortAnswer: ShortAnswerModel,
        multiItem: MultiChoiceQuestion,
        position: Int,
        from: String
    ) {
        questionBtn.setOnClickListener {
            val popupMenu = PopupMenu(it.context, it)
            popupMenu.inflate(R.menu.section_menu)
            var updatedQuestionItem: Any
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.editSection -> {
                        if (from == "short") {
                            AdminELearningShortAnswerDialogFragment(shortAnswer) { question ->
                                updatedQuestionItem = QuestionItem.ShortAnswer(question)
                                questionItems[position] =
                                    updatedQuestionItem as QuestionItem.ShortAnswer
                                notifyItemChanged(position)
                            }.show(fragmentManager, "")
                        } else {
                            AdminELearningMultiChoiceDialogFragment(multiItem) { question ->
                                updatedQuestionItem = QuestionItem.MultiChoice(question)
                                questionItems[position] =
                                    updatedQuestionItem as QuestionItem.MultiChoice
                                notifyItemChanged(position)
                            }.show(fragmentManager, "")
                        }
                        true
                    }

                    R.id.deleteSection -> {
                        deleteItem(position)
                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()

        }
    }


    private fun shortAnswerItemClick(shortAnswer: ShortAnswerModel, position: Int) {
        AdminELearningShortAnswerDialogFragment(shortAnswer) { question ->
            val updatedQuestionItem = QuestionItem.ShortAnswer(question)
            questionItems[position] = updatedQuestionItem
            notifyItemChanged(position)
        }.show(fragmentManager, "")
    }

    private fun multiChoiceItemClick(multiItem: MultiChoiceQuestion, position: Int) {
        AdminELearningMultiChoiceDialogFragment(multiItem) { question ->
            val updatedQuestionItem = QuestionItem.MultiChoice(question)
            questionItems[position] = updatedQuestionItem
            notifyItemChanged(position)
        }.show(fragmentManager, "")
    }


    private fun deleteItem(position: Int) {
        questionItems.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {

        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(questionItems, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(questionItems, i, i - 1)
            }
        }
        println("question Item: $questionItems")

        notifyItemMoved(fromPosition, toPosition)
    }


    override fun onItemDismiss(position: Int) {

    }

}