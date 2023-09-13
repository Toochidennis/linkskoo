package com.digitaldream.linkskool.dialog

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.digitaldream.linkskool.R
import com.squareup.picasso.Picasso
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import timber.log.Timber
import java.io.File
import java.io.FileInputStream

private const val ARG_PARAM1 = "name"
private const val ARG_PARAM2 = "file"
private const val ARG_PARAM3 = "task"

class AdminELearningFilePreviewDialogFragment :
    DialogFragment(R.layout.fragment_admin_e_learning_file_preview) {

    private lateinit var imageView: ImageView
    private lateinit var webView: WebView

    private var fileName: String? = null
    private var filePath: String? = null
    private var taskType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)

        arguments?.let {
            fileName = it.getString(ARG_PARAM1)
            filePath = it.getString(ARG_PARAM2)
            taskType = it.getString(ARG_PARAM3)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(fileName: String, filePath: String, taskType: String) =
            AdminELearningFilePreviewDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, fileName)
                    putString(ARG_PARAM2, filePath)
                    putString(ARG_PARAM3, taskType)
                }

            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpView(view)

        if (taskType == "image") {
            loadImage()
        } else {
            loadExcel()
        }

    }

    private fun setUpView(view: View) {
        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            imageView = findViewById(R.id.imageView)
            webView = findViewById(R.id.webView)

            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            val actionBar = (requireActivity() as AppCompatActivity).supportActionBar

            actionBar?.apply {
                title = fileName
                setDisplayHomeAsUpEnabled(true)
                setHomeButtonEnabled(true)
            }

            toolbar.setNavigationOnClickListener {
                dismiss()
            }
        }
    }

    private fun loadImage() {
        Picasso.get().load(filePath).into(imageView)
    }

    private fun loadExcel() {
        try {
            val isFile = File(filePath!!)

            if (isFile.exists()) {
                val inputStream = FileInputStream(filePath)
                val workbook = XSSFWorkbook(inputStream)
                val sheet = workbook.getSheetAt(0)

                inputStream.close()

                val htmlTable = StringBuilder("<table>")
                for (row in sheet) {
                    htmlTable.append("<tr>")
                    for (cell in row) {
                        htmlTable.append("<td>").append(cell.toString()).append("</td>")
                    }
                    htmlTable.append("</tr>")
                }
                htmlTable.append("</table>")

                Timber.tag("response").d(htmlTable.toString())

                webView.loadDataWithBaseURL(
                    null, htmlTable.toString(),
                    "text/html", "utf-8", null
                )
            } else {
                Toast.makeText(requireContext(), "Unable to open file", Toast.LENGTH_SHORT).show()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}