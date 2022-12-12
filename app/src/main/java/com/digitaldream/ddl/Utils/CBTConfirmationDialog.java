package com.digitaldream.ddl.Utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.digitaldream.ddl.DatabaseHelper;
import com.digitaldream.ddl.Models.Exam;
import com.digitaldream.ddl.Models.ExamQuestions;
import com.digitaldream.ddl.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class CBTConfirmationDialog extends Dialog {

    private List<Exam> mExamList;
    private List<ExamQuestions> mExamQuestionsList;
    private Dao<Exam, Long> mExamDao;
    private Dao<ExamQuestions, Long> mExamQuestions;
    private String mJson;
    private String mCourseName;
    private String mYear;
    private TextView mSubject, mExamYear, mCancelText, mContinueText;
    private DatabaseHelper mDatabaseHelper;
    ProgressBar progressBar;
    CardView mCancelBtn, mContinueBtn;
    ConstraintLayout mCancelLayout, mContinueLayout;


    public CBTConfirmationDialog(@NonNull Context context, String sCourseName
            , String sYear) {
        super(context);
        mCourseName = sCourseName;
        mYear = sYear;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_cbt_confirmation);

        mSubject = findViewById(R.id.subject);
        mExamYear = findViewById(R.id.year);
        mCancelText = findViewById(R.id.cancel_btn_text);
        mContinueText = findViewById(R.id.comment_text);
        mCancelBtn = findViewById(R.id.cancel_btn);
        mContinueBtn = findViewById(R.id.continue_btn);
        progressBar = findViewById(R.id.progressBar);
        mCancelLayout = findViewById(R.id.cancel_layout);
        mContinueLayout = findViewById(R.id.continue_layout);

        mDatabaseHelper = new DatabaseHelper(getContext());


        try {
            mExamDao =
                    DaoManager.createDao(mDatabaseHelper.getConnectionSource(), Exam.class);
            mExamQuestions =
                    DaoManager.createDao(mDatabaseHelper.getConnectionSource(), ExamQuestions.class);
        } catch (SQLException sE) {
            sE.printStackTrace();
        }

        mSubject.setText("Subject: " + mCourseName);
        mExamYear.setText("Year: " + mYear);

        /* mStart.setOnClickListener(sView -> {




         *//*if (!mJson.isEmpty()) {
                Log.i("jsonResponse", mJson);
                getContext().startActivity(new Intent(getContext(),
                        ExamActivity.class)
                        .putExtra("Json", mJson)
                        .putExtra("course", mCourseName)
                        .putExtra("year", mYear)
                        .putExtra("from", "cbt"));
                dismiss();
            } else {
                Toast.makeText(getContext(), "Something went wrong!",
                        Toast.LENGTH_SHORT).show();
            }
*//*
        });*/

/*
        mCardView.setOnClickListener(sView -> {
            activated();
            new Handler().postDelayed(this::stopped, 3000);
        });
*/

        loadQuestions();

    }


    void activated() {
        Animation animation = AnimationUtils.loadAnimation(getContext(),
                R.anim.fade_in);
        progressBar.setAnimation(animation);
        progressBar.setVisibility(View.VISIBLE);
        mSubject.setAnimation(animation);
        mSubject.setText("Please wait...");

    }

    void stopped() {
        //mLayout.setBackgroundColor(mCardView.getResources().getColor(R
        // .color.green_cyan));
        progressBar.setVisibility(View.GONE);
        mSubject.setText("Done");

    }

    public void loadQuestions() {
        try {
            mExamList = mExamDao.queryBuilder().where().eq("course",
                    mCourseName).and().eq("year", mYear).query();

            if (!mExamList.isEmpty()) {
                Exam exam = mExamList.get(0);

                mExamQuestionsList =
                        mExamQuestions.queryBuilder().where().eq("examId",
                                exam.getExamId()).query();
                if (!mExamQuestionsList.isEmpty()) {
                    ExamQuestions examQuestions = mExamQuestionsList.get(0);
                    mJson = examQuestions.getJson();
                } else {
                    new getCourse().execute(Integer.toString(exam.getYearId()));
                }
            }
        } catch (SQLException sE) {
            sE.printStackTrace();
        }
    }

    private class getCourse extends AsyncTask<String, Void, String> {
        HttpURLConnection urlConnection = null;
        BufferedReader returnedLogin = null;
        URL receiveCourse = null;

        @Override
        protected String doInBackground(String... params) {
            final String LOGIN_BASE_URL =
                    "http://www.cbtportal.linkskool.com/api";
            final String JSON = "json";
            final String EXAM = "exam";
            final String CODE = "appCode";
            final String PATH = "exam_json.php";
            String jsonString = null;

            Uri login = Uri.parse(LOGIN_BASE_URL).buildUpon()
                    .appendPath(PATH)
                    .appendQueryParameter(JSON, "1")
                    .appendQueryParameter(CODE, "VDOK-124-CAUCHY")
                    .appendQueryParameter(EXAM, params[0])
                    .build();
            try {
                receiveCourse = new URL(login.toString());
                urlConnection = (HttpURLConnection) receiveCourse.openConnection();
                urlConnection.setRequestMethod("GET");
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
            try {
                if (result != null) {
                    mJson = result;
                    JSONObject obj = new JSONObject(result);
                    JSONObject object = obj.getJSONObject("e");
                    String id = object.getString("id");
                    ExamQuestions examQuestions = new ExamQuestions();
                    examQuestions.setExamId(Integer.parseInt(id));
                    examQuestions.setJson(result);
                    mExamQuestions.create(examQuestions);
                } else {
                    loadQuestions();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Something went wrong!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
