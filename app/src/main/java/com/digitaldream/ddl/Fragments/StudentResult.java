package com.digitaldream.ddl.Fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.digitaldream.ddl.DatabaseHelper;
import com.digitaldream.ddl.R;
import com.digitaldream.ddl.Adapters.StudentResultDownloadAdapter;
import com.digitaldream.ddl.Models.StudentResultDownloadTable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class StudentResult extends Fragment implements StudentResultDownloadAdapter.OnStudentResultDownloadClickListener {

    private RecyclerView recyclerView;
    private Dao<StudentResultDownloadTable,Long> studentResultDao;
    private DatabaseHelper databaseHelper;
    private List<StudentResultDownloadTable> studentResultList;
    private Toolbar toolbar;
    private LinearLayout emptyState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_student_result, container, false);
        setHasOptionsMenu(true);


        emptyState = view.findViewById(R.id.student_rs_empty_state);
        databaseHelper = new DatabaseHelper(getContext());
        try {
            studentResultDao = DaoManager.createDao(databaseHelper.getConnectionSource(),StudentResultDownloadTable.class);
            studentResultList=studentResultDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        recyclerView = view.findViewById(R.id.student_result_recyclerview);

        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Results");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        if(!studentResultList.isEmpty()) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);
            StudentResultDownloadAdapter adapter = new StudentResultDownloadAdapter(getContext(), studentResultList, this);
            recyclerView.setAdapter(adapter);
        }else{
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.staff_logout_menu,menu);
        menu.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStudentResultDownloadClick(int position) {
    }
}
