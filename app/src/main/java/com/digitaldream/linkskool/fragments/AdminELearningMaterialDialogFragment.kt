package com.digitaldream.linkskool.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningQuestionSettingsAdapter
import com.digitaldream.linkskool.adapters.GenericAdapter
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.dialog.AdminELearningAttachmentDialog
import com.digitaldream.linkskool.models.AttachmentModel
import com.digitaldream.linkskool.models.ClassNameTable
import com.digitaldream.linkskool.models.TagModel
import com.digitaldream.linkskool.utils.FunctionUtils.compareJsonObjects
import com.digitaldream.linkskool.utils.FunctionUtils.convertUriOrFileToBase64
import com.digitaldream.linkskool.utils.FunctionUtils.requestToServer
import com.digitaldream.linkskool.utils.FunctionUtils.showSoftInput
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"
private const val ARG_PARAM4 = "param4"


class AdminELearningMaterialDialogFragment :
    DialogFragment(R.layout.fragment_admin_e_learning_material) {

    private lateinit var mBackBtn: ImageView
    private lateinit var mPostBtn: Button
    private lateinit var mMaterialTitleEditText: EditText
    private lateinit var mClassRecyclerView: RecyclerView
    private lateinit var mSelectAllBtn: Button
    private lateinit var mEmptyClassTxt: TextView
    private lateinit var mDescriptionEditText: EditText
    private lateinit var mAttachmentTxt: TextView
    private lateinit var mAttachmentBtn: RelativeLayout
    private lateinit var mAttachmentRecyclerView: RecyclerView
    private lateinit var mAddAttachmentBtn: TextView
    private lateinit var mTopicTxt: TextView

    private var mClassList = mutableListOf<ClassNameTable>()
    private val selectedClassItems = hashMapOf<String, String>()
    private val mTagList = mutableListOf<TagModel>()

    private val mFileList = mutableListOf<AttachmentModel>()
    private lateinit var mAdapter: GenericAdapter<AttachmentModel>

    private var mLevelId: String? = null
    private var mCourseId: String? = null
    private var mCourseName: String? = null
    private var jsonFromTopic: String? = null
    private var updatedJson = JSONObject()
    private var year: String? = null
    private var term: String? = null
    private var userId: String? = null
    private var userName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        arguments?.let {
            mLevelId = it.getString(ARG_PARAM1)
            mCourseId = it.getString(ARG_PARAM2)
            jsonFromTopic = it.getString(ARG_PARAM3)
            mCourseName = it.getString(ARG_PARAM4)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String, param3: String, param4: String) =
            AdminELearningMaterialDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_PARAM3, param3)
                    putString(ARG_PARAM4, param4)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            mBackBtn = findViewById(R.id.backBtn)
            mPostBtn = findViewById(R.id.postBtn)
            mMaterialTitleEditText = findViewById(R.id.materialTitle)
            mClassRecyclerView = findViewById(R.id.classRecyclerView)
            mSelectAllBtn = findViewById(R.id.selectAllBtn)
            mEmptyClassTxt = findViewById(R.id.emptyClassTxt)
            mDescriptionEditText = findViewById(R.id.descriptionEditText)
            mAttachmentTxt = findViewById(R.id.attachmentTxt)
            mAttachmentBtn = findViewById(R.id.attachmentBtn)
            mAttachmentRecyclerView = findViewById(R.id.attachmentRecyclerView)
            mAddAttachmentBtn = findViewById(R.id.addAttachmentButton)
            mTopicTxt = findViewById(R.id.topicTxt)
        }

        val sharedPreferences =
            requireActivity().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        year = sharedPreferences.getString("school_year", "")
        term = sharedPreferences.getString("term", "")
        userId = sharedPreferences.getString("user_id", "")
        userName = sharedPreferences.getString("user", "")


        onEdit()

        classList()

        fileAttachment(mAttachmentBtn)
        fileAttachment(mAddAttachmentBtn)

        showSoftInput(requireContext(), mMaterialTitleEditText)

        mPostBtn.setOnClickListener {
            verifyMaterial()
        }

        mBackBtn.setOnClickListener {
            onExit()
        }
    }

    private fun classList() {
        try {
            val mDatabaseHelper = DatabaseHelper(requireContext())
            val dao: Dao<ClassNameTable, Long> = DaoManager.createDao(
                mDatabaseHelper.connectionSource, ClassNameTable::class.java
            )
            mClassList = dao.queryBuilder().where().eq("level", mLevelId).query()
            mClassList.sortBy { it.className }

            mClassList.forEach { item ->
                mTagList.add(TagModel(item.classId, item.className))
            }

            if (selectedClassItems.isNotEmpty()) {
                mTagList.forEach { tagModel ->
                    if (selectedClassItems[tagModel.tagId] == tagModel.tagName)
                        tagModel.isSelected = true
                }
            }

            if (mTagList.isEmpty()) {
                mClassRecyclerView.isVisible = false
                mSelectAllBtn.isVisible = false
                mEmptyClassTxt.isVisible = true
            } else {
                AdminELearningQuestionSettingsAdapter(
                    selectedClassItems,
                    mTagList,
                    mSelectAllBtn
                ).let {
                    mClassRecyclerView.apply {
                        hasFixedSize()
                        layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        adapter = it
                        isVisible = true

                        mSelectAllBtn.isVisible = true
                        mEmptyClassTxt.isVisible = false
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun fileAttachment(button: View) {
        button.setOnClickListener {
            AdminELearningAttachmentDialog { type: String, name: String, uri: Any? ->
                mFileList.add(AttachmentModel(name, "", type, uri))
                setUpAdapter()
            }.show(parentFragmentManager, "")
        }
    }

    private fun setUpAdapter() {
        try {
            if (mFileList.isNotEmpty()) {
                mAdapter = GenericAdapter(
                    mFileList,
                    R.layout.fragment_admin_e_learning_assigment_attachment_item,
                    bindItem = { itemView, model, position ->
                        val itemTxt: TextView = itemView.findViewById(R.id.itemTxt)
                        val deleteButton: ImageView =
                            itemView.findViewById(R.id.deleteButton)

                        itemTxt.text = model.name

                        setCompoundDrawable(itemTxt, model.type)

                        deleteAttachment(deleteButton, position)

                    }, onItemClick = { position: Int ->
                        val itemPosition = mFileList[position]

                        previewAttachment(itemPosition.type, itemPosition.uri)
                    }
                )

                mAttachmentRecyclerView.apply {
                    hasFixedSize()
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = mAdapter
                    smoothScrollToPosition(mFileList.size - 1)

                    mAttachmentTxt.isVisible = false
                    mAddAttachmentBtn.isVisible = true
                    mAttachmentBtn.isClickable = false
                }
            } else {
                mAttachmentTxt.isVisible = true
                mAddAttachmentBtn.isVisible = false
                mAttachmentBtn.isClickable = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun previewAttachment(type: String, uri: Any?) {
        val fileUri = when (uri) {
            is File -> {
                val file = File(uri.absolutePath)
                FileProvider.getUriForFile(
                    requireContext(),
                    "${requireActivity().packageName}.provider",
                    file
                )
            }

            is String -> Uri.parse(uri)

            else -> uri
        }

        when (type) {
            "image" -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileUri as Uri?, "image/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            }

            "video" -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileUri as Uri?, "video/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            }

            "url" -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fileUri.toString()))
                startActivity(intent)
            }

            "pdf", "excel", "word" -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileUri as Uri?, "application/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            }

            else -> {
                Toast.makeText(requireContext(), "Can't open file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteAttachment(deleteButton: ImageView, position: Int) {
        deleteButton.setOnClickListener {
            mFileList.removeAt(position)
            if (mFileList.isEmpty()) {
                mAttachmentTxt.isVisible = true
                mAddAttachmentBtn.isVisible = false
                mAttachmentBtn.isClickable = true
            }
            mAdapter.notifyDataSetChanged()
        }
    }

    private fun setCompoundDrawable(textView: TextView, type: String) {
        textView.setCompoundDrawablesWithIntrinsicBounds(
            when (type) {
                "image" -> R.drawable.ic_image24
                "video" -> R.drawable.ic_video24
                "pdf" -> R.drawable.ic_pdf24
                "unknown" -> R.drawable.ic_unknown_document24
                "url" -> R.drawable.ic_link
                else -> R.drawable.ic_document24
            }.let {
                ContextCompat.getDrawable(requireContext(), it)
            },
            null, null, null
        )
    }

    private fun verifyMaterial() {
        val titleText = mMaterialTitleEditText.text.toString().trim()
        val descriptionText = mDescriptionEditText.text.toString().trim()
        val topicText = mTopicTxt.text.toString()

        if (titleText.isEmpty()) {
            mMaterialTitleEditText.error = "Please enter material title"
        } else if (selectedClassItems.size == 0) {
            Toast.makeText(requireContext(), "Please select a class", Toast.LENGTH_SHORT).show()
        } else if (descriptionText.isEmpty()) {
            mDescriptionEditText.error = "Please enter a description"
        } else if (topicText.isEmpty()) {
            Toast.makeText(requireContext(), "Please select a topic", Toast.LENGTH_SHORT).show()
        } else {
            prepareMaterial()
            println("material $updatedJson")
            postMaterial()
        }
    }

    private fun prepareMaterial() {
        val materialObject = JSONObject()
        val filesArray = JSONArray()
        val classArray = JSONArray()

        val titleText = mMaterialTitleEditText.text.toString().trim()
        val descriptionText = mDescriptionEditText.text.toString().trim()
        val topicText = mTopicTxt.text.toString()

        JSONObject().apply {
            put("title", titleText)
            put("description", descriptionText)
            put("topic", topicText)

            mFileList.isNotEmpty().let { isTrue ->
                if (isTrue) {
                    mFileList.forEach { attachment ->
                        JSONObject().apply {
                            put("file_name", attachment.name)

                            val oldFileName =
                                if (attachment.name != attachment.oldName &&
                                    attachment.oldName.isNotBlank()
                                ) {
                                    attachment.oldName
                                } else {
                                    ""
                                }

                            put("old_file_name", oldFileName)
                            put("type", attachment.type)

                            val image = convertUriOrFileToBase64(attachment.uri, requireContext())
                            put("image", image)
                        }.let {
                            filesArray.put(it)
                        }
                    }
                }
            }

            put("files", filesArray)

            selectedClassItems.forEach { (key, value) ->
                if (key.isNotEmpty() and value.isNotEmpty()) {
                    JSONObject().apply {
                        put("id", key)
                        put("name", value)
                    }.let {
                        classArray.put(it)
                    }
                }
            }

            put("class", classArray)
            put("course", mCourseId)
            put("course_name", mCourseName)
            put("author_id", userId)
            put("author_name", userName)
            put("year", year)
            put("term", term)
        }.let {
            materialObject.put("material", it)
        }

        updatedJson = materialObject
    }

    private fun postMaterial() {
        val url ="${getString(R.string.base_url)}/addContent.php"
        val hashMap = HashMap<String, String>().apply {
            put("material", updatedJson.toString())
        }

        requestToServer(Request.Method.POST,url, requireContext(), hashMap)
    }


    private fun onEdit() {

    }

    private fun onExit() {
        try {
            val json1 = JSONObject(jsonFromTopic!!)
            val json2 = updatedJson

            if (json2.length() != 0) {
                val areContentSame = compareJsonObjects(json1, json2)

                if (areContentSame) {
                    dismiss()
                } else {
                    exitWarning()
                }
            } else {
                dismiss()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun exitWarning() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Are you sure to exit?")
            setMessage("Your unsaved changes will be lost")
            setPositiveButton("Yes") { _, _ ->
                dismiss()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }.create()
    }

}