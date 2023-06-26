package com.digitaldream.linkskool.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.dialog.AdminELearningQuestionDialog
import com.digitaldream.linkskool.models.MultiChoiceQuestion
import com.digitaldream.linkskool.models.ShortAnswerModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONObject


private const val ARG_PARAM1 = "param1"


class AdminELearningQuestionFragment : Fragment(R.layout.fragment_admin_e_learning_question) {


    private lateinit var topicButton: RelativeLayout
    private lateinit var questionTitleTxt: TextView
    private lateinit var descriptionTxt: TextView
    private lateinit var questionRecyclerView: RecyclerView
    private lateinit var previewQuestionButton: LinearLayout
    private lateinit var submitQuestionButton: LinearLayout
    private lateinit var addQuestionButton: FloatingActionButton

    private var questionModel = MultiChoiceQuestion()
    private var shortAnswerModel = ShortAnswerModel()
    private val selectedClassId = hashMapOf<String, String>()

    private var jsonFromQuestionSettings: String? = null
    private var questionTitle: String? = null
    private var levelId: String? = null
    private var courseId: String? = null
    private var questionDescription: String? = null
    private var startDate: String? = null
    private var startTime: String? = null
    private var endDate: String? = null
    private var endTime: String? = null
    private var questionTopic: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            jsonFromQuestionSettings = it.getString(ARG_PARAM1)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
            AdminELearningQuestionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            topicButton = findViewById(R.id.topicButton)
            questionTitleTxt = findViewById(R.id.questionTitleTxt)
            descriptionTxt = findViewById(R.id.descriptionTxt)
            questionRecyclerView = findViewById(R.id.questionRecyclerView)
            previewQuestionButton = findViewById(R.id.previewQuestionButton)
            submitQuestionButton = findViewById(R.id.submitQuestionButton)
            addQuestionButton = findViewById(R.id.add_question_btn)

            toolbar.apply {
                title = "Stream"
                setNavigationIcon(R.drawable.arrow_left)
                setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            }
        }

        fromQuestionSettings()

        addQuestionButton.setOnClickListener {
            addQuestion()
        }

        topicButton.setOnClickListener {
            toQuestionSettings()
        }
    }

    private fun addQuestion() {
        AdminELearningQuestionDialog(
            requireContext(),
            parentFragmentManager,
            MultiChoiceQuestion(),
            ShortAnswerModel()
        ) { question: MultiChoiceQuestion?, shortQuestion: ShortAnswerModel?, section: String? ->

            println("multi $question  short: $shortQuestion, section: $section")

        }.apply {
            setCancelable(true)
            show()
        }.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }


    private fun fromQuestionSettings() {
        if (!jsonFromQuestionSettings.isNullOrEmpty()) {
            jsonFromQuestionSettings?.let {
                JSONObject(it).run {
                    val settingsObject = getJSONObject("settings")
                    val classArray = getJSONArray("class")

                    questionTitle = settingsObject.getString("title")
                    questionDescription = settingsObject.getString("description")
                    startDate = settingsObject.getString("startDate")
                    endDate = settingsObject.getString("endDate")
                    startTime = settingsObject.getString("startTime")
                    endTime = settingsObject.getString("endTime")
                    questionTopic = settingsObject.getString("topic")
                    levelId = settingsObject.getString("levelId")
                    courseId = settingsObject.getString("courseId")

                    questionTitleTxt.text = questionTitle
                    descriptionTxt.text = questionDescription

                    for (i in 0 until classArray.length()) {
                        selectedClassId[classArray.getJSONObject(i).getString("id")] =
                            classArray.getJSONObject(i).getString("name")
                    }
                }
            }

        }
    }

    private fun toQuestionSettings() {
        val jsonObject = JSONObject()
        val classArray = JSONArray()

        selectedClassId.forEach { (key, value) ->
            if (key.isNotEmpty() && value.isNotEmpty()) {
                JSONObject().apply {
                    put("id", key)
                    put("name", value)
                }.let {
                    classArray.put(it)
                }
            }
        }

        JSONObject().apply {
            put("title", questionTitle)
            put("description", questionDescription)
            put("startDate", startDate)
            put("endDate", endDate)
            put("startTime", startTime)
            put("endTime", endTime)
            put("topic", questionTopic)
        }.let {
            jsonObject.put("settings", it)
            jsonObject.put("class", classArray)
        }

        requireActivity().supportFragmentManager.commit {
            replace(
                R.id.learning_container, AdminELearningQuestionSettingsFragment
                    .newInstance(levelId!!, "", jsonObject.toString())
            )
        }

    }

}

/*
<RelativeLayout
android:id="@+id/notification_layout"
android:layout_width="match_parent"
android:layout_height="wrap_content"
android:layout_margin="@dimen/dimen_10"
android:background="@drawable/edit_text_bg4"
android:clickable="true"
android:focusable="true"
android:foreground="?android:attr/selectableItemBackground">


<RelativeLayout
android:id="@+id/description_layout"
android:layout_width="match_parent"
android:layout_height="match_parent"
android:paddingStart="5dp"
android:paddingEnd="5dp">

<androidx.cardview.widget.CardView
android:id="@+id/count_icon"
android:layout_width="@dimen/dimen_32"
android:layout_height="@dimen/dimen_32"
android:layout_alignParentStart="true"
android:layout_alignParentTop="true"
android:layout_marginTop="10dp"
android:backgroundTint="@color/test_color_3"
app:cardCornerRadius="@dimen/dimen_32">

<TextView
android:layout_width="wrap_content"
android:layout_height="wrap_content"
android:layout_gravity="center"
android:fontFamily="@font/poppins_regular"
android:padding="5dp"
android:text="1"
android:textColor="@color/white"
android:textSize="@dimen/text_14" />

</androidx.cardview.widget.CardView>

<TextView
android:id="@+id/description1"
android:layout_width="match_parent"
android:layout_height="wrap_content"
android:layout_alignParentTop="true"
android:layout_marginStart="15dp"
android:layout_marginEnd="15dp"
android:layout_toEndOf="@id/count_icon"
android:ellipsize="marquee"
android:fontFamily="@font/poppins_regular"
android:lines="2"
android:text="Introduction to DevOps is what____?"
android:textColor="@color/black"
android:textSize="@dimen/text_16" />

</RelativeLayout>

<LinearLayout
android:id="@+id/separator"
android:layout_width="match_parent"
android:layout_height="wrap_content"
android:layout_below="@id/description_layout"
android:background="@drawable/line_separator"
android:orientation="horizontal" />

<TextView
android:layout_width="match_parent"
android:layout_height="wrap_content"
android:layout_below="@id/separator"
android:drawablePadding="@dimen/dimen_16"
android:padding="@dimen/dimen_10"
android:text="Add answer"
android:textColor="@color/black"
android:textSize="14sp"
app:drawableEndCompat="@drawable/ic_more"
app:drawableStartCompat="@drawable/ic_multi_choice" />

</RelativeLayout>*/
