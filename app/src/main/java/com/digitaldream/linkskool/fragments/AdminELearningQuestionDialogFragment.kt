package com.digitaldream.linkskool.fragments

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.GenericAdapter
import com.digitaldream.linkskool.models.OptionsModel
import java.util.Optional


class AdminELearningQuestionDialogFragment :
    DialogFragment(R.layout.fragment_admin_e_learning_multi_choice) {

    private lateinit var mAdapter: GenericAdapter<OptionsModel>
    private val optionList = mutableListOf<OptionsModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            val dismissBtn: ImageView = findViewById(R.id.close_btn)
            val askBtn: Button = findViewById(R.id.ask_btn)
            val questionEditText: EditText = findViewById(R.id.question_edit)
            val optionsRecyclerview: RecyclerView = findViewById(R.id.options_recyclerview)
            val attachmentRecyclerView: RecyclerView = findViewById(R.id.attachment_recyclerview)
            val attachmentBtn: RelativeLayout = findViewById(R.id.attachment_btn)
            val addOptionBtn: TextView = findViewById(R.id.add_option_btn)

            optionList.add(OptionsModel().apply {
                optionText = "Option 1"
            })

            var selectedPosition = RecyclerView.NO_POSITION
            var count = 1

            mAdapter = GenericAdapter(
                optionList,
                R.layout.fragment_admin_e_learning_multi_choice_item,
                bindItem = { itemView, model, position ->
                    val radioButton: RadioButton =
                        itemView.findViewById(R.id.radioButtonOption)
                    val editText: EditText =
                        itemView.findViewById(R.id.editTextAnswer)
                    val removeButton: ImageView =
                        itemView.findViewById(R.id.removeOptionButton)


                    editText.setOnTouchListener { v, event ->
                        v.parent.requestDisallowInterceptTouchEvent(true)
                        when (event.action and MotionEvent.ACTION_MASK) {
                            MotionEvent.ACTION_SCROLL -> {
                                v.parent.requestDisallowInterceptTouchEvent(false)
                                true
                            }

                            else -> false
                        }
                    }


                    editText.setText(model.optionText)
                    radioButton.isChecked = position == selectedPosition
                    radioButton.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            selectedPosition = position
                            println("position $selectedPosition")
                            if (!optionsRecyclerview.isComputingLayout && optionsRecyclerview.scrollState == RecyclerView
                                    .SCROLL_STATE_IDLE
                            ) {
                                mAdapter.notifyDataSetChanged()
                            }

                        }
                    }

                },
                onItemClick = {}
            )

            val temAnimator = DefaultItemAnimator()
            temAnimator.supportsChangeAnimations = true

            optionsRecyclerview.apply {
                itemAnimator = temAnimator
                layoutManager = LinearLayoutManager(requireContext())
                adapter = mAdapter
                hasFixedSize()
                isAnimating
            }


            addOptionBtn.setOnClickListener {
                optionList.add(OptionsModel().apply {
                    count += 1
                    optionText = "Option $count"
                    mAdapter.notifyItemInserted(optionList.size - 1)
                    mAdapter.notifyDataSetChanged()
                })
            }


            attachmentBtn.setOnClickListener {

            }

        }
    }


}
