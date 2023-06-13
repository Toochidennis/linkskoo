package com.digitaldream.linkskool.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.fragments.AdminELearningTopicsFragment

class ELearningActivity : AppCompatActivity(R.layout.activity_elearn) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (intent.getStringExtra("from")) {
            "view_post" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.learning_container,
                        AdminELearningTopicsFragment()
                    )
                }
            }
        }
    }

}