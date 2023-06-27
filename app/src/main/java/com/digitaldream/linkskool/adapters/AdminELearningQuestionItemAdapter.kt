package com.digitaldream.linkskool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.models.MultiChoiceQuestion
import com.digitaldream.linkskool.models.QuestionItem
import com.digitaldream.linkskool.models.ShortAnswerModel


class AdminELearningQuestionItemAdapter(
    private var questionList: MutableList<QuestionItem?>,
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        println("size: ${questionList.size}")
        val questionItem = questionList[position]
        when (holder) {
            is MultiChoiceViewHolder -> {
                val question = (questionItem as? QuestionItem.MultiChoice)?.question ?: return
                holder.bind(question)
            }

            is ShortAnswerViewHolder -> {
                val question = (questionItem as? QuestionItem.ShortAnswer)?.question ?: return
                holder.bind(question)
            }
        }
    }


    override fun getItemCount() = questionList.size

    override fun getItemViewType(position: Int): Int {
        return when (questionList[position]) {
            is QuestionItem.MultiChoice -> VIEW_TYPE_MULTI_CHOICE_OPTION
            is QuestionItem.ShortAnswer -> VIEW_TYPE_SHORT_ANSWER
            else -> 0
        }
    }


    inner class MultiChoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questionTextView: TextView = itemView.findViewById(R.id.questionTxt)
        val questionCountTextView: TextView = itemView.findViewById(R.id.questionCountTxt)
        val answerTextView: TextView = itemView.findViewById(R.id.answerTxt)
        val questionButton: ImageView = itemView.findViewById(R.id.questionButton)

        fun bind(multiItem: MultiChoiceQuestion) {
            val option = multiItem.options?.get(adapterPosition) ?: return

            val questionCount = (adapterPosition + 1).toString()
            questionTextView.text = multiItem.questionText
            questionCountTextView.text = questionCount
            answerTextView.text = multiItem.correctAnswer
        }
    }


    inner class ShortAnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questionTextView: TextView = itemView.findViewById(R.id.questionTxt)
        val questionCountTextView: TextView = itemView.findViewById(R.id.questionCountTxt)
        val answerTextView: TextView = itemView.findViewById(R.id.answerTxt)
        val questionButton: ImageView = itemView.findViewById(R.id.questionButton)

        fun bind(shortAnswer: ShortAnswerModel) {
            val questionCount = (adapterPosition + 1).toString()
            questionTextView.text = shortAnswer.questionText
            questionCountTextView.text = questionCount
            answerTextView.text = shortAnswer.answerText
        }
    }
}



