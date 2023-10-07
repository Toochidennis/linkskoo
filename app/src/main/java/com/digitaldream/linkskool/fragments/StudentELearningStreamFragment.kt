package com.digitaldream.linkskool.fragments

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.StudentELearningActivity
import com.digitaldream.linkskool.adapters.AdminELearningCommentAdapter
import com.digitaldream.linkskool.adapters.GenericAdapter
import com.digitaldream.linkskool.adapters.StudentELearningStreamAdapter
import com.digitaldream.linkskool.adapters.StudentELearningStreamCommentAdapter
import com.digitaldream.linkskool.models.CommentDataModel
import com.digitaldream.linkskool.models.ContentModel
import com.digitaldream.linkskool.utils.FunctionUtils.formatDate2
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import org.json.JSONArray
import timber.log.Timber


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class StudentELearningStreamFragment : Fragment() {

    private lateinit var streamRecyclerView: RecyclerView
    private lateinit var emptyTxt: TextView

    private lateinit var streamAdapter: StudentELearningStreamAdapter
    private var contentList = mutableListOf<ContentModel>()

    private var jsonData: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            jsonData = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_e_learning_stream, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(data: String, param2: String = "") =
            StudentELearningStreamFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, data)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        loadStreams()
    }

    private fun setUpViews(view: View) {
        view.apply {
            streamRecyclerView = findViewById(R.id.streamRecyclerView)
            emptyTxt = findViewById(R.id.emptyTxt)
        }
        /*        val sharedPreferences = requireActivity().getSharedPreferences(
                    "loginDetail",
                    MODE_PRIVATE
                )

                userName = sharedPreferences.getString("user", "")
                userId = sharedPreferences.getString("user_id", "")*/

    }


    private fun loadStreams() {
        if (jsonData?.isNotBlank() == true) {
            if (jsonData != "[]") {
                parseResponse(jsonData!!)
                emptyTxt.isVisible = false
            } else {
                emptyTxt.isVisible = true
            }
        }
    }


    private fun parseResponse(response: String) {
        try {
            with(JSONArray(response)) {
                for (i in 0 until length()) {
                    val contentObject = getJSONObject(i)

                    contentObject.let {
                        val id = it.getString("id")
                        val title = it.getString("title")
                        val courseId = it.getString("course_id")
                        val levelId = it.getString("level")
                        val authorId = it.getString("author_id")
                        val authorName = it.getString("author_name")
                        val term = it.getString("term")
                        val date = it.getString("upload_date")
                        val type = it.getString("type")
                        val category = it.getString("category")

                        when (it.getString("content_type")) {
                            "Assignment" -> {
                                val content = ContentModel(
                                    id, title,
                                    "New assignment:",
                                    courseId,
                                    levelId,
                                    authorId, authorName, date, term, type,
                                    "assignment",
                                    category
                                )

                                contentList.add(content)

                            }

                            "Material" -> {
                                val content = ContentModel(
                                    id, title,
                                    "New material:",
                                    courseId,
                                    levelId,
                                    authorId, authorName, date, term, type,
                                    "material",
                                    category
                                )

                                contentList.add(content)
                            }

                            "Quiz" -> {
                                val content = ContentModel(
                                    id, title,
                                    "New question:",
                                    courseId, levelId,
                                    authorId, authorName,
                                    date, term, type,
                                    "question",
                                    category
                                )

                                contentList.add(content)
                            }

                            else -> null
                        }
                    }
                }
            }

            setUpRecyclerView()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpRecyclerView() {
        streamAdapter = StudentELearningStreamAdapter(contentList)

        streamRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = streamAdapter
        }
    }

}