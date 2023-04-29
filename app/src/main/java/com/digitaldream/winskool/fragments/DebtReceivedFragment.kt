package com.digitaldream.winskool.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.DebtReceivedAdapter
import com.digitaldream.winskool.dialog.AdminClassesDialog
import com.digitaldream.winskool.dialog.OnInputListener
import com.digitaldream.winskool.interfaces.ResultListener
import com.digitaldream.winskool.models.StudentTable
import com.digitaldream.winskool.utils.FunctionUtils.requestToServer
import com.digitaldream.winskool.utils.VolleyCallback

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val CLASS_NAME = "name"
private const val CLASS_ID = "id"
private const val FROM = "from"

class DebtReceivedFragment : Fragment() {


    //declare view variables
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: DebtReceivedAdapter
    private lateinit var mTitle: TextView
    private lateinit var mClassBtn: Button
    private lateinit var mErrorMessage: TextView
    private lateinit var mRefreshBtn: Button
    private lateinit var mErrorImage: ImageView
    private lateinit var mSearchBar: EditText
    private lateinit var mBackBtn: ImageView


    private val mList = mutableListOf<StudentTable>()


    private var mClassName: String? = null
    private var mClassId: String? = null
    private var mFrom: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mClassName = it.getString(CLASS_NAME)
            mClassId = it.getString(CLASS_ID)
            mFrom = it.getString(FROM)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(sClassName: String, sClassId: String, sFrom: String) =
            DebtReceivedFragment().apply {
                arguments = Bundle().apply {
                    putString(CLASS_NAME, sClassName)
                    putString(CLASS_ID, sClassId)
                    putString(FROM, sFrom)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_debt_received, container, false)

        //initialise variables
        mRecyclerView = view.findViewById(R.id.debt_receipt_recycler)
        mTitle = view.findViewById(R.id.toolbar_text)
        mClassBtn = view.findViewById(R.id.class_name_btn)
        mErrorImage = view.findViewById(R.id.error_image)
        mErrorMessage = view.findViewById(R.id.error_message)
        mRefreshBtn = view.findViewById(R.id.refresh_btn)
        mSearchBar = view.findViewById(R.id.search_bar)
        mBackBtn = view.findViewById(R.id.back_btn)


        mBackBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        mClassBtn.text = mClassName

        println(mFrom)


        if (mFrom == "debt") {
            "Debt".also { mTitle.text = it }
        } else "Received".also { mTitle.text = it }

        if (mFrom == "debt") {
            getDebtStudents(mClassId!!)
        } else getPaidStudents(mClassId!!)


        changeClass()

        filterNames()

        return view
    }


    // change class
    private fun changeClass() {
        mClassBtn.setOnClickListener {
            AdminClassesDialog(requireContext(),
                "payment",
                "changeLevel",
                object : ResultListener {
                    override fun sendClassName(sName: String) {
                        mClassBtn.text = sName

                    }

                    override fun sendLevelId(sLevelId: String) {

                    }

                    override fun sendClassId(sClassId: String) {

                        if (mFrom == " debt") {
                            getDebtStudents(sClassId)
                        } else getPaidStudents(sClassId)

                    }
                })
                .apply {
                    setCancelable(true)
                    show()
                }.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

        }

    }


    // search for a student
    private fun filterNames() {
        mSearchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val name = mutableListOf<StudentTable>()
                mList.forEach {
                    if (it.studentFullName.lowercase().contains(s.toString().lowercase()))
                        name.add(it)
                }

                mAdapter.updateFilterList(name)
            }
        })
    }


    // get names of students that have paid in a class
    private fun getPaidStudents(classId: String) {
        val hashMap = hashMapOf<String, String>()
        val url = "${getString(R.string.base_url)}/manageReceipts.php?class=$classId"

        requestToServer(Request.Method.GET, url, requireActivity(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    println("This is my response $response")
                }

                override fun onError(error: VolleyError) {

                }
            })
    }

    // get names of students owing in a class
    private fun getDebtStudents(classId: String) {
        val hashMap = hashMapOf<String, String>()
        val url = "${getString(R.string.base_url)}/manageReceipts.php?debt_class=$classId"

        requestToServer(Request.Method.GET, url, requireActivity(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    println("This is my $response")
                }

                override fun onError(error: VolleyError) {

                }
            })
    }


}