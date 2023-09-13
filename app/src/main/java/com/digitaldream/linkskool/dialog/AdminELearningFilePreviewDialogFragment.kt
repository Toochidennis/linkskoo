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

class AdminELearningFilePreviewDialogFragment :
    DialogFragment(R.layout.fragment_admin_e_learning_file_preview) {

    private lateinit var imageView: ImageView

    private var fileName: String? = null
    private var filePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)

        arguments?.let {
            fileName = it.getString(ARG_PARAM1)
            filePath = it.getString(ARG_PARAM2)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(fileName: String, filePath: String) =
            AdminELearningFilePreviewDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, fileName)
                    putString(ARG_PARAM2, filePath)
                }

            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpView(view)

        loadImage()

    }

    private fun setUpView(view: View) {
        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            imageView = findViewById(R.id.imageView)

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

}