package com.digitaldream.linkskool.adapters

import android.media.MediaMetadataRetriever
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.models.AttachmentModel
import com.digitaldream.linkskool.utils.FileRequest
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AdminELearningFilesAdapter(
    private val itemList: MutableList<AttachmentModel>
) : RecyclerView.Adapter<AdminELearningFilesAdapter.FilesViewHolder>() {

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

            fileRequest(
                itemView,
                attachmentModel,
                imageView = fileImageView,
                textView = fileNameTxt
            )
        }
    }

    private fun loadImage(
        itemView: View, attachmentModel: AttachmentModel, imageView: ImageView,
        textView: TextView
    ) {
        val fileURL = "${itemView.context.getString(R.string.base_url)}/${attachmentModel.uri}"

        Picasso.get().load(fileURL).into(imageView)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
    }
    //http://linkskool.com/developmentportal/api../assets/elearning/practice/VID-20230828-WA0015.mp4
    private fun fileRequest(
        itemView: View, attachmentModel: AttachmentModel, imageView:
        ImageView, textView: TextView
    ) {
        val fileURL = "${itemView.context.getString(R.string.base_url)}/${attachmentModel.uri}"
        val fileSavePath = File(
            Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .absolutePath + "/rec.jpg"
        )

        val fileRequest = FileRequest(
            Request.Method.GET,
            fileURL,
            fileSavePath.absolutePath,
            { response ->
                try {
                    Timber.tag("response").d("$response")

//                    val outputStream = FileOutputStream(fileSavePath)
//                    outputStream.write(response)
//                    outputStream.close()

                    generateVideoThumbnail(fileSavePath.absolutePath, imageView)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        ) { error ->
            Timber.tag("response").d(error)
        }

        Volley.newRequestQueue(itemView.context).add(fileRequest)

    }

    private fun generateVideoThumbnail(videoFilePath: String, imageView: ImageView) {
        Timber.tag("response").d(videoFilePath)


        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(videoFilePath)
            val frame = retriever.getFrameAtTime(0) // Get the first frame of the video
            imageView.setImageBitmap(frame)
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle any errors that may occur while generating the thumbnail
        } finally {
            retriever.release()
        }
    }

    private fun createTempDir(itemView: View, type: String): String? {
        val tempDir = itemView.context.cacheDir

        val pathName = when (type) {
            "image" -> "image.jpg"
            "video" -> "temp_video.mp4"
            "pdf" -> "temp_pdf.pdf"
            "word" -> "temp_word.docs"
            "excel" -> "temp_excel.xls"
            "csv" -> "temp_csv.csv"
            else -> ""
        }

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

}