package com.digitaldream.linkskool.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.models.MultiChoiceQuestion
import com.digitaldream.linkskool.models.MultipleChoiceOption
import com.digitaldream.linkskool.models.QuestionItem
import com.digitaldream.linkskool.models.ShortAnswerModel
import com.squareup.picasso.Picasso

class AdminELearningQuestionPreviewAdapter(
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
                val view = inflater.inflate(
                    R.layout.item_multi_choice_option_preview, parent,
                    false
                )
                MultipleChoiceViewHolder(view)
            }

            VIEW_TYPE_SHORT_ANSWER -> {
                val view = inflater.inflate(
                    R.layout.item_short_answer_preview, parent,
                    false
                )
                ShortAnswerViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type: $viewType")

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val questionItem = questionList[position]
        when (holder) {
            is MultipleChoiceViewHolder -> {
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
            else -> position
        }
    }

    inner class MultipleChoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionTxt: TextView = itemView.findViewById(R.id.questionTxt)
        private val questionImage: ImageView = itemView.findViewById(R.id.questionImage)
        private val optionRecyclerView: RecyclerView =
            itemView.findViewById(R.id.optionsRecyclerView)

        fun bind(multiItem: MultiChoiceQuestion) {
            questionTxt.text = multiItem.questionText
            loadImage(multiItem.attachmentUri, questionImage)

            multiItem.options?.let {
                optionsAdapter(it, optionRecyclerView)
            }
        }

    }

    inner class ShortAnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionTxt: TextView = itemView.findViewById(R.id.questionTxt)
        private val questionImage: ImageView = itemView.findViewById(R.id.questionImage)
        private val answerEditText: EditText = itemView.findViewById(R.id.answerEditText)

        fun bind(shortAnswer: ShortAnswerModel) {
            questionTxt.text = shortAnswer.questionText
            loadImage(shortAnswer.attachmentUri, questionImage)
            answerEditText.setText(shortAnswer.answerText)
        }

    }


    private fun optionsAdapter(
        options: MutableList<MultipleChoiceOption>,
        recyclerView: RecyclerView
    ) {
        val labelList =
            arrayOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', '0', 'P')

        GenericAdapter2(
            options,
            R.layout.item_options_preview,
            bindItem = { itemView, model, position ->
                val optionLabel: TextView = itemView.findViewById(R.id.optionsLabel)
                val optionTxt: TextView = itemView.findViewById(R.id.optionsTxt)
                val optionImage: ImageView = itemView.findViewById(R.id.optionImage)

                optionLabel.text = labelList[position].toString()

                if (model.optionText.isEmpty()) {
                    loadImage(model.attachmentUri, optionImage)
                    optionTxt.isVisible = false
                } else {
                    optionTxt.text = model.optionText
                    optionTxt.isVisible = true
                    optionImage.isVisible = false
                }

                itemView.alpha = 0f
                itemView.animate()
                    .alpha(1f)
                    .setDuration(500)
                    .setStartDelay(position * 100L)
                    .start()
            }
        ).let {
            recyclerView.apply {
                hasFixedSize()
                layoutManager = LinearLayoutManager(context)
                adapter = it
            }
        }

    }

    private fun loadImage(imageUri: Any?, imageView: ImageView) {
        try {
            when (imageUri) {
                is String -> {
                    if (imageUri.isNotEmpty()) {
                        Picasso.get().load(imageUri).into(imageView)
                        imageView.isVisible = true
                    } else {
                        imageView.isVisible = false
                    }

                }

                is Uri -> {
                    Picasso.get().load(imageUri).into(imageView)
                    imageView.isVisible = true
                }

                else -> imageView.isVisible = false

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}