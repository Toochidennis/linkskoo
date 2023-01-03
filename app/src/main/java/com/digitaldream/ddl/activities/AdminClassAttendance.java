package com.digitaldream.ddl.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.ddl.adapters.AdminClassAttendanceAdapter;
import com.digitaldream.ddl.DatabaseHelper;
import com.digitaldream.ddl.models.ClassNameTable;
import com.digitaldream.ddl.models.GeneralSettingModel;
import com.digitaldream.ddl.models.StudentTable;
import com.digitaldream.ddl.models.TeachersTable;
import com.digitaldream.ddl.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class AdminClassAttendance extends AppCompatActivity implements AdminClassAttendanceAdapter.OnStudentClickListener {

    private FloatingActionButton mActionButton;
    private RecyclerView mRecyclerView;
    private AdminClassAttendanceAdapter mAdminClassAttendanceAdapter;
    private List<StudentTable> mStudentTableList;
    private Dao<StudentTable, Long> mStudentTableDao;
    private Dao<GeneralSettingModel, Long> mSettingModelDao;
    private Dao<ClassNameTable, Long> mClassNameDao;
    private List<ClassNameTable> mClassNameList;
    private Dao<TeachersTable, Long> mTeacherDao;
    private List<TeachersTable> mTeachersTableList;
    private List<GeneralSettingModel> mSettingModelList;
    DatabaseHelper mDatabaseHelper;
    private String mStudentClassId;
    private boolean mSelectAll = false;
    private Menu mMenu;
    private static String db, year, term, from, courseId, responseId, staffId;

    private ActionBar actionBar;

    TextView mName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_attendance_class);

        Intent intent = getIntent();
        String studentLevelId = intent.getStringExtra("levelId");
        mStudentClassId = intent.getStringExtra("classId");
        courseId = intent.getStringExtra("courseId");
        responseId = intent.getStringExtra("responseId");
        from = intent.getStringExtra("class_name");


        Toolbar toolbar = findViewById(R.id.course_tool_bar);
        setSupportActionBar(toolbar);
        //final ActionBar actionBar = getSupportActionBar();
        actionBar = getSupportActionBar();
        assert actionBar != null;

        actionBar.setTitle("Class Attendance");

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);

        mDatabaseHelper = new DatabaseHelper(this);

        try {
            mStudentTableDao = DaoManager.createDao(mDatabaseHelper.getConnectionSource(), StudentTable.class);
            QueryBuilder<StudentTable, Long> queryBuilder = mStudentTableDao.queryBuilder();

            queryBuilder.where().eq("studentLevel", studentLevelId).and().eq(
                    "studentClass", mStudentClassId);
            mStudentTableList = queryBuilder.query();
            Log.i("date", mStudentTableList.toString());
            Random rnd = new Random();
            for (StudentTable table : mStudentTableList) {
                int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                table.setColor(currentColor);
            }

        } catch (SQLiteException | SQLException e) {
            e.printStackTrace();
        }


        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db", "");
        term = sharedPreferences.getString("term", "");
        staffId = sharedPreferences.getString("user_id", "");
        year = sharedPreferences.getString("school_year", "");
        Log.i("term", year);
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
        TextView classTerm = findViewById(R.id.class_term);
        mName = findViewById(R.id.class_title);

        mName.setText(from);

        classTerm.setText(String.format("%d/%s %s", previousYear, year,
                termText));

        mRecyclerView = findViewById(R.id.students_recycler);
        mRecyclerView.setHasFixedSize(true);
        mAdminClassAttendanceAdapter = new AdminClassAttendanceAdapter(this, mStudentTableList,
                this);
        LinearLayoutManager manager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdminClassAttendanceAdapter);

        RelativeLayout layout = findViewById(R.id.empty_state);

        if (!mStudentTableList.isEmpty()) {
            layout.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

        mActionButton = findViewById(R.id.submit_btn);

        mActionButton.setOnClickListener(v -> {
            JSONArray jsonArray = new JSONArray();
            for (StudentTable studentTable : mStudentTableList) {
                if (studentTable.isSelected()) {
                    try {

                        JSONObject studentObject = new JSONObject();
                        studentObject.put("id", studentTable.getStudentId());
                        studentObject.put("name",
                                getStudentName(studentTable.getStudentSurname(), studentTable.getStudentMiddlename(), studentTable.getStudentFirstname()));

                        jsonArray.put(studentObject);

                        Log.i("value", jsonArray.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            takeClassAttendance(mStudentClassId, staffId, courseId,
                    jsonArray.toString(), year, term, db,
                    (String) actionBar.getTitle());

        });

        getPreviousAttendance(mStudentClassId, courseId, getDate(), db);


        //  Log.i("responseId", getResponseId());

    }


    private int getSelectedCount() {
        int a = 0;
        for (StudentTable table : mStudentTableList) {
            if (table.isSelected()) {
                a++;
            }
        }
        return a;
    }

    private void showHideFab() {
        boolean show = false;

        for (StudentTable table : mStudentTableList) {
            if (table.isSelected()) {
                show = true;
                break;
            }
        }

        if (show) {

            mActionButton.setVisibility(View.VISIBLE);
        } else {
            mActionButton.setVisibility(View.GONE);
        }
    }


    private void selection(boolean value) {
        boolean show = false;
        for (StudentTable st : mStudentTableList) {
            st.setSelected(value);
            show = st.isSelected();
        }
        mAdminClassAttendanceAdapter.notifyDataSetChanged();
        if (show) {
            mActionButton.setVisibility(View.VISIBLE);
        } else {
            mActionButton.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.check_all, menu);
        mMenu = menu;
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.check_all:
                if (mSelectAll) {
                    mMenu.getItem(0).setVisible(false);
                    selection(false);
                    mSelectAll = false;
                } else {
                    mMenu.getItem(0).setIcon(ContextCompat.getDrawable(this,
                            R.drawable.ic_check));
                    selection(true);
                    mSelectAll = true;
                }


                if (getSelectedCount() == 0) {

                    actionBar.setTitle("Class Attendance");

                } else {
                    actionBar.setTitle("" + getSelectedCount());
                }
                mAdminClassAttendanceAdapter.notifyDataSetChanged();

                return true;
            case android.R.id.home:
                onBackPressed();
                return true;

        }


        return false;
    }


    @Override
    public void onStudentClick(int position) {

        StudentTable studentTable = mStudentTableList.get(position);
        if (studentTable.isSelected()) {
            studentTable.setSelected(false);
        } else {
            mMenu.getItem(0).setVisible(false);
            studentTable.setSelected(true);
        }

        if (getSelectedCount() == 0) {

            actionBar.setTitle("Class Attendance");

        } else {
            actionBar.setTitle("" + getSelectedCount());
        }

        mAdminClassAttendanceAdapter.notifyDataSetChanged();
        invalidateOptionsMenu();
        mSelectAll = false;
        showHideFab();

    }

    @Override
    public void onStudentLongClick(int position) {

    }


    private void takeClassAttendance(String sClassId, String sStaffId,
                                     String sCourseId,
                                     String sStudents, String sYear,
                                     String sTerm, String sDb, String sCount) {
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCancelable(false);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        String url = Login.urlBase + "/setAttendance.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.i("Response", response);
                    dialog1.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (status.equals("success")) {
                            Toast.makeText(AdminClassAttendance.this, "Operation was successful",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AdminClassAttendance.this, "Something " +
                                            "went wrong!",
                                    Toast.LENGTH_SHORT).show();
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
                stringMap.put("id", Objects.requireNonNullElse(responseId, "0"));
                stringMap.put("class", sClassId);
                stringMap.put("staff", sStaffId);
                stringMap.put("year", sYear);
                stringMap.put("term", sTerm);
                stringMap.put("course", Objects.requireNonNullElse(sCourseId,
                        "0"));
                stringMap.put("register", sStudents);
                stringMap.put("count", sCount);
                stringMap.put("date", getDate());
                stringMap.put("_db", sDb);
                return stringMap;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    public void getPreviousAttendance(String sClassId, String sCourseId,
                                      String sDate
            , String sDb) {

        String url = Login.urlBase + "/getAttendance.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.i("response", response);
                    String json = "";
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            json = jsonObject.getString("register");

                        }

                        if (!json.isEmpty()) {
                            JSONArray jsonArray1 = new JSONArray(json);
                            for (int j = 0; j < jsonArray1.length(); j++) {
                                JSONObject object = jsonArray1.getJSONObject(j);
                                String studentId = object.getString("id");

                                getStudentsList(mStudentTableList,
                                        studentId);
                            }
                            mAdminClassAttendanceAdapter.notifyDataSetChanged();
                            actionBar.setTitle("" + getSelectedCount());
                            showHideFab();
                        }

                    } catch (JSONException sE) {
                        sE.printStackTrace();
                    }

                }, error -> {
            error.printStackTrace();
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("class", sClassId);
                stringMap.put("date", sDate);
                stringMap.put("course", Objects.requireNonNullElse(sCourseId,
                        "0"));
                stringMap.put("_db", sDb);
                return stringMap;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    public void getStudentsList(List<StudentTable> sStudentTableList, String sId) {
        for (StudentTable item : sStudentTableList) {
            if (sId.equals(item.getStudentId())) {
                item.setSelected(true);
            }
        }

    }


    public String getDate() {
        String date;
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String dayOfMonth = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        date = year + "-" + month + "-" + dayOfMonth;

        return date.concat(" 00:00:00");
    }

    public String getStudentName(String sSurName, String sMiddleName,
                                 String sFirstName) {

        try {
            if (sSurName != null) {
                sSurName = sSurName.substring(0, 1).toUpperCase() + "" + sSurName.substring(1).toLowerCase();
            } else {
                sSurName = "";
            }
            if (sMiddleName != null) {
                sMiddleName = sMiddleName.substring(0, 1).toUpperCase() + "" + sMiddleName.substring(1).toLowerCase();
            } else {
                sMiddleName = "";
            }
            if (sFirstName != null) {
                sFirstName = sFirstName.substring(0, 1).toUpperCase() + "" + sFirstName.substring(1).toLowerCase();
            } else {
                sFirstName = "";
            }

        } catch (StringIndexOutOfBoundsException | NullPointerException e) {
            e.printStackTrace();
        }

        return sSurName + " " + sMiddleName + " " + sFirstName;
    }
}