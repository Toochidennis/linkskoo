package com.digitaldream.ddl.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitaldream.ddl.activities.StaffEnterResult;
import com.digitaldream.ddl.DatabaseHelper;
import com.digitaldream.ddl.models.CourseTable;
import com.digitaldream.ddl.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResultStaff extends Fragment {
    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private Dao<CourseTable, Long> courseDao;
    private LinearLayout emptyState;
    private List<CourseTable> courseList;
    private Toolbar toolbar;
    private TextView mTerm;
    private SectionedRecyclerViewAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result_staff, container, false);

        setHasOptionsMenu(true);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle("Results");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(
                "loginDetail", Context.MODE_PRIVATE);
        String term = sharedPreferences.getString("term", "");
        String year = sharedPreferences.getString("school_year", "");

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

        mTerm = view.findViewById(R.id.term);
        mTerm.setText(String.format("%d/%s %s", previousYear, year,
                termText));


        databaseHelper = new DatabaseHelper(getContext());

        emptyState = view.findViewById(R.id.staff_studentResult_empty_state);
        recyclerView = view.findViewById(R.id.staff_course_results);
        adapter = new SectionedRecyclerViewAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        adapter.removeAllSections();
        try {
            courseDao = DaoManager.createDao(databaseHelper.getConnectionSource(), CourseTable.class);
            courseList = courseDao.queryForAll();



            List<String> stringList = new ArrayList<>();

            Collections.sort(courseList, (s1, s2) ->
                    s1.getCourseName().substring(0, 1)
                            .compareToIgnoreCase(s2.getCourseName().substring(0, 1)));

            for (int i = 0; i < courseList.size(); i++) {

                Log.i("course name", courseList.get(i).getCourseName());

                List<CourseTable> courseTable = getCourseTableList(courseList.get(i).getCourseName());

                String courseName = courseList.get(i).getCourseName();
                String[] strArray = courseName.split(" ");
                StringBuilder stringBuilder = new StringBuilder();
                for (String s : strArray) {
                    try {
                        String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                        stringBuilder.append(cap).append(" ");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (!stringList.contains(courseList.get(i).getCourseName())) {
                    adapter.addSection(new SectionAdapter(getContext(),
                            courseTable, stringBuilder.toString()));
                    stringList.add(courseList.get(i).getCourseName());
                }

            }

            if (!courseList.isEmpty()) {
                recyclerView.setAdapter(adapter);
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyState.setVisibility(View.VISIBLE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


    public List<CourseTable> getCourseTableList(String sCourseName) throws SQLException {

        QueryBuilder<CourseTable, Long> queryBuilder =
                courseDao.queryBuilder();
        queryBuilder.where().eq("courseName", sCourseName);
        return queryBuilder.query();

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

    }


    public static class SectionAdapter extends Section {

        Context mContext;
        private final List<CourseTable> mCourseTableList;
        private final String headerTitle;

        public SectionAdapter(Context sContext,
                              List<CourseTable> sCourseTableList,
                              String sHeaderTitle) {
            super(SectionParameters.builder()
                    .itemResourceId(R.layout.activity_staff_course_by_subject)
                    .headerResourceId(R.layout.head)
                    .build());
            mContext = sContext;
            mCourseTableList = sCourseTableList;
            headerTitle = sHeaderTitle;
        }

        @Override
        public int getContentItemsTotal() {
            return mCourseTableList.size();
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new ItemViewHolder(view);
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new HeaderViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder sViewHolder, int sI) {
            ItemViewHolder staffCourseViewHolder = (ItemViewHolder) sViewHolder;
            final CourseTable ct = mCourseTableList.get(sI);

            staffCourseViewHolder.courseClass.setText(ct.getClassName().toUpperCase());

            staffCourseViewHolder.addResult.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, StaffEnterResult.class);
                intent.putExtra("status", "add_result");
                intent.putExtra("course_id", ct.getCourseId());
                intent.putExtra("class_id", ct.getClassId());
                intent.putExtra("level_id", ct.getLevelId());
                mContext.startActivity(intent);
            });

            staffCourseViewHolder.viewResult.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, StaffEnterResult.class);
                intent.putExtra("status", "view_result");
                intent.putExtra("course_id", ct.getCourseId());
                intent.putExtra("class_id", ct.getClassId());
                intent.putExtra("level_id", ct.getLevelId());
                mContext.startActivity(intent);
            });

        }


        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;

            String title =
                    headerTitle.substring(0, 1).toUpperCase() + "" + headerTitle.substring(1).toLowerCase();
            headerViewHolder.mHeader.setText(title);
        }

        public static class ItemViewHolder extends RecyclerView.ViewHolder {

            private final TextView courseClass;
            private final LinearLayout viewResult;
            private final LinearLayout addResult;

            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                viewResult = itemView.findViewById(R.id.staff_view_student);
                addResult = itemView.findViewById(R.id.staff_add_student);
                courseClass = itemView.findViewById(R.id.staff_course_class);

            }


        }

        public static class HeaderViewHolder extends RecyclerView.ViewHolder {
            private final TextView mHeader;

            public HeaderViewHolder(@NonNull View itemView) {
                super(itemView);
                mHeader = itemView.findViewById(R.id.course_name);
            }
        }


    }
}
