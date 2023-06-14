package com.digitaldream.linkskool.dialog

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.digitaldream.linkskool.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

enum class FileType {
    IMAGE,
    VIDEO,
    PDF,
    WORD,
    EXCEL,
    CSV,
    UNKNOWN
}


class AdminELearningAttachmentDialog() : BottomSheetDialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_admin_elearning_attachment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val filePickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { fileUri: Uri? ->
                fileUri?.let { uri ->
                    val mimeType = requireActivity().contentResolver.getType(uri)
                    val fileType = when {
                        mimeType?.startsWith("image/.*") == true -> FileType.IMAGE
                        mimeType?.startsWith("video/.*") == true -> FileType.VIDEO
                        mimeType == "application/pdf" -> FileType.PDF
                        mimeType == "application/msword" || mimeType == "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> FileType.WORD
                        mimeType == "text/csv" -> FileType.CSV
                        mimeType == "application/vnd.ms-excel" || mimeType == "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> FileType.EXCEL
                        else -> FileType.UNKNOWN

                    }
                }
            }


        view.apply {
            val insertLink: TextView = findViewById(R.id.insertLink)
            val uploadFile: TextView = findViewById(R.id.uploadFile)
            val takePhoto: TextView = findViewById(R.id.takePhoto)
            val recordVideo: TextView = findViewById(R.id.recordVideo)


            insertLink.setOnClickListener {
                InsertLinkDialog(requireContext()).apply {
                    setCancelable(true)
                    show()
                }.window?.setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }


            uploadFile.setOnClickListener {
                filePickerLauncher.launch("*/*")
            }


            val cameraLaucher = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    val imageUri = data?.data
                    val imagePath: String? = data?.getStringExtra(MediaStore.EXTRA_OUTPUT)

                    println("image: $imageUri mm: $imagePath")
                }

            }


            takePhoto.setOnClickListener {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
                    cameraLaucher.launch(takePictureIntent)
                }
            }


            recordVideo.setOnClickListener {
                val takePictureIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
                    cameraLaucher.launch(takePictureIntent)
                }
            }


        }

    }

}