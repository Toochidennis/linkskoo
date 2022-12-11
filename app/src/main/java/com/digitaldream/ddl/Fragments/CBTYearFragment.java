package com.digitaldream.ddl.Fragments;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digitaldream.ddl.Adapters.CBTYearAdapter;
import com.digitaldream.ddl.DatabaseHelper;
import com.digitaldream.ddl.Models.Exam;
import com.digitaldream.ddl.Models.ExamQuestions;
import com.digitaldream.ddl.R;
import com.digitaldream.ddl.Utils.CustomCBTDialog;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class CBTYearFragment extends Fragment implements CBTYearAdapter.OnYearClickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mCourseName;
    private String mExamTypeId;
    private RecyclerView mRecyclerView;
    private CBTYearAdapter mAdapter;
    private DatabaseHelper mDatabaseHelper;
    private List<Exam> mExamList;
    private Dao<Exam, Long> mDao;
    private Toolbar mToolbar;
    private ActionBar mActionBar;

    public CBTYearFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CBTYearFragment newInstance(String param1, String param2) {
        CBTYearFragment fragment = new CBTYearFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCourseName = getArguments().getString(ARG_PARAM1);
            mExamTypeId = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_c_b_t_year, container,
                false);

        mToolbar = view.findViewById(R.id.toolbar);
        mRecyclerView = view.findViewById(R.id.cbt_year_recycler);


        mDatabaseHelper = new DatabaseHelper(getContext());

        try {
            mDao = DaoManager.createDao(mDatabaseHelper.getConnectionSource()
                    , Exam.class);
        } catch (SQLException sE) {
            sE.printStackTrace();
        }

        ((AppCompatActivity) (Objects.requireNonNull(getActivity()))).setSupportActionBar(mToolbar);
        mActionBar =
                ((AppCompatActivity) (getActivity())).getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_left_black);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("Select year");
        setHasOptionsMenu(true);
        mToolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());

        return view;
    }

    @Override
    public void onYearClick(int position) {

    }


    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            try {
                mExamList = mDao.queryBuilder().where().eq("course",
                        mCourseName).query();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Collections.sort(mExamList,
                            Collections.reverseOrder(Comparator.comparing(Exam::getYear)));
                }

                if (!mExamList.isEmpty()) {
                    mAdapter = new CBTYearAdapter(getContext(), mExamList,
                            this);
                    GridLayoutManager manager =
                            new GridLayoutManager(getContext(), 2);
                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setLayoutManager(manager);
                    mRecyclerView.setAdapter(mAdapter);
                }
            } catch (SQLException sE) {
                sE.printStackTrace();
            }

        }
    }

}