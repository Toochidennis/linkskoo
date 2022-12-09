package com.digitaldream.ddl.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.digitaldream.ddl.Adapters.SectionPagerAdapter;
import com.digitaldream.ddl.Fragments.AdminClassAttendanceFragment;
import com.digitaldream.ddl.Fragments.CourseAttendanceFragment;
import com.digitaldream.ddl.Fragments.StaffClassAttendanceFragment;
import com.digitaldream.ddl.Fragments.StaffCourseAttendanceFragment;
import com.digitaldream.ddl.R;
import com.google.android.material.tabs.TabLayout;


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


        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        term = sharedPreferences.getString("term", "");
        // year = mSettingModelList.get(0).getSchoolYear();
        year = sharedPreferences.getString("school_year", "");
        Log.i("term", year);

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
            mClassYear.setText(String.format("%d/%s %s", previousYear, year,
                    termText));

            adapter.addFragment(AdminClassAttendanceFragment.newInstance(mStudentClassId, mStudentLevelId, mClassName, "admin"), "Class");
            adapter.addFragment(CourseAttendanceFragment.newInstance(mStudentClassId, mStudentLevelId), "Course");

        } else {
            mName.setText(termText);
            mClassYear.setText(String.format("%d/%s", previousYear, year));

            adapter.addFragment(new StaffClassAttendanceFragment(), "Class");
            adapter.addFragment(new StaffCourseAttendanceFragment(), "Course");
        }

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager, true);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }


}