package com.digitaldream.linkskool.dialog

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.digitaldream.linkskool.R
import com.squareup.picasso.Picasso


class AdminELearningFilePreviewDialogFragment(
    private val file: Any?,
    private val fileName: String
) : DialogFragment(R.layout.fragment_admin_e_learning_file_preview) {

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)
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
        when (file) {
            is String -> Picasso.get().load(file).into(imageView)
            is Bitmap -> imageView.setImageBitmap(file)
        }

    }

}