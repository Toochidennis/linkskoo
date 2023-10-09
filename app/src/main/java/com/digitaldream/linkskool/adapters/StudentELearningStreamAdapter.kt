package com.digitaldream.linkskool.adapters

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.StudentELearningActivity
import com.digitaldream.linkskool.models.CommentDataModel
import com.digitaldream.linkskool.models.ContentModel
import com.digitaldream.linkskool.utils.FunctionUtils.formatDate2
import com.digitaldream.linkskool.utils.FunctionUtils.getDate
import com.digitaldream.linkskool.utils.FunctionUtils.hideKeyboard
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import timber.log.Timber

class StudentELearningStreamAdapter(
    private val itemList: MutableList<ContentModel>
) : RecyclerView.Adapter<StudentELearningStreamAdapter.ContentViewModel>() {

    private var userName: String? = null
    private var userId: String? = null
    private var year: String? = null
    private var courseName: String? = null

    private val commentStorage = mutableMapOf<String, MutableList<CommentDataModel>>()
    private lateinit var commentAdapter: StudentELearningStreamCommentAdapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewModel {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_stream_layout, parent, false)

        return ContentViewModel(view)
    }

    override fun onBindViewHolder(holder: ContentViewModel, position: Int) {
        val itemModel = itemList[position]
        holder.bind(itemModel)
    }

    override fun getItemCount() = itemList.size

    inner class ContentViewModel(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val descriptionTxt: TextView = itemView.findViewById(R.id.descriptionTxt)
        private val dateTxt: TextView = itemView.findViewById(R.id.dateTxt)
        private val commentRecyclerView: RecyclerView =
            itemView.findViewById(R.id.commentRecyclerView)
        private val commentEditText: EditText = itemView.findViewById(R.id.commentEditText)
        private val sendBtn: ImageButton = itemView.findViewById(R.id.sendBtn)

        fun bind(contentModel: ContentModel) {
            setImageResource(imageView, contentModel)

            val description = "${contentModel.description} ${contentModel.title}"
            descriptionTxt.text = description

            val formattedDate = formatDate2(contentModel.date, "custom")
            dateTxt.text = formattedDate

            loadComment(contentModel)

            viewContentDetails(itemView, contentModel)
        }

        private fun loadComment(contentModel: ContentModel) {
            editTextWatcher(commentEditText, sendBtn)
            sendComment(contentModel, sendBtn, commentEditText, commentRecyclerView)
        }
    }

    private fun setImageResource(imageView: ImageView, contentModel: ContentModel) {
        imageView.setImageResource(
            when (contentModel.viewType) {
                "material" -> R.drawable.ic_material
                "question" -> R.drawable.ic_question
                else -> R.drawable.baseline_assignment_24
            }
        )

        imageView.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
    }

    private fun showSendBtn(imageButton: ImageButton) {
        imageButton.isVisible = true
    }

    private fun hideSendBtn(imageButton: ImageButton) {
        imageButton.isVisible = false
    }

    private fun viewContentDetails(itemView: View, contentModel: ContentModel) {
        itemView.setOnClickListener {
            val url =
                "${it.context.getString(R.string.base_url)}/getContent.php?" +
                        "id=${contentModel.id}&type=${contentModel.type}"

            sendRequest(it.context, url) { response ->
                launchActivity(it.context, contentModel.viewType, response)
            }
        }
    }

    private fun sendRequest(
        context: Context,
        url: String,
        method: Int = Request.Method.GET,
        isShowProgressBar: Boolean = true,
        data: HashMap<String, String>? = null,
        onResponse: (String) -> Unit
    ) {
        sendRequestToServer(
            method,
            url,
            context,
            data,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    onResponse(response)
                }

                override fun onError(error: VolleyError) {
                    Toast.makeText(
                        context, "Something went wrong please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, isShowProgressBar
        )
    }

    private fun launchActivity(context: Context, from: String, response: String) {
        context.startActivity(
            Intent(context, StudentELearningActivity::class.java)
                .putExtra("from", from)
                .putExtra("json", response)
        )
    }

    private fun editTextWatcher(editText: EditText, imageButton: ImageButton) {
        hideSendBtn(imageButton)

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotBlank()) {
                    showSendBtn(imageButton)
                } else {
                    hideSendBtn(imageButton)
                }
            }
        })
    }

    private fun setUpCommentRecyclerView(recyclerView: RecyclerView, contentModel: ContentModel) {
        val commentList = commentStorage[contentModel.id]

        if (commentList != null) {
            commentAdapter = StudentELearningStreamCommentAdapter(commentList)

            recyclerView.apply {
                hasFixedSize()
                layoutManager = LinearLayoutManager(recyclerView.context)
                adapter = commentAdapter
            }
        }
    }

    private fun prepareComment(commentEditText: EditText, contentModel: ContentModel) {
        val sharedPreferences =
            commentEditText.context.getSharedPreferences("loginDetail", MODE_PRIVATE)
        with(sharedPreferences) {
            userId = getString("user_id", "")
            userName = getString("user", "")
            year = getString("school_year", "")
            courseName = getString("course_name", "")
        }

        val message = commentEditText.text.toString().trim()
        val date = formatDate2(getDate(), "custom")

        val commentDataModel = CommentDataModel(
            "", userId ?: "", "",
            userName ?: "", message, date
        )

        val newCommentList =
            mutableListOf<CommentDataModel>().apply {
                add(commentDataModel)
            }

        val contentId = contentModel.id

        val previousCommentList = commentStorage[contentId]

        if (previousCommentList == null) {
            commentStorage[contentId] = newCommentList
        } else {
            previousCommentList.addAll(newCommentList)
        }

        hideKeyboard(commentEditText, commentEditText.context)
    }

    private fun sendComment(
        contentModel: ContentModel,
        imageButton: ImageButton,
        editText: EditText,
        recyclerView: RecyclerView
    ) {
        imageButton.setOnClickListener {
            prepareComment(editText, contentModel)
            setUpCommentRecyclerView(recyclerView, contentModel)

            postComment(it.context, contentModel)

            getComment(it.context, contentModel)
        }
    }

    private fun prepareCommentJson(contentModel: ContentModel): HashMap<String, String> {
        val commentList = commentStorage[contentModel.id]

        return HashMap<String, String>().apply {
            put("content_id", contentModel.id)
            put("author_id", userId ?: "")
            put("author_name", userName ?: "")

            commentList?.forEach { commentData ->
                put("comment", commentData.comment)
            }

            put("content_title", contentModel.title)
            put("level", contentModel.levelId)
            put("course", contentModel.courseId)
            put("course_name", courseName ?: "")
            put("term", contentModel.term)
            put("year", year ?: "")
        }
    }

    private fun postComment(context: Context, contentModel: ContentModel) {
        val commentHashMap = prepareCommentJson(contentModel)
        Timber.tag("comment").d("$commentHashMap")
        val url = "${context.getString(R.string.base_url)}/addContentComment.php"

        sendRequest(context, url, Request.Method.POST, false, commentHashMap) {

        }
    }

    private fun getComment(context: Context, contentModel: ContentModel) {
        val url = "${context.getString(R.string.base_url)}/getContentComment" +
                ".php?content_id=${contentModel.id}"

        sendRequest(context, url, Request.Method.GET, false) {

        }
    }

}