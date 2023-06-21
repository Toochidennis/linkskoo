package com.digitaldream.linkskool.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.fragments.*


class ELearningActivity : AppCompatActivity(R.layout.activity_elearn) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val levelId =  intent.getStringExtra("levelId")
        val courseId =  intent.getStringExtra("courseId")

        when (intent.getStringExtra("from")) {
            "view_post" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.learning_container,
                        AdminELearningTopicsFragment.newInstance(levelId!!, courseId!!)
                    )
                }
            }

            "assignment"->{
                supportFragmentManager.commit {
                    replace(
                        R.id.learning_container,
                        AdminELearningAssignmentFragment.newInstance(levelId!!, courseId!!)
                    )
                }
            }

            "question"->{
                supportFragmentManager.commit {
                    replace(
                        R.id.learning_container,
                        AdminELearningQuestionSettingsFragment.newInstance(levelId!!, courseId!!)
                    )
                }
            }

            "material"->{
                supportFragmentManager.commit {
                    replace(
                        R.id.learning_container,
                        AdminELearningMaterialFragment()
                    )
                }
            }

        }
    }

}