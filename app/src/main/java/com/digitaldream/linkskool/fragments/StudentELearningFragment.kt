package com.digitaldream.linkskool.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Orientation
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.StudentELearningCourseOutlineActivity
import com.digitaldream.linkskool.adapters.GenericAdapter
import com.digitaldream.linkskool.adapters.StudentELearningUpcomingQuizAdapter
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.models.CourseOutlineTable
import com.digitaldream.linkskool.models.RecentActivityModel
import com.digitaldream.linkskool.models.UpcomingQuizModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudentELearningFragment : Fragment() {

    private lateinit var courseRecyclerView: RecyclerView
    private lateinit var upcomingQuizViewPager: ViewPager2
    private lateinit var recentActivityRecyclerView: RecyclerView
    private lateinit var upcomingQuizTabLayout: TabLayout

    private lateinit var courseAdapter: GenericAdapter<CourseOutlineTable>
    private lateinit var upcomingQuizAdapter: StudentELearningUpcomingQuizAdapter
    private lateinit var recentActivityAdapter: GenericAdapter<RecentActivityModel>

    private lateinit var databaseHelper: DatabaseHelper
    private var autoSlidingJob: Job? = null
    private val delayMillis = 3000L

    private val upcomingQuizList = mutableListOf<UpcomingQuizModel>()
    private val recentActivityList = mutableListOf<RecentActivityModel>()

    private var outlineTableList = mutableListOf<CourseOutlineTable>()
    private var levelId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_e_learning, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        loadCourses()

        setUpUpcomingQuizAdapter()
        setUpRecentActivityAdapter()
    }

    private fun setUpViews(view: View) {
        view.apply {
            val toolbar: Toolbar = view.findViewById(R.id.toolbar)
            upcomingQuizViewPager = findViewById(R.id.quizViewPager)
            upcomingQuizTabLayout = findViewById(R.id.quizTabLayout)
            recentActivityRecyclerView = findViewById(R.id.recentActivityRecyclerView)
            courseRecyclerView = view.findViewById(R.id.courseRecyclerView)

            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            val actionBar = (requireActivity() as AppCompatActivity).supportActionBar

            actionBar?.apply {
                title = "Classroom"
                setHomeButtonEnabled(true)
                setDisplayHomeAsUpEnabled(true)
            }

            toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        }

        val sharedPreferences = requireActivity().getSharedPreferences(
            "loginDetail", Context.MODE_PRIVATE
        )

        levelId = sharedPreferences.getString("level", "")
        databaseHelper = DatabaseHelper(requireActivity())

    }

    private fun loadCourses() {
        try {
            val mLongCourseOutlineTableDao =
                DaoManager.createDao<Dao<CourseOutlineTable, Long>, CourseOutlineTable>(
                    databaseHelper.connectionSource, CourseOutlineTable::class.java
                )
            val mQueryBuilder = mLongCourseOutlineTableDao.queryBuilder()
            mQueryBuilder.groupBy("courseId").where().eq("levelId", levelId)
            outlineTableList = mQueryBuilder.query()

            setUpCourseAdapter()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpCourseAdapter() {
        courseAdapter = GenericAdapter(
            outlineTableList,
            R.layout.item_student_e_learning_course_layout,
            bindItem = { itemView, model, _ ->
                val courseCardView: CardView = itemView.findViewById(R.id.courseCardView)
                val courseImageView: ImageView = itemView.findViewById(R.id.courseImageView)
                val courseNameTxt: TextView = itemView.findViewById(R.id.courseNameTxt)

                courseNameTxt.text = model.courseName

            }
        ) { position ->
            startActivity(
                Intent(requireActivity(), StudentELearningCourseOutlineActivity::class.java)
                    .putExtra("courseName", outlineTableList[position].courseName)
                    .putExtra("courseId", outlineTableList[position].courseId)
            )
        }

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        courseRecyclerView.hasFixedSize()
        courseRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        courseRecyclerView.adapter = courseAdapter
    }

    private fun setUpUpcomingQuizAdapter() {
        upcomingQuizList.apply {
            add(UpcomingQuizModel("1", "English language", "First C/A test", "02 October, 2023"))
            add(UpcomingQuizModel("2", "Mathematics", "First C/A test", "02 October, 2023"))
            add(UpcomingQuizModel("3", "Computer Science", "Examination", "02 October, 2023"))
            add(UpcomingQuizModel("4", "Catering", "Second C/A test", "02 October, 2023"))
        }

        upcomingQuizAdapter = StudentELearningUpcomingQuizAdapter(upcomingQuizList)
        upcomingQuizViewPager.adapter = upcomingQuizAdapter

        TabLayoutMediator(upcomingQuizTabLayout, upcomingQuizViewPager) { tab, position ->
            val customView = LayoutInflater.from(requireContext())
                .inflate(R.layout.custom_tab_layout, null)
            val tabIcon: ImageView = customView.findViewById(R.id.tabIcon)

            if (position == upcomingQuizViewPager.currentItem) {
                tabIcon.setImageResource(R.drawable.selected_indicator)
            } else {
                tabIcon.setImageResource(R.drawable.default_unselected_dot)
            }

            tab.customView = customView
        }.attach()

        upcomingQuizViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                upcomingQuizViewPager.currentItem = position
            }
        })
    }


    private fun setUpRecentActivityAdapter() {
        recentActivityList.apply {
            add(RecentActivityModel("1", "Catering", "ToochiDennis", "Catering assignment"))
            add(RecentActivityModel("1", "Arts", "Sxmtee", "Arts Material"))
            add(RecentActivityModel("1", "Maths", "SofaHead", "Maths assignment"))
        }

        recentActivityAdapter = GenericAdapter(
            recentActivityList,
            R.layout.item_recent_activity_layout,
            bindItem = { itemView, model, _ ->
                val userNameTxt: TextView = itemView.findViewById(R.id.userNameTxt)
                val commentTxt: TextView = itemView.findViewById(R.id.commentTxt)

                userNameTxt.text = model.name
                "Commented on ${model.title}".let { commentTxt.text = it }
            }
        ) {

        }

        setUpRecentActivityRecyclerView()
    }

    private fun setUpRecentActivityRecyclerView() {
        recentActivityRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = recentActivityAdapter
        }
    }

    private fun startAutoSliding() {
        autoSlidingJob = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                delay(delayMillis)

                withContext(Dispatchers.Main) {
                    // Upcoming quiz
                    if (upcomingQuizViewPager.currentItem < upcomingQuizList.size - 1) {
                        upcomingQuizViewPager.currentItem++
                    } else {
                        upcomingQuizViewPager.currentItem = 0
                    }

                    val position = upcomingQuizViewPager.currentItem
                    upcomingQuizTabLayout.selectTab(upcomingQuizTabLayout.getTabAt(position))
                }
            }
        }
    }

    private fun stopAutoSliding() {
        autoSlidingJob?.cancel()
        autoSlidingJob = null
    }


    override fun onResume() {
        super.onResume()
        startAutoSliding()
    }

    override fun onPause() {
        super.onPause()
        stopAutoSliding()
    }

}