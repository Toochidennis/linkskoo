package com.digitaldream.linkskool.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.GenericAdapter
import com.digitaldream.linkskool.adapters.StudentELearningCourseWorkAdapter
import com.digitaldream.linkskool.models.ContentModel
import org.json.JSONArray


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class StudentELearningStreamFragment : Fragment() {

    private lateinit var streamRecyclerView: RecyclerView
    private lateinit var emptyTxt: TextView

    private lateinit var streamAdapter: GenericAdapter<ContentModel>
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
    }

    private fun setUpViews(view: View) {

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

                            "Question" -> {
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

                            else -> return
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
        streamRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = streamAdapter
        }
    }

    private fun setUpStreamAdapter() {
        streamAdapter = GenericAdapter(
            contentList,
            R.layout.item_stream_layout,
            bindItem = {itemView, model, position ->


            }
        ){

        }
    }

}