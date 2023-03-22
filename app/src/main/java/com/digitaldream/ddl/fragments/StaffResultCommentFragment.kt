package com.digitaldream.ddl.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.digitaldream.ddl.R
import com.digitaldream.ddl.activities.Login


private const val ARG_PARAM1 = "param1"


class StaffResultCommentFragment : Fragment() {

    private var mClassId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mClassId = it.getString(ARG_PARAM1)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(sClassId: String) =
            StaffResultCommentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, sClassId)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_staff_result_comment, container, false)

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)

        toolbar.apply {
            setNavigationIcon(R.drawable.arrow_left)
            toolbar.title = "Comment"
            toolbar.setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        }

        loadComment(view)

        return view
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadComment(
        sView: View,
    ) {
        val webView: WebView = sView.findViewById(R.id.web_view)
        val sharedPreferences = requireContext().getSharedPreferences(
            "loginDetail",
            Context.MODE_PRIVATE
        )
        val staffId = sharedPreferences.getString("user_id", "")
        val db = sharedPreferences.getString("db","")

        val url = "${Login.urlBase}/addComment.php?staff_id=$staffId&&class=$mClassId&&_db=$db"

        webView.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            domStorageEnabled = true
        }

        webView.loadUrl(url)
    }
}