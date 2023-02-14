package com.digitaldream.winskool.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.Button
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.TermlyFeeSetupActivity

class TermFeeDialog(sContext: Context) : Dialog(sContext) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.attributes.windowAnimations = R.style.DialogAnimation
        window!!.setGravity(Gravity.BOTTOM)
        setContentView(R.layout.dialog_fee_term)

        val jss1Btn: Button = findViewById(R.id.jss_1)
        val jss2Btn: Button = findViewById(R.id.jss_2)
        val jss3Btn: Button = findViewById(R.id.jss_3)
        val sss1Btn: Button = findViewById(R.id.sss_1)
        val sss2Btn: Button = findViewById(R.id.sss_2)
        val sss3Btn: Button = findViewById(R.id.sss_3)
        
        jss1Btn.setOnClickListener {
            context.startActivity(
                Intent(context, TermlyFeeSetupActivity().javaClass)
                    .putExtra("term_text", jss1Btn.text)
            )
            dismiss()
        }

        jss2Btn.setOnClickListener {
            context.startActivity(
                Intent(context, TermlyFeeSetupActivity().javaClass)
                    .putExtra("term_text", jss2Btn.text)
            )
            dismiss()
        }

        jss3Btn.setOnClickListener {
            context.startActivity(
                Intent(context, TermlyFeeSetupActivity().javaClass)
                    .putExtra("term_text", jss3Btn.text)
            )
            dismiss()
        }

        sss1Btn.setOnClickListener {
            context.startActivity(
                Intent(context, TermlyFeeSetupActivity().javaClass)
                    .putExtra("term_text", sss1Btn.text)
            )
            dismiss()
        }

        sss2Btn.setOnClickListener {
            context.startActivity(
                Intent(context, TermlyFeeSetupActivity().javaClass)
                    .putExtra("term_text", sss2Btn.text)
            )
            dismiss()
        }

        sss3Btn.setOnClickListener {
            context.startActivity(
                Intent(context, TermlyFeeSetupActivity().javaClass)
                    .putExtra("term_text", sss3Btn.text)
            )
            dismiss()
        }
    }

}