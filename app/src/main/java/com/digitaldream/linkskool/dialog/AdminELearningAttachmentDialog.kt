package com.digitaldream.linkskool.dialog

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.digitaldream.linkskool.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File

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

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var imageFile: File
    private var cameraCode = 0


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


//                    when (fileType) {
//                        FileType.IMAGE -> {
//
//                        }
//                        else ->null
//                    }
                }
            }


        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                if (cameraCode == 0) {
                    val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                    println("Image: ${imageFile.name}")
                } else {
                    val videoUri = result.data?.data
                    val videoFile = File(videoUri?.path.toString())
                    println("video: ${videoFile.name} ")
                }

            }

        }


        view.apply {
            val insertLink: TextView = findViewById(R.id.insertLink)
            val uploadFile: TextView = findViewById(R.id.uploadFile)
            val takePhotoBtn: TextView = findViewById(R.id.takePhoto)
            val recordVideoBtn: TextView = findViewById(R.id.recordVideo)


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


            takePhotoBtn.setOnClickListener {
                //requestCameraPermission()
                takePhoto()
            }

            recordVideoBtn.setOnClickListener {
                // requestCameraPermission()
                recordVideo()
            }

        }

    }


    private fun createImageFile(): File {
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "IMG_",
            ".JPG",
            storageDir
        ).apply {
            imageFile = this
        }
    }


    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            createImageFile()
            cameraCode = 0

            val imageUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireActivity().packageName}.provider",
                imageFile
            )

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            cameraLauncher.launch(takePictureIntent)

        } else {
            Toast.makeText(requireContext(), "package name is null", Toast.LENGTH_SHORT).show()
        }

    }


    private fun recordVideo() {
        val recordVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if (recordVideoIntent.resolveActivity(requireActivity().packageManager) != null) {
            cameraCode = 1
            cameraLauncher.launch(recordVideoIntent)
        } else {
            Toast.makeText(requireContext(), "package name is null", Toast.LENGTH_SHORT).show()
        }
    }

}