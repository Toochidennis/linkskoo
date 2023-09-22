package com.digitaldream.linkskool.adapters

import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.dialog.AdminELearningFilePreviewDialogFragment
import com.digitaldream.linkskool.models.AttachmentModel
import com.digitaldream.linkskool.utils.FileViewModel
import com.squareup.picasso.Picasso
import java.io.File

class AdminELearningFilesAdapter(
    private val fragmentManager: FragmentManager,
    private val itemList: MutableList<AttachmentModel>,
    private val fileViewModel: FileViewModel
) : RecyclerView.Adapter<AdminELearningFilesAdapter.FilesViewHolder>() {

    private val picasso = Picasso.get()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_files_layout,
            parent, false
        )
        return FilesViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilesViewHolder, position: Int) {
        val attachmentModel = itemList[position]
        holder.bind(attachmentModel)
    }

    override fun getItemCount() = itemList.size

    inner class FilesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fileImageView: ImageView = itemView.findViewById(R.id.fileImageView)
        private val fileNameTxt: TextView = itemView.findViewById(R.id.fileNameTxt)

        fun bind(attachmentModel: AttachmentModel) {
            fileNameTxt.text = attachmentModel.name
            setCompoundDrawable(fileNameTxt, attachmentModel.type)
            var fileSavePath = ""

            when (attachmentModel.type) {
                "image" -> loadImage(itemView, attachmentModel, fileImageView)
                "url" -> loadUrl(itemView, fileImageView)
                else -> {
                    fileSavePath = createTempDir(itemView, attachmentModel)

                    fileViewModel.downloadAndProcessFile(attachmentModel, fileSavePath)
                }
            }

            fileViewModel.fileProcessed.observe(itemView.context as LifecycleOwner) { (file, bitmap) ->
                if (file.absolutePath == fileSavePath) {
                    fileImageView.setImageBitmap(bitmap)
                    fileImageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    attachmentModel.uri = file.absolutePath
                }
            }

            itemView.setOnClickListener {
                viewFiles(itemView, attachmentModel)
            }
        }
    }

    private fun loadImage(
        itemView: View, attachmentModel: AttachmentModel, imageView: ImageView
    ) {
        val fileURL = "${itemView.context.getString(R.string.base_url)}/${attachmentModel.uri}"

        picasso.load(fileURL).into(imageView)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
    }

    private fun loadUrl(itemView: View, imageView: ImageView) {
        imageView.apply {
            setColorFilter(
                ContextCompat.getColor(itemView.context, R.color.test_color_7),
                PorterDuff.Mode.SRC_IN
            )

            setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_link))
        }
    }


    private fun createTempDir(itemView: View, attachmentModel: AttachmentModel): String {
        val tempDir = itemView.context.cacheDir
        val name = attachmentModel.name
        val pathName = "temp_$name"
        return File(tempDir, pathName).absolutePath
    }

    private fun setCompoundDrawable(textView: TextView, type: String) {
        textView.setCompoundDrawablesWithIntrinsicBounds(
            when (type) {
                "image" -> R.drawable.ic_image24
                "video" -> R.drawable.ic_video24
                "pdf" -> R.drawable.ic_pdf24
                "word" -> R.drawable.ic_file_word
                "excel" -> R.drawable.ic_file_excel
                "unknown" -> R.drawable.ic_unknown_document24
                "url" -> R.drawable.ic_link
                else -> R.drawable.ic_document24
            }.let {
                ContextCompat.getDrawable(textView.context, it)
            },
            null, null, null
        )
    }

    private fun viewFiles(itemView: View, attachmentModel: AttachmentModel) {
        try {
            val uri = when (attachmentModel.type) {
                "video", "pdf", "word", "excel" -> getFileUri(
                    itemView,
                    attachmentModel.uri.toString()
                )

                "url" -> Uri.parse(attachmentModel.uri.toString())
                else -> null
            }

            if (uri != null) {
                openFileWithIntent(itemView, uri, attachmentModel.type)
            } else {
                previewImage(itemView, attachmentModel)
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
            "video" -> "video/*"
            "pdf" -> "application/pdf"
            "word" -> "application/msword"
            "excel" -> "application/vnd.ms-excel"
            "url" -> uri
            else -> null
        }

        if (mimeType != null)
            if (mimeType is String) {
                intent.setDataAndType(uri, mimeType)
                itemView.context.startActivity(intent)
            } else {
                itemView.context.startActivity(
                    Intent(Intent.ACTION_VIEW, mimeType as Uri)
                )
            }
    }

    private fun previewImage(itemView: View, attachmentModel: AttachmentModel) {
        val filePath = when (attachmentModel.type) {
            "image" -> "${itemView.context.getString(R.string.base_url)}/${attachmentModel.uri}"
            else -> null
        }

        if (filePath != null) {
            AdminELearningFilePreviewDialogFragment(
                filePath,
                attachmentModel.name
            ).show(fragmentManager, "view file")
        } else {
            Toast.makeText(itemView.context, "Unable to open file", Toast.LENGTH_SHORT).show()
        }

    }

}