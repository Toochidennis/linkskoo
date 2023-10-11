package com.digitaldream.toyibatskool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.toyibatskool.R
import com.digitaldream.toyibatskool.models.UpcomingQuizModel

class StudentELearningUpcomingQuizAdapter(
    private val itemList: MutableList<UpcomingQuizModel>
) : RecyclerView.Adapter<StudentELearningUpcomingQuizAdapter.QuizViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_up_coming_quiz_layout, parent, false
        )

        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val model = itemList[position]

        holder.bind(model)
    }

    override fun getItemCount() = itemList.size

    inner class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val courseNameTxt: TextView = itemView.findViewById(R.id.courseNameTxt)
        private val quizTitleTxt: TextView = itemView.findViewById(R.id.titleTxt)
        private val dueDateTxt: TextView = itemView.findViewById(R.id.dateTxt)

        fun bind(upcomingQuizModel: UpcomingQuizModel) {
            courseNameTxt.text = upcomingQuizModel.courseName
            quizTitleTxt.text = upcomingQuizModel.title
            dueDateTxt.text = upcomingQuizModel.date
        }
    }

}