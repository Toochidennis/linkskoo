package com.digitaldream.ddl.Fragments;


import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digitaldream.ddl.Activities.StudentElearningCourses;
import com.digitaldream.ddl.Adapters.StudentLevelAdapter;
import com.digitaldream.ddl.DatabaseHelper;
import com.digitaldream.ddl.Models.CourseOutlineTable;
import com.digitaldream.ddl.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentElearning extends Fragment implements StudentLevelAdapter.OnLevelClickListener {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private Dao<CourseOutlineTable,Long> courseOutlineDao;
    private List<CourseOutlineTable> list;


    public StudentElearning() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_elearning, container, false);
        setHasOptionsMenu(true);

        databaseHelper = new DatabaseHelper(getContext());
        try {
            courseOutlineDao = DaoManager.createDao(databaseHelper.getConnectionSource(),CourseOutlineTable.class);
            list = courseOutlineDao.queryBuilder().groupBy("levelId").query();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        recyclerView = view.findViewById(R.id.student_elearning_recyclerview);

        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("E-Learning");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        StudentLevelAdapter adapter = new StudentLevelAdapter(getContext(),list,this);
        recyclerView.setAdapter(adapter);
        //recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));

        return view;
    }

    @Override
    public void onLevelClick(int position) {
        Intent intent = new Intent(getContext(), StudentElearningCourses.class);
        intent.putExtra("levelName",list.get(position).getLevelName());
        intent.putExtra("levelId",list.get(position).getLevelId());
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
}
