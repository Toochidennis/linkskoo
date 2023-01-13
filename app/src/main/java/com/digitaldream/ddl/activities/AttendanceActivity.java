package com.digitaldream.ddl.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.digitaldream.ddl.R;
import com.digitaldream.ddl.adapters.SectionPagerAdapter;
import com.digitaldream.ddl.fragments.AdminClassAttendanceFragment;
import com.digitaldream.ddl.fragments.CourseAttendanceFragment;
import com.digitaldream.ddl.fragments.StaffClassAttendanceFragment;
import com.digitaldream.ddl.fragments.StaffCourseAttendanceFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.Locale;


public class AttendanceActivity extends AppCompatActivity {

    private TextView mName, mClassYear;
    String mStudentLevelId, mStudentClassId, mClassName, from;

    private String year, term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Attendance");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        Intent intent = getIntent();
        mStudentLevelId = intent.getStringExtra("levelId");
        mStudentClassId = intent.getStringExtra("classId");
        mClassName = intent.getStringExtra("className");
        from = intent.getStringExtra("from");

        mName = findViewById(R.id.class_name);
        mClassYear = findViewById(R.id.class_term);
        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager viewPager = findViewById(R.id.container);


        SharedPreferences sharedPreferences = getSharedPreferences(
                "loginDetail", Context.MODE_PRIVATE);
        term = sharedPreferences.getString("term", "");
        // year = mSettingModelList.get(0).getSchoolYear();
        year = sharedPreferences.getString("school_year", "");
        Log.i("term", year);

        try {
            int previousYear = Integer.parseInt(year) - 1;
            String termText = "";
            switch (term) {
                case "1":
                    termText = "First Term";
                    break;
                case "2":
                    termText = "Second Term";
                    break;
                case "3":
                    termText = "Third Term";
                    break;
            }

            SectionPagerAdapter adapter =
                    new SectionPagerAdapter(getSupportFragmentManager());

            if (from.equals("result")) {
                mName.setText(mClassName);
                mClassYear.setText(
                        String.format(Locale.getDefault(), "%d/%s %s",
                                previousYear, year, termText));

                adapter.addFragment(
                        AdminClassAttendanceFragment.newInstance(
                                mStudentClassId,
                                mStudentLevelId, mClassName, "admin"), "Class");
                adapter.addFragment(
                        CourseAttendanceFragment.newInstance(mStudentClassId,
                                mStudentLevelId), "Course");

            } else {
                mName.setText(termText);
                mClassYear.setText(String.format(Locale.getDefault(), "%d/%s",
                        previousYear, year));

                adapter.addFragment(new StaffClassAttendanceFragment(),
                        "Class");
                adapter.addFragment(new StaffCourseAttendanceFragment(),
                        "Course");
            }

            viewPager.setAdapter(adapter);

            tabLayout.setupWithViewPager(viewPager, true);

        } catch (Exception sE) {
            sE.printStackTrace();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


}