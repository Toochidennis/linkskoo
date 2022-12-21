package com.digitaldream.ddl.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.digitaldream.ddl.Fragments.AdminClassAttendanceFragment;
import com.digitaldream.ddl.Fragments.CBTCoursesFragment;
import com.digitaldream.ddl.Fragments.CBTExamTypeFragment;
import com.digitaldream.ddl.Fragments.CBTYearFragment;
import com.digitaldream.ddl.Fragments.ContactsStaff;
import com.digitaldream.ddl.Fragments.LibraryGamesFragment;
import com.digitaldream.ddl.Fragments.LibraryVideosFragment;
import com.digitaldream.ddl.Fragments.ResultStaff;
import com.digitaldream.ddl.R;

public class StaffUtils extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_utils);
        Intent i = getIntent();
        String from = i.getStringExtra("from");
        String classId = i.getStringExtra("classId");
        String levelId = i.getStringExtra("levelId");
        String className = i.getStringExtra("class_name");
        String courseName = i.getStringExtra("course_name");
        switch (from) {
            case "result":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ResultStaff()).commit();
                break;
            case "student":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ContactsStaff()).commit();
                break;
            case "staff":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        AdminClassAttendanceFragment.newInstance(classId, levelId, className, "staff")).commit();
                break;
            case "cbt":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CBTExamTypeFragment()).commit();
                break;
            case "cbt_exam_name":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CBTCoursesFragment()).commit();
                break;
            case "cbt_course":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, CBTYearFragment.newInstance(courseName, "")).commit();
                break;
            case "videos":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LibraryVideosFragment()).commit();
                break;
            case "games":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LibraryGamesFragment()).commit();
                break;


        }


    }
}
