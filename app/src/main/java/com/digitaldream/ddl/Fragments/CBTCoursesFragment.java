package com.digitaldream.ddl.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digitaldream.ddl.Adapters.CBTCoursesAdapter;
import com.digitaldream.ddl.DatabaseHelper;
import com.digitaldream.ddl.Models.Exam;
import com.digitaldream.ddl.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class CBTCoursesFragment extends Fragment implements CBTCoursesAdapter.OnCourseClickListener {

    private Toolbar mToolbar;
    private ActionBar mActionBar;
    private RecyclerView mRecyclerView;
    private CBTCoursesAdapter mAdapter;
    private DatabaseHelper mDatabaseHelper;
    private String mExamTypeId;
    private List<Exam> mExamList;
    private Dao<Exam, Long> mDao;

    public CBTCoursesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_c_b_t_courses, container,
                false);

        mToolbar = view.findViewById(R.id.toolbar);
        mRecyclerView = view.findViewById(R.id.cbt_courses_recycler);

        mDatabaseHelper = new DatabaseHelper(getContext());

        try {
            SharedPreferences sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences("exam", Context.MODE_PRIVATE);
            mExamTypeId = Integer.toString(sharedPreferences.getInt("examTypeId", 1));

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
        mActionBar.setTitle("Select a course");
        setHasOptionsMenu(true);
        mToolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public void onCourseClick(int position) {

    }


    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() != null) {
            loadCourses();
        }

    }

    public void loadCourses() {
        try {
            QueryBuilder<Exam, Long> queryBuilder =
                    mDao.queryBuilder().distinct().selectColumns("course");
            queryBuilder.where().eq("examTypeId", mExamTypeId);

            mExamList = queryBuilder.query();
            mAdapter = new CBTCoursesAdapter(getContext(), mExamList, this);
            LinearLayoutManager manager =
                    new LinearLayoutManager(getContext());
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

        } catch (SQLException sE) {
            sE.printStackTrace();
        }
    }

    public void insertExam(String subject, String subjectId, String year, String yearId) {
        try {
            Exam exam = new Exam();
            exam.setCourse(subject);
            exam.setExamId(Integer.parseInt(subjectId));
            exam.setYear(year);
            exam.setYearId(Integer.parseInt(yearId));
            exam.setExamTypeId(Integer.parseInt(mExamTypeId));
            mDao.create(exam);
            loadCourses();
        } catch (Exception ignored) {
        }
    }


    private class fetchExamTask extends AsyncTask<String, Void, String> {
        HttpURLConnection urlConnection = null;
        BufferedReader returnedRegister = null;

        @Override
        protected String doInBackground(String... params) {

            final String SIGNUP_BASE_URL =
                    "http://www.cbtportal.linkskool.com/api/get_course.php?json";
            String jsonString = null;

            Uri login = Uri.parse(SIGNUP_BASE_URL).buildUpon()
                    .build();

            URL sendRegister = null;
            try {
                sendRegister = new URL(login.toString());
                urlConnection = (HttpURLConnection) sendRegister.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder stringBuilder = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                returnedRegister = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = returnedRegister.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                if (stringBuilder.length() == 0) {
                    return null;
                }
                jsonString = stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();

                if (returnedRegister != null) {
                    try {
                        returnedRegister.close();
                    } catch (final IOException e) {
                        e.printStackTrace();

                    }
                }
            }
            return jsonString;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("response", result);

            if (result != null) {
                try {
                    JSONArray examListObject = new JSONArray(result);
                    for (int i = 0; i < examListObject.length(); i++) {
                        JSONObject exam = examListObject.getJSONObject(i);
                        String subjectId = exam.getString("i");
                        String examYearArray = exam.getString("d");
                        JSONArray examYear = new JSONArray(examYearArray);
                        if (subjectId.equals(mExamTypeId)) {
                            for (int j = 0; j < examYear.length(); j++) {
                                JSONObject exam1 = examYear.getJSONObject(j);
                                String examSubject = exam1.getString("c");
                                String examId = exam1.getString("i");
                                String YearsArray = exam1.getString("y");
                                JSONArray yearArray = new JSONArray(YearsArray);
                                for (int k = 0; k < yearArray.length(); k++) {
                                    JSONObject exam2 = yearArray.getJSONObject(k);
                                    String yearId = exam2.getString("i");
                                    String year = exam2.getString("d");
                                    insertExam(examSubject, examId, year, yearId);
                                }
                            }
                        }
                    }
                } catch (JSONException sE) {
                    sE.printStackTrace();
                }
            } else {
                new fetchExamTask().execute();
            }

        }
    }
}