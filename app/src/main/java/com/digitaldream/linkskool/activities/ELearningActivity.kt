package com.digitaldream.linkskool.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.dialog.AdminELearningCreateTopicFragment
import com.digitaldream.linkskool.fragments.AdminELearningAssignmentFragment
import com.digitaldream.linkskool.fragments.AdminELearningAssignmentPreviewFragment
import com.digitaldream.linkskool.fragments.AdminELearningCourseOutlineFragment
import com.digitaldream.linkskool.fragments.AdminELearningMaterialFragment
import com.digitaldream.linkskool.fragments.AdminELearningQuestionSettingsFragment


class ELearningActivity : AppCompatActivity(R.layout.activity_elearn) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val levelId = intent.getStringExtra("levelId")
        val courseId = intent.getStringExtra("courseId")
        val courseName = intent.getStringExtra("courseName")
        val json = intent.getStringExtra("json")

        when (intent.getStringExtra("from")) {
            "view_post" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.learning_container,
                        AdminELearningCourseOutlineFragment.newInstance(
                            levelId!!, courseId!!,
                            courseName!!
                        )
                    )
                }
            }

            "question" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.learning_container,
                        AdminELearningQuestionSettingsFragment.newInstance(
                            levelId!!, courseId!!, json!!, "", courseName!!,
                        )
                    )
                }
            }

            "material" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.learning_container,
                        AdminELearningMaterialFragment
                            .newInstance(
                                levelId!!, courseId!!,
                                json!!, courseName!!
                            )
                    )
                }
            }

            "assignment" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.learning_container,
                        AdminELearningAssignmentFragment
                            .newInstance(
                                levelId!!, courseId!!,
                                json!!, courseName!!
                            )
                    )
                }
            }

            "topic" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.learning_container,
                        AdminELearningCreateTopicFragment
                            .newInstance(
                                courseId!!,
                                levelId!!,
                                courseName!!,
                                json!!
                            )
                    )
                }
            }

            "assignment_preview"->{
                supportFragmentManager.commit {
                    replace(
                        R.id.learning_container,
                        AdminELearningAssignmentPreviewFragment()
                    )
                }
            }
        }
    }
}