package com.digitaldream.linkskool.fragments;


import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.activities.StudentELearningCourseOutlineActivity;
import com.digitaldream.linkskool.adapters.StudentELearningAdapter;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.CourseOutlineTable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.List;

import timber.log.Timber;


public class StudentELearningFragment extends Fragment implements StudentELearningAdapter.OnCourseClickListener {
    private RecyclerView recyclerView;

    private List<CourseOutlineTable> outlineTableList;
    private DatabaseHelper databaseHelper;

    private String levelId;

    public StudentELearningFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_elearning, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpViews(view);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(
                "loginDetail", MODE_PRIVATE
        );
        levelId = sharedPreferences.getString("level", "");

        databaseHelper = new DatabaseHelper(requireActivity());

        loadCourses();
    }

    private void setUpViews(View sView) {
        recyclerView = sView.findViewById(R.id.studentELearningRecyclerView);
        Toolbar mToolbar = sView.findViewById(R.id.toolbar);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbar);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Classroom");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }


    private void loadCourses() {
        try {
            Dao<CourseOutlineTable, Long> mLongCourseOutlineTableDao =
                    DaoManager.createDao(databaseHelper.getConnectionSource(), CourseOutlineTable.class);
            QueryBuilder<CourseOutlineTable, Long> mQueryBuilder =
                    mLongCourseOutlineTableDao.queryBuilder();
            mQueryBuilder.groupBy("courseId").where().eq("levelId", levelId);
            outlineTableList = mQueryBuilder.query();

            setUpRecyclerView();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpRecyclerView() {
        StudentELearningAdapter mStudentElearningCourseAdapter =
                new StudentELearningAdapter(outlineTableList, this);

        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(mStudentElearningCourseAdapter);
    }


    @Override
    public void onCourseClick(int position) {
        startActivity(
                new Intent(requireActivity(), StudentELearningCourseOutlineActivity.class)
                        .putExtra("courseName", outlineTableList.get(position).getCourseName())
                        .putExtra("courseId", outlineTableList.get(position).getCourseId())
        );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if ((item.getItemId()) == android.R.id.home) {
            requireActivity().onBackPressed();
            return true;
        }
        return false;
    }

}
