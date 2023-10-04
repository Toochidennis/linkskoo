package com.digitaldream.linkskool.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.dialog.AdminELearningFilePreviewDialogFragment
import com.digitaldream.linkskool.models.AttachmentModel
import com.digitaldream.linkskool.utils.StudentFileViewModel
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.io.File

class StudentELearningAssignmentSubmissionAdapter(
    private val itemList: MutableList<AttachmentModel>,
    private val studentFileViewModel: StudentFileViewModel,
    private val fragmentManager: FragmentManager,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<StudentELearningAssignmentSubmissionAdapter.FileViewHolder>() {

    private val picasso = Picasso.get()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_student_assignment_submission_layout,
            parent, false
        )

        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val itemModel = itemList[position]
        holder.bind(itemModel)
    }

    override fun getItemCount() = itemList.size

    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fileImageView: ImageView = itemView.findViewById(R.id.fileImageView)
        private val fileNameTxt: TextView = itemView.findViewById(R.id.fileNameTxt)
        private val deleteBtn: ImageButton = itemView.findViewById(R.id.deleteBtn)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

        fun bind(attachmentModel: AttachmentModel) {
            fileNameTxt.text = attachmentModel.name
            setCompoundDrawable(fileNameTxt, attachmentModel.type)

            progressBar.isVisible = attachmentModel.isNewFile

            var fileSavedPath = ""

            when (attachmentModel.type) {
                "image" -> {
                    loadImage(attachmentModel, fileImageView)
                    progressBar.isVisible = false
                }

                else -> {
                    fileSavedPath = createTempDir(itemView, attachmentModel)
                    studentFileViewModel.processFile(attachmentModel, fileSavedPath)
                }
            }

            studentFileViewModel.processedFile.observe(lifecycleOwner) { (file, bitmap) ->
                if (file.absolutePath == fileSavedPath) {
                    attachmentModel.uri = file.absolutePath
                    fileImageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    fileImageView.setImageBitmap(bitmap)
                    progressBar.isVisible = false
                }
            }


            itemView.setOnClickListener {
                viewFiles(itemView, attachmentModel)
            }
        }
    }

    private fun loadImage(attachmentModel: AttachmentModel, imageView: ImageView) {
        when (attachmentModel.uri) {
            is File -> picasso.load(attachmentModel.uri as File).into(imageView)
            is Uri -> picasso.load(attachmentModel.uri as Uri).into(imageView)
        }

        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
    }

    private fun setCompoundDrawable(textView: TextView, type: String) {
        textView.setCompoundDrawablesWithIntrinsicBounds(
            when (type) {
                "pdf" -> R.drawable.ic_pdf24
                "word" -> R.drawable.ic_file_word
                "excel" -> R.drawable.ic_file_excel
                else -> R.drawable.ic_image24
            }.let {
                ContextCompat.getDrawable(textView.context, it)
            },
            null, null, null
        )
    }

    private fun createTempDir(itemView: View, attachmentModel: AttachmentModel): String {
        val tempDir = itemView.context.cacheDir
        val name = attachmentModel.name
        val pathName = "temp_$name"
        return File(tempDir, pathName).absolutePath
    }


    private fun viewFiles(itemView: View, attachmentModel: AttachmentModel) {
        try {
            when (attachmentModel.type) {
                "pdf", "word", "excel" -> {
                    val uri = getFileUri(itemView, attachmentModel.uri.toString())
                    if (uri != null) {
                        openFileWithIntent(itemView, uri, attachmentModel.type)
                    }
                }

                else -> previewImage(attachmentModel)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getFileUri(itemView: View, fileUri: String): Uri? {
        val file = File(fileUri)
        return if (file.exists()) {
            FileProvider.getUriForFile(
                itemView.context,
                "${itemView.context.packageName}.provider",
                file
            )
        } else {
            null
        }
    }

    private fun openFileWithIntent(itemView: View, uri: Uri, fileType: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        val mimeType = when (fileType) {
            "pdf" -> "application/pdf"
            "word" -> "application/msword"
            "excel" -> "application/vnd.ms-excel"
            else -> null
        }

        if (mimeType != null)
            intent.setDataAndType(uri, mimeType)
        itemView.context.startActivity(intent)
    }

    private fun previewImage(attachmentModel: AttachmentModel) {
        AdminELearningFilePreviewDialogFragment(
            attachmentModel.uri,
            attachmentModel.name
        ).show(fragmentManager, "view file")

    }

    /*  private fun deleteAttachment(deleteButton: ImageView, position: Int) {
          deleteButton.setOnClickListener {
              if (mFrom == "edit") {
                  val deletedAttachmentModel = mFileList[position]
                  deletedAttachmentModel.name = ""
                  mDeletedFileList.add(deletedAttachmentModel)
              }

              mFileList.removeAt(position)
              if (mFileList.isEmpty()) {
                  mAttachmentTxt.isVisible = true
                  mAddAttachmentBtn.isVisible = false
                  mAttachmentBtn.isClickable = true
              }

          }
      }*/

}