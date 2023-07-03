package com.digitaldream.linkskool.adapters

import android.content.ClipData
import android.os.Build
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.dialog.AdminELearningShortAnswerDialogFragment
import com.digitaldream.linkskool.fragments.AdminELearningMultiChoiceDialogFragment
import com.digitaldream.linkskool.models.MultiChoiceQuestion
import com.digitaldream.linkskool.models.QuestionItem
import com.digitaldream.linkskool.models.ShortAnswerModel


class AdminELearningQuestionItemAdapter(
    private val fragmentManager: FragmentManager,
    private var questionList: MutableList<QuestionItem?>,
    private val listener: OnQuestionDragListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
        private const val VIEW_TYPE_MULTI_CHOICE_OPTION = 1
        private const val VIEW_TYPE_SHORT_ANSWER = 2
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            VIEW_TYPE_MULTI_CHOICE_OPTION -> {
                val view = inflater.inflate(R.layout.item_multi_choice_option, parent, false)
                MultiChoiceViewHolder(view)
            }

            VIEW_TYPE_SHORT_ANSWER -> {
                val view = inflater.inflate(R.layout.item_short_answer, parent, false)
                ShortAnswerViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type: $viewType")
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val questionItem = questionList[position]
        when (holder) {
            is MultiChoiceViewHolder -> {
                val question = (questionItem as? QuestionItem.MultiChoice)?.question ?: return
                holder.bind(question)

                holder.itemView.setOnLongClickListener { view ->
                    val dragData = ClipData.newPlainText("", "")
                    val shadowBuilder = View.DragShadowBuilder(view)
                    view.startDragAndDrop(dragData, shadowBuilder, view, 0)
                    true
                }

                holder.itemView.setOnDragListener { _, event ->
                    if (event.action == DragEvent.ACTION_DROP) {
                        val draggedView = event.localState as View
                        val draggedAdapter = draggedView.tag as AdminELearningQuestionItemAdapter
                        val sourcePosition = draggedAdapter.questionList.indexOf(questionItem)
                        val targetPosition = holder.adapterPosition

                        listener.onQuestionDropped(
                            questionItem,
                            sourcePosition,
                            draggedAdapter,
                            this,
                            targetPosition
                        )
                    }
                    true
                }

            }

            is ShortAnswerViewHolder -> {
                val question = (questionItem as? QuestionItem.ShortAnswer)?.question ?: return
                holder.bind(question)

                holder.itemView.setOnLongClickListener { view ->
                    val dragData = ClipData.newPlainText("", "")
                    val shadowBuilder = View.DragShadowBuilder(view)
                    view.startDragAndDrop(dragData, shadowBuilder, view, 0)
                    true
                }

                holder.itemView.setOnDragListener { _, event ->
                    if (event.action == DragEvent.ACTION_DROP) {
                        val draggedView = event.localState as View
                        draggedView.tag = this
                        val draggedAdapter = draggedView.tag as AdminELearningQuestionItemAdapter
                        val sourcePosition = draggedAdapter.questionList.indexOf(questionItem)
                        val targetPosition = holder.adapterPosition

                        listener.onQuestionDropped(
                            questionItem,
                            sourcePosition,
                            draggedAdapter,
                            this,
                            targetPosition
                        )
                    }
                    true
                }

            }
        }
    }


    override fun getItemCount() = questionList.size

    override fun getItemViewType(position: Int): Int {
        return when (questionList[position]) {
            is QuestionItem.MultiChoice -> VIEW_TYPE_MULTI_CHOICE_OPTION
            is QuestionItem.ShortAnswer -> VIEW_TYPE_SHORT_ANSWER
            else -> position
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


    inner class ShortAnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

    private fun multiChoiceItemClick(multiItem: MultiChoiceQuestion, position: Int) {
        AdminELearningMultiChoiceDialogFragment(multiItem) { question ->
            val updatedQuestionItem = QuestionItem.MultiChoice(question)
            questionList[position] = updatedQuestionItem
            notifyItemChanged(position)
        }.show(fragmentManager, "")
    }


    private fun deleteQuestion(position: Int) {
        questionList.removeAt(position)
        notifyItemRemoved(position)
    }

    private fun shortAnswerItemClick(shortAnswer: ShortAnswerModel, position: Int) {
        AdminELearningShortAnswerDialogFragment(shortAnswer) { question ->
            val updatedQuestionItem = QuestionItem.ShortAnswer(question)
            questionList[position] = updatedQuestionItem
            notifyItemChanged(position)
        }.show(fragmentManager, "")
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
                                questionList[position] =
                                    updatedQuestionItem as QuestionItem.ShortAnswer
                                notifyItemChanged(position)
                            }.show(fragmentManager, "")
                        } else {
                            AdminELearningMultiChoiceDialogFragment(multiItem) { question ->
                                updatedQuestionItem = QuestionItem.MultiChoice(question)
                                questionList[position] =
                                    updatedQuestionItem as QuestionItem.MultiChoice
                                notifyItemChanged(position)
                            }.show(fragmentManager, "")
                        }
                        true
                    }

                    R.id.deleteSection -> {
                        deleteQuestion(position)
                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()

        }
    }

    interface OnQuestionDragListener {
        fun onQuestionDropped(
            question: QuestionItem,
            sourcePosition: Int,
            sourceAdapter: AdminELearningQuestionItemAdapter,
            targetAdapter: AdminELearningQuestionItemAdapter,
            targetPosition: Int
        )
    }
}



