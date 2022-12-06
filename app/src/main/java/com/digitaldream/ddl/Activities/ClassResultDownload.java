package com.digitaldream.ddl.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.ddl.DatabaseHelper;
import com.digitaldream.ddl.Models.ClassNameTable;
import com.digitaldream.ddl.R;
import com.digitaldream.ddl.Adapters.SubjectDownloadAdapter;
import com.digitaldream.ddl.Models.SubjectResultModel;
import com.digitaldream.ddl.Models.TeachersTable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class ClassResultDownload extends AppCompatActivity implements SubjectDownloadAdapter.OnSubjectDownloadClickListener {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<SubjectResultModel> subjectDownloadList;
    SubjectDownloadAdapter subjectDownloadAdapter;
    private TextView session,className;
    private LinearLayout emptyLayout;
    private RelativeLayout editClassBtn;
    private String classID;
    private Dao<ClassNameTable,Long> classDao;
    private Dao<TeachersTable,Long> teacherDao;
    private DatabaseHelper databaseHelper;
    public static String schoolYear;
    public static String term;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_result_download);

        databaseHelper = new DatabaseHelper(this);
        try {
            classDao = DaoManager.createDao(databaseHelper.getConnectionSource(),ClassNameTable.class);
            teacherDao = DaoManager.createDao(databaseHelper.getConnectionSource(),TeachersTable.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Subject Results");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);

        Intent i = getIntent();
        classID =i.getStringExtra("class_id");
        schoolYear = i.getStringExtra("session");
        term = i.getStringExtra("term");
        String class_name = i.getStringExtra("class_name");

        recyclerView = findViewById(R.id.subject_download_recycler);
        emptyLayout = findViewById(R.id.class_res_dwnd_empty_state);
        editClassBtn = findViewById(R.id.class_edit);
        editClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ClassResultDownload.this, AddClass.class);
                i.putExtra("class_id",classID);
                i.putExtra("from","classEdit");
                startActivity(i);
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        String db = sharedPreferences.getString("db","");
        subjectDownloadList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        getClassCoursesApiCall(classID.trim(),db.trim());



        //subjectDownloadAdapter = new SubjectDownloadAdapter(this, subjectDownloadList, this);
            //recyclerView.setAdapter(subjectDownloadAdapter);


        session = findViewById(R.id.session_class);
        int previousYear = Integer.parseInt(schoolYear)-1;
        session.setText(String.valueOf(previousYear)+" / "+schoolYear);

        className = findViewById(R.id.class_name_class);
        className.setText(ClassResultDetails.class_name.toUpperCase());

    }

    @Override
    public void onSubjectDownloadClick(int position) {
        Intent intent = new Intent(ClassResultDownload.this, SubjectResultUtil.class);
        intent.putExtra("courseId",subjectDownloadList.get(position).getCourseId());
        intent.putExtra("class_id",subjectDownloadList.get(position).getClassId());
        intent.putExtra("term",term);
        intent.putExtra("year",schoolYear);
        startActivity(intent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getClassCoursesApiCall(String classID,String db){
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(ClassResultDownload.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        Log.i("response",classID+" "+db);

        String url = Login.urlBase+"/classCourse.php?class="+classID+"&year="+schoolYear+"&term="+term+"&_db="+db;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog1.dismiss();
                Log.i("response", response);
                if (!response.equals("null")) {
                    try {
                        JSONObject object = new JSONObject(response);
                        Iterator<String> keys = object.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            if (object.get(key) instanceof JSONObject) {
                                // do something with jsonObject here
                                JSONObject object1 = object.getJSONObject(key);
                                String courseId = object1.getString("id");
                                String courseName = object1.getString("course_name");
                                String classID = object1.getString("class");
                                subjectDownloadList.add(new SubjectResultModel(courseName, courseId, classID));

                            }
                        }
                        if (!subjectDownloadList.isEmpty()) {
                            emptyLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            subjectDownloadAdapter = new SubjectDownloadAdapter(ClassResultDownload.this, subjectDownloadList, ClassResultDownload.this);
                            recyclerView.setAdapter(subjectDownloadAdapter);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            emptyLayout.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    recyclerView.setVisibility(View.GONE);
                    emptyLayout.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        QueryBuilder<ClassNameTable,Long> queryBuilder=classDao.queryBuilder();
        try {
            queryBuilder.where().eq("classId",classID);
            List<ClassNameTable> classList = queryBuilder.query();
            className.setText(classList.get(0).getClassName().toUpperCase());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
