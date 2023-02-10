package com.digitaldream.winskool.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import com.digitaldream.winskool.R

class DownloadReceiptDialog(
    mContext: Context, private var sInputListener:
    OnInputListener
) : Dialog(mContext) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.attributes.windowAnimations = R.style.DialogAnimation
        window!!.setGravity(Gravity.BOTTOM)

        setContentView(R.layout.dialog_receipt_download)

        val cancelBtn: ImageView = findViewById(R.id.cancel_button)
        val shareBtn: Button = findViewById(R.id.share_receipt)
        val downloadBtn: Button = findViewById(R.id.download_receipt)


        cancelBtn.setOnClickListener {
            dismiss()
        }

        shareBtn.setOnClickListener {
            sInputListener.sendInput(shareBtn.text.toString())
            dismiss()
        }

        downloadBtn.setOnClickListener {
            sInputListener.sendInput(downloadBtn.text.toString())
            dismiss()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        try {
            sInputListener = context as OnInputListener
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }
}