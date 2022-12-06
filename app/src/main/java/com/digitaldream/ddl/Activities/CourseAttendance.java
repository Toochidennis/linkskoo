package com.digitaldream.ddl.Activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.ddl.Adapters.CourseAttendanceAdapter;
import com.digitaldream.ddl.DatabaseHelper;
import com.digitaldream.ddl.Fragments.DateDialogFragment;
import com.digitaldream.ddl.Models.CourseTable;
import com.digitaldream.ddl.Models.GeneralSettingModel;
import com.digitaldream.ddl.Models.StudentTable;
import com.digitaldream.ddl.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class CourseAttendance extends AppCompatActivity implements CourseAttendanceAdapter.OnCourseClickListener, DatePickerDialog.OnDateSetListener {

    private CourseAttendanceAdapter mAttendanceAdapter;
    private Dao<CourseTable, Long> mCourseDao;
    private List<CourseTable> mCourseList;
    private List<StudentTable> mStudentTableList;
    private List<StudentTable> mStudentTable;
    private Dao<GeneralSettingModel, Long> mSettingModelDao;
    private List<GeneralSettingModel> mSettingModelList;
    DatabaseHelper mDatabaseHelper;

    private String mStudentLevelId;
    private String mStudentClassId;
    private String mCourseId;
    private String from;
    private String db, term, year;

    private RelativeLayout mLayout;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mAttendanceBtn, mTakeAttendance, mFilterAttendance,
            mAttendanceBtnEmpty, mTakeAttendanceEmpty, mFilterAttendanceEmpty;
    private Animation mFabOpen, mFabClose, mRotateForward, mRotateBackward;

    private boolean isOpen = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_course);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Course Attendance");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);

        TextView name = findViewById(R.id.course_name);
        mLayout = findViewById(R.id.empty_state);


        Intent intent = getIntent();
        mStudentLevelId = intent.getStringExtra("levelId");
        mStudentClassId = intent.getStringExtra("classId");
        mCourseId = intent.getStringExtra("courseId");
        from = intent.getStringExtra("from");


        mDatabaseHelper = new DatabaseHelper(this);
        try {
            mCourseDao =
                    DaoManager.createDao(mDatabaseHelper.getConnectionSource(),
                            CourseTable.class);
            mCourseList = mCourseDao.queryBuilder().where().eq("courseId",
                    mCourseId).query();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db", "");
        term = sharedPreferences.getString("term", "");
        year = sharedPreferences.getString("school_year", "");

        name.setText(mCourseList.get(0).getCourseName());

        mStudentTableList = new ArrayList<>();
        mStudentTable = new ArrayList<>();

        mRecyclerView = findViewById(R.id.course_details_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mAttendanceAdapter = new CourseAttendanceAdapter(this,
                mStudentTableList, this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAttendanceAdapter);

        fabButtonAction();
        getPreviousAttendance();

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mStudentTableList.clear();
        getAttendance(year, mStudentClassId, mCourseId, term, db);
    }

    public void fabButtonAction() {

        //Un empty state
        mAttendanceBtn = findViewById(R.id.attendance);
        mTakeAttendance = findViewById(R.id.take_attendance);
        mFilterAttendance = findViewById(R.id.filter_attendance);

        mFabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        mFabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        mRotateForward = AnimationUtils.loadAnimation(this,
                R.anim.rotate_forward);
        mRotateBackward = AnimationUtils.loadAnimation(this,
                R.anim.rotate_backward);


        mAttendanceBtn.setOnClickListener(v -> onFabAnimation(mAttendanceBtn,
                mTakeAttendance, mFilterAttendance));

        mTakeAttendance.setOnClickListener(v -> {
            Intent newIntent = new Intent(CourseAttendance.this,
                    ClassAttendance.class);
            newIntent.putExtra("levelId", mStudentLevelId);
            newIntent.putExtra("classId", mStudentClassId);
            newIntent.putExtra("courseId", mCourseId);

            if (!mStudentTable.isEmpty()) {
                newIntent.putExtra("responseId",
                        mStudentTable.get(0).getStudentId());
            }

            newIntent.putExtra("from", "course");
            startActivity(newIntent);
        });

        mFilterAttendance.setOnClickListener(v -> {

            FragmentActivity activity = this;
            FragmentManager manager = activity.getSupportFragmentManager();
            DateDialogFragment dialogFragment = new DateDialogFragment();
            dialogFragment.show(manager, "date picker");


        });


        //empty state
        mAttendanceBtnEmpty = findViewById(R.id.attendance_empty);
        mTakeAttendanceEmpty = findViewById(R.id.take_attendance_empty);
        mFilterAttendanceEmpty = findViewById(R.id.filter_attendance_empty);


        mAttendanceBtnEmpty.setOnClickListener(v -> onFabAnimation(mAttendanceBtnEmpty,
                mTakeAttendanceEmpty, mFilterAttendanceEmpty));

        mTakeAttendanceEmpty.setOnClickListener(v -> {
            Intent newIntent = new Intent(CourseAttendance.this,
                    ClassAttendance.class);
            newIntent.putExtra("levelId", mStudentLevelId);
            newIntent.putExtra("classId", mStudentClassId);
            newIntent.putExtra("courseId", mCourseId);
            newIntent.putExtra("from", "course");
            startActivity(newIntent);
        });


    }

    public void onFabAnimation(FloatingActionButton sAttend,
                               FloatingActionButton sTake,
                               FloatingActionButton sFilter) {
        if (isOpen) {
            sAttend.startAnimation(mRotateBackward);
            sTake.startAnimation(mFabClose);
            sFilter.startAnimation(mFabClose);
            sTake.setClickable(false);
            sFilter.setClickable(false);
            isOpen = false;
        } else {
            sAttend.startAnimation(mRotateForward);
            sTake.startAnimation(mFabOpen);
            sFilter.startAnimation(mFabOpen);
            sTake.setClickable(true);
            sFilter.setClickable(true);

            sTake.setVisibility(View.VISIBLE);
            sFilter.setVisibility(View.VISIBLE);

            isOpen = true;
        }
    }


    public void getAttendance(String sYear, String sClassId, String sCourseId,
                              String sTerm, String sDb) {

        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCancelable(false);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        String url = Login.urlBase + "/getAttendanceList.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, response -> {
            Log.i("response", response);
            dialog1.dismiss();
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String date = jsonObject.getString("date");
                    String count = jsonObject.getString("count");

                    String dateConverted = dateConverter(date);
                    Log.i("date", dateConverted);

                    StudentTable studentTable = new StudentTable();
                    studentTable.setDate(dateConverted);
                    studentTable.setStudentCount(count);
                    studentTable.setCourseCount(date);
                    mStudentTableList.add(studentTable);

                }
                mAttendanceAdapter.notifyDataSetChanged();

                if (!mStudentTableList.isEmpty()) {
                    mLayout.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);

                } else {
                    mLayout.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);

                }

            } catch (JSONException | ParseException sE) {
                sE.printStackTrace();
            }

        }, error -> {
            dialog1.dismiss();
            Toast.makeText(this, "Something went wrong!",
                    Toast.LENGTH_SHORT).show();
            mLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            ImageView imageView = findViewById(R.id.image);
            TextView textView = findViewById(R.id.error_message);
            imageView.setImageResource(R.drawable.no_internet);
            textView.setText("Seems like you're not connected to the internet!");
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("year", sYear);
                stringMap.put("class", sClassId);
                stringMap.put("course", sCourseId);
                stringMap.put("term", sTerm);
                stringMap.put("_db", sDb);
                return stringMap;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public void getPreviousAttendance() {
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCancelable(false);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();

        String url = Login.urlBase + "/getAttendance.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.i("Response", response);
                    dialog1.dismiss();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String responseId = jsonObject.getString("id");

                            StudentTable studentTable = new StudentTable();
                            studentTable.setStudentId(responseId);
                            mStudentTable.add(studentTable);
                        }

                    } catch (JSONException sE) {
                        sE.printStackTrace();
                    }

                }, error -> {
            error.printStackTrace();
            dialog1.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("class", mStudentClassId);
                stringMap.put("date", getDate());
                stringMap.put("course", mCourseId);
                stringMap.put("_db", db);
                return stringMap;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    public String getDate() {
        String date;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        date = year + "-" + month + "-" + dayOfMonth;

        return date.concat(" 00:00:00");
    }


    public String dateConverter(String date) throws ParseException {

        String format = date.replace(" ", "T").concat(".000Z");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd" +
                "'T'HH" + ":mm:ss.SSS'Z'", Locale.US);

        Date oldDate = simpleDateFormat.parse(format);

        assert oldDate != null;
        return DateFormat.getDateInstance(DateFormat.FULL).format(oldDate);

    }

    @Override
    public void onDayClick(int position) {

        Intent newIntent = new Intent(CourseAttendance.this,
                AttendanceDetails.class);
        newIntent.putExtra("from", "course");
        newIntent.putExtra("courseId", mCourseId);
        newIntent.putExtra("classId", mStudentClassId);
        newIntent.putExtra("date", mStudentTableList.get(position).getCourseCount());
        newIntent.putExtra("course_name", mCourseList.get(0).getCourseName());
        newIntent.putExtra("db", db);
        startActivity(newIntent);

    }

    @Override
    public void onDateSet(DatePicker sDatePicker, int sYear, int sMonth,
                          int sDayOfMonth) {
        int month = sMonth + 1;

        String currentDate = sYear + "-" + month + "-" + sDayOfMonth;
        currentDate = currentDate.concat(" 00:00:00");

        Log.i("date", currentDate);
        Intent newIntent = new Intent(CourseAttendance.this,
                AttendanceDetails.class);
        newIntent.putExtra("from", "course");
        newIntent.putExtra("courseId", mCourseId);
        newIntent.putExtra("classId", mStudentClassId);
        newIntent.putExtra("date", currentDate);
        newIntent.putExtra("course_name", mCourseList.get(0).getCourseName());
        newIntent.putExtra("db", db);
        startActivity(newIntent);


    }


}
