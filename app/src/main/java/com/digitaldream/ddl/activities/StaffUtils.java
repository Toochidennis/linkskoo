package com.digitaldream.ddl.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.digitaldream.ddl.R;
import com.digitaldream.ddl.fragments.AdminClassAttendanceFragment;
import com.digitaldream.ddl.fragments.CBTCoursesFragment;
import com.digitaldream.ddl.fragments.CBTExamTypeFragment;
import com.digitaldream.ddl.fragments.CBTYearFragment;
import com.digitaldream.ddl.fragments.StaffFormClassFragment;
import com.digitaldream.ddl.fragments.LibraryGamesFragment;
import com.digitaldream.ddl.fragments.LibraryVideosFragment;
import com.digitaldream.ddl.fragments.ResultStaff;
import com.digitaldream.ddl.fragments.StaffFormClassStudentsFragment;
import com.digitaldream.ddl.fragments.StaffResultCommentFragment;
import com.digitaldream.ddl.fragments.StaffSkillsBehaviourFragment;

public class StaffUtils extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_utils);

        Intent i = getIntent();
        String classId = i.getStringExtra("classId");
        String levelId = i.getStringExtra("levelId");
        String className = i.getStringExtra("class_name");
        String courseName = i.getStringExtra("course_name");

        switch (i.getStringExtra("from")) {

            case "result":
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.payment_container, new ResultStaff()).commit();
                break;

            case "student":
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.payment_container, new StaffFormClassFragment()).commit();
                break;

            case "staff":
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.payment_container,
                        AdminClassAttendanceFragment.newInstance(classId,
                                levelId, className, "staff")).commit();
                break;

            case "cbt":
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.payment_container,
                        new CBTExamTypeFragment()).commit();
                break;

            case "exam_type":
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.payment_container,
                        new CBTCoursesFragment()).commit();
                break;

            case "cbt_course":
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.payment_container,
                        CBTYearFragment.newInstance(courseName, "")).commit();
                break;

            case "videos":
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.payment_container,
                        new LibraryVideosFragment()).commit();
                break;

            case "games":
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.payment_container,
                        new LibraryGamesFragment()).commit();
                break;

            case "form_class":
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.payment_container,
                        StaffFormClassStudentsFragment.newInstance(classId)).commit();
                break;

            case "staff_comment":
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.payment_container,
                        StaffResultCommentFragment.newInstance(classId)).commit();
                break;

            case "skills_behaviour":
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.payment_container,
                        StaffSkillsBehaviourFragment.newInstance(classId)).commit();
                break;
        }


    }
}
