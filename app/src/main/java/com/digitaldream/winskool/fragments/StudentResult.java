package com.digitaldream.winskool.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digitaldream.winskool.R;
import com.digitaldream.winskool.adapters.StudentResultDownloadAdapter;
import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.models.StudentResultDownloadTable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.util.List;


public class StudentResult extends Fragment implements
        StudentResultDownloadAdapter.OnStudentResultDownloadClickListener {

    private RecyclerView recyclerView;
    private Dao<StudentResultDownloadTable, Long> studentResultDao;
    private DatabaseHelper databaseHelper;
    private List<StudentResultDownloadTable> studentResultList;
    private Toolbar toolbar;
    private LinearLayout emptyState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_result, container, false);


        emptyState = view.findViewById(R.id.student_rs_empty_state);
        databaseHelper = new DatabaseHelper(getContext());
        try {
            studentResultDao = DaoManager.createDao(databaseHelper.getConnectionSource(),
                    StudentResultDownloadTable.class);
            studentResultList = studentResultDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        recyclerView = view.findViewById(R.id.student_result_recyclerview);

        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Results");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        if (!studentResultList.isEmpty()) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);
            StudentResultDownloadAdapter adapter = new StudentResultDownloadAdapter(getContext(),
                    studentResultList, this);
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        }

        return view;
    }


    @Override
    public void onStudentResultDownloadClick(int position) {
    }
}
