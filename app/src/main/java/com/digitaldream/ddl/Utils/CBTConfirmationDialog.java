package com.digitaldream.ddl.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
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
import com.digitaldream.ddl.ExamActivity;
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
import java.util.Objects;

public class CBTConfirmationDialog extends Dialog {

    private List<Exam> mExamList;
    private List<ExamQuestions> mExamQuestionsList;
    private Dao<Exam, Long> mExamDao;
    private Dao<ExamQuestions, Long> mExamQuestions;
    private String mJson;
    private String mCourseName;
    private String mYear, mExamTypeName;
    private TextView mSubject, mExamYear, mExamName;
    private DatabaseHelper mDatabaseHelper;
    ProgressBar progressBar;
    CardView mCancelBtn, mContinueBtn;


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
        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(R.layout.dialog_cbt_confirmation);

        mSubject = findViewById(R.id.subject);
        mExamYear = findViewById(R.id.year);
        mExamName = findViewById(R.id.exam_type);
        mCancelBtn = findViewById(R.id.cancel_btn);
        mContinueBtn = findViewById(R.id.continue_btn);
        progressBar = findViewById(R.id.progressBar);

        mDatabaseHelper = new DatabaseHelper(getContext());

        SharedPreferences sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences("exam", Context.MODE_PRIVATE);
        mExamTypeName = sharedPreferences.getString("examName", "");


        try {
            mExamDao =
                    DaoManager.createDao(mDatabaseHelper.getConnectionSource(), Exam.class);
            mExamQuestions =
                    DaoManager.createDao(mDatabaseHelper.getConnectionSource(), ExamQuestions.class);
        } catch (SQLException sE) {
            sE.printStackTrace();
        }

        String[] courseString = mCourseName.toLowerCase().split(" ");
        StringBuilder courseBuilder = new StringBuilder();
        for (String letter : courseString) {
            try {
                String words =
                        letter.substring(0, 1).toUpperCase() + letter.substring(1).toLowerCase();
                courseBuilder.append(words).append(" ");
            } catch (Exception sE) {
                sE.printStackTrace();
            }

        }
        String[] examStrings = mExamTypeName.toLowerCase().split(" ");
        StringBuilder examBuilder = new StringBuilder();
        for (String letter : examStrings) {
            try {
                String words =
                        letter.substring(0, 1).toUpperCase() + letter.substring(1).toLowerCase();
                examBuilder.append(words).append(" ");
            } catch (Exception sE) {
                sE.printStackTrace();
            }

        }

        mExamName.setText(examBuilder.toString());
        mSubject.setText(courseBuilder.toString());
        mExamYear.setText(mYear);

        loadQuestions();

        mCancelBtn.setOnClickListener(sView -> {
            dismiss();
        });

        mContinueBtn.setOnClickListener(sView -> {

            if (!mExamQuestionsList.isEmpty()) {
                Log.i("jsonResponse", mJson);
                getContext().startActivity(new Intent(getContext(),
                        ExamActivity.class)
                        .putExtra("Json", mJson)
                        .putExtra("course", mCourseName)
                        .putExtra("year", mYear)
                        .putExtra("from", "cbt"));
            } else {
                Toast.makeText(getContext(), "Something went wrong",
                        Toast.LENGTH_SHORT).show();
            }

            dismiss();

        });


    }


/*    void activateAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getContext(),
                R.anim.fade_in);
        progressBar.setAnimation(animation);
        progressBar.setVisibility(View.VISIBLE);
        mContinueText.setAnimation(animation);
        mContinueText.setText("Please wait...");

    }

    void cancelDialog() {
        mCancelLayout.setBackgroundColor(mCancelBtn.getResources().getColor(R
                .color.color_4));
        mContinueLayout.setBackgroundColor(mContinueBtn.getResources().getColor(R.color.romance));
        progressBar.setVisibility(View.GONE);
        mCancelText.setTextColor(mCancelBtn.getResources().getColor(R.color.white));
        mContinueText.setTextColor(mContinueBtn.getResources().getColor(R.color.text_bg_color));
    }*/

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
                    Log.i("Status", "ran " + mJson + " execution");
                }
            }
        } catch (SQLException sE) {
            sE.printStackTrace();
        }
        Log.i("Status", "ran" + mJson);
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
