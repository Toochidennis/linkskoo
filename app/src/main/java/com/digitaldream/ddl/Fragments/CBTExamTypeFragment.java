package com.digitaldream.ddl.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.digitaldream.ddl.Activities.StaffUtils;
import com.digitaldream.ddl.Adapters.CBTExamTypeAdapter;
import com.digitaldream.ddl.DatabaseHelper;
import com.digitaldream.ddl.Models.Exam;
import com.digitaldream.ddl.Models.ExamType;
import com.digitaldream.ddl.R;
import com.digitaldream.ddl.Utils.CustomDialog;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;


public class CBTExamTypeFragment extends Fragment implements CBTExamTypeAdapter.OnExamClickListener {

    private Toolbar mToolbar;
    private ActionBar mActionBar;
    private RecyclerView mRecyclerView;
    private CBTExamTypeAdapter mAdapter;
    private DatabaseHelper mDatabaseHelper;
    private List<ExamType> mExamTypeList;
    private Dao<ExamType, Long> mDao;

    public CBTExamTypeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_c_b_t_exam_type,
                container, false);

        mToolbar = view.findViewById(R.id.toolbar);
        mRecyclerView = view.findViewById(R.id.exam_recycler);

        mDatabaseHelper = new DatabaseHelper(getContext());

        try {
            mDao = DaoManager.createDao(mDatabaseHelper.getConnectionSource()
                    , ExamType.class);
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
        mActionBar.setTitle("CBT");
        setHasOptionsMenu(true);
        mToolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());

        //inflateExam();

        return view;
    }

    @Override
    public void onExamClick(int position) {

        if (!mExamTypeList.isEmpty()) {

            ExamType examType = mExamTypeList.get(position);
            SharedPreferences sharedPreferences =
                    Objects.requireNonNull(getContext()).getSharedPreferences("exam",
                            Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("examTypeId", examType.getExamTypeId());
            editor.putString("examName", examType.getExamName());
            editor.apply();

            startActivity(new Intent(getContext(), StaffUtils.class).putExtra("from", "cbt_exam_name"));
        } else {
            Toast.makeText(getContext(), "Something went wrong!",
                    Toast.LENGTH_SHORT).show();
        }

    }


    private class ExamOptions extends AsyncTask<String, Void, String> {
        HttpURLConnection urlConnection = null;
        BufferedReader returnedLogin = null;

        @Override
        protected String doInBackground(String... sStrings) {
            final String EXAM_BASE_URL =
                    "http://www.cbtportal.linkskool.com/api/get_course.php?json";
            String jsonString = null;
            Uri login = Uri.parse(EXAM_BASE_URL).buildUpon().build();
            URL sendLogin;
            try {
                sendLogin = new URL(login.toString());
                urlConnection = (HttpURLConnection) sendLogin.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setUseCaches(false);
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder stringBuilder = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                returnedLogin = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = returnedLogin.readLine()) != null) {
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
                if (returnedLogin != null) {
                    try {
                        returnedLogin.close();
                    } catch (IOException sE) {
                        sE.printStackTrace();
                    }
                }
            }
            return jsonString;
        }

        @Override
        protected void onPostExecute(String result) {

            if (getActivity() != null) {
                if (result != null) {
                    Log.i("results", result);
                    try {
                        JSONArray examArray = new JSONArray(result);
                        for (int i = 0; i < examArray.length(); i++) {
                            JSONObject examObject = examArray.getJSONObject(i);
                            checkExam(examObject);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    new ExamOptions().execute();
                }
            } else {
                Toast.makeText(getContext(), "Something went wrong!",
                        Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (getActivity() != null) {
                startActivity(new Intent(getContext(), StaffUtils.class).putExtra("from", "cbt_exam_name"));
            } else {
                Toast.makeText(getContext(), "Something went wrong!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void checkExam(JSONObject sJSONObject) {

        try {
            String title = sJSONObject.getString("t");
            String link = sJSONObject.getString("p");
            String status = sJSONObject.getString("s");
            String category = sJSONObject.getString("t");
            String id = sJSONObject.getString("i");
            QueryBuilder<ExamType, Long> queryBuilder = mDao.queryBuilder();
            queryBuilder.where().eq("examName", title);
            mExamTypeList = queryBuilder.query();

            if (mExamTypeList.isEmpty()) {
                ExamType examType = new ExamType();
                examType.setExamName(title);
                examType.setStatus(status);
                examType.setExamTypeId(Integer.parseInt(id));
                examType.setCategory(category);
                examType.setExamLogo(link);
                mDao.create(examType);
                // inflateExam();
            }
        } catch (SQLException | JSONException sE) {
            sE.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // mExamTypeList.clear();
        if (getActivity() != null) {
            try {
                mExamTypeList = mDao.queryForAll();

                if (!mExamTypeList.isEmpty()) {
                    mAdapter = new CBTExamTypeAdapter(getContext(),
                            mExamTypeList, this);
                    GridLayoutManager manager =
                            new GridLayoutManager(getContext(), 2);
                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setLayoutManager(manager);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                } else {
                    new ExamOptions().execute();
                }
            } catch (SQLException sE) {
                sE.printStackTrace();
            }

        } else {
            Toast.makeText(getContext(), "Something went wrong!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}



    /*public void inflateExam() {
        try {
            mExamTypeList = mDao.queryForAll();
            if (!mExamTypeList.isEmpty()) {
                mAdapter = new CBTExamTypeAdapter(getContext(), mExamTypeList);
                mGridView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

                mGridView.setOnItemClickListener((sAdapterView, sView, sI, sL) -> {
                    ExamType examType = (ExamType) mAdapter.getItem(sI);

                    SharedPreferences sharedPreferences =
                            Objects.requireNonNull(getContext()).getSharedPreferences("exam",
                                    Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("examTypeId", examType.getExamTypeId());
                    editor.putString("examName", examType.getExamName());
                    editor.apply();

                    startActivity(new Intent(getContext(), StaffUtils.class).putExtra("from", "cbt_exam_name"));

                });
            } else {
                new ExamOptions().execute();
            }
        } catch (SQLException sE) {
            sE.printStackTrace();
        }
    }*/