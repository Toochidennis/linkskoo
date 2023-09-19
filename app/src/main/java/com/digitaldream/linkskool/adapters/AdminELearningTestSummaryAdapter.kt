package com.digitaldream.linkskool.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.models.MultiChoiceQuestion
import com.digitaldream.linkskool.models.QuestionItem
import com.digitaldream.linkskool.models.ShortAnswerModel
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.util.Locale

class AdminELearningTestSummaryAdapter(
    private val itemList: MutableList<QuestionItem?>,
    private val userResponses: MutableMap<String, String>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
        private const val VIEW_TYPE_MULTI_CHOICE_OPTION = 1
        private const val VIEW_TYPE_SHORT_ANSWER = 2
    }

    private var picasso = Picasso.get()

    private val labelList =
        arrayOf(
            'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L',
            'M', 'N', '0', 'P'
        )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_MULTI_CHOICE_OPTION -> {
                val view =
                    inflater.inflate(R.layout.item_multi_choice_test_summary_layout, parent, false)

                MultiChoiceSummaryViewHolder(view)
            }

            VIEW_TYPE_SHORT_ANSWER -> {
                val view = inflater.inflate(
                    R.layout.item_short_answer_test_summary_layout, parent,
                    false
                )

                ShortAnswerSummaryViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val questionItem = itemList[position]

        when (holder) {
            is MultiChoiceSummaryViewHolder -> {
                val question = (questionItem as? QuestionItem.MultiChoice)?.question ?: return
                holder.bind(question)
            }

            is ShortAnswerSummaryViewHolder -> {
                val question = (questionItem as? QuestionItem.ShortAnswer)?.question ?: return
                holder.bind(question)
            }
        }
    }

    override fun getItemCount() = itemList.size

    override fun getItemViewType(position: Int): Int {
        return when (itemList[position]) {
            is QuestionItem.MultiChoice -> VIEW_TYPE_MULTI_CHOICE_OPTION
            is QuestionItem.ShortAnswer -> VIEW_TYPE_SHORT_ANSWER
            else -> position
        }
    }

    inner class MultiChoiceSummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionCountTxt: TextView = itemView.findViewById(R.id.questionCountTxt)
        private val questionTxt: TextView = itemView.findViewById(R.id.questionTxt)
        private val questionImageView: ImageView = itemView.findViewById(R.id.questionImageView)
        private val userAnswerTxt: TextView = itemView.findViewById(R.id.userAnswerTxt)
        private val correctAnswerTxt: TextView = itemView.findViewById(R.id.correctAnswerTxt)
        private val userImageView: ImageView = itemView.findViewById(R.id.userImageView)
        private val correctImageView: ImageView = itemView.findViewById(R.id.correctImageView)

        fun bind(multiChoiceQuestion: MultiChoiceQuestion) {
            ("Question ${adapterPosition + 1}").let { questionCountTxt.text = it }
            questionTxt.text = multiChoiceQuestion.questionText
            loadImage(itemView.context, multiChoiceQuestion.attachmentUri, questionImageView)

            val label = labelList[multiChoiceQuestion.checkedPosition]
            val correctAnswer =
                String.format(
                    Locale.getDefault(), "Correct answer: %s. %s", label,
                    multiChoiceQuestion.correctAnswer
                )

            correctAnswerTxt.text = correctAnswer

            userResponse(multiChoiceQuestion)

        }

        private fun userResponse(multiChoiceQuestion: MultiChoiceQuestion) {
            val questionId = multiChoiceQuestion.questionId
            var optionOrder = 0

            val userResponse = userResponses[questionId]

            if (userResponse != null) {
                multiChoiceQuestion.options?.forEach { multipleChoiceOption ->
                    if (multipleChoiceOption.optionText.ifEmpty {
                            multipleChoiceOption.attachmentName
                        }.equals(userResponse, ignoreCase = true)) {

                        optionOrder = (multipleChoiceOption.optionOrder).toInt()
                    }
                }

                val label = labelList[optionOrder]
                val userAnswer =
                    String.format(
                        Locale.getDefault(), "Your answer: %s. %s", label,
                        userResponse
                    )

                userAnswerTxt.text = userAnswer

            }

        }

    }

    inner class ShortAnswerSummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionCountTxt: TextView = itemView.findViewById(R.id.questionCountTxt)
        private val questionTxt: TextView = itemView.findViewById(R.id.questionTxt)
        private val questionImageView: ImageView = itemView.findViewById(R.id.questionImageView)
        private val userAnswerTxt: TextView = itemView.findViewById(R.id.userAnswerTxt)
        private val correctAnswerTxt: TextView = itemView.findViewById(R.id.correctAnswerTxt)

        fun bind(shortAnswer: ShortAnswerModel) {
            ("Question ${adapterPosition + 1}").let { questionCountTxt.text = it }
            questionTxt.text = shortAnswer.questionText
            loadImage(itemView.context, shortAnswer.attachmentUri, questionImageView)

            ("Correct answer: ${shortAnswer.answerText}").let { correctAnswerTxt.text = it }

            val questionId = shortAnswer.questionId
            val userResponse = userResponses[questionId]

            if (userResponse != null) {
                "Your answer: $userResponse".let { userAnswerTxt.text = it }
            }
        }

    }


    private fun loadImage(context: Context, imageUri: Any?, imageView: ImageView) {
        try {
            when (imageUri) {
                is String -> {
                    if (imageUri.isNotEmpty()) {
                        val isBase64 = isBased64(imageUri)

                        if (isBase64) {
                            val bitmap = decodeBase64(imageUri)
                            imageView.isVisible = bitmap != null
                            imageView.setImageBitmap(bitmap)
                        } else {
                            val url = "${context.getString(R.string.base_url)}/$imageUri"
                            picasso.load(url).into(imageView)
                            imageView.isVisible = true
                        }

                    } else {
                        imageView.isVisible = false
                    }
                }

                is Uri -> {
                    picasso.load(imageUri).into(imageView)
                    imageView.isVisible = true
                }

                else -> imageView.isVisible = false

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun decodeBase64(encodedString: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(encodedString, Base64.DEFAULT)
            Bitmap.createBitmap(
                BitmapFactory.decodeByteArray(
                    decodedBytes,
                    0,
                    decodedBytes.size
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun isBased64(encodedString: String): Boolean {
        return try {
            org.apache.commons.codec.binary.Base64.isBase64(encodedString)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

}