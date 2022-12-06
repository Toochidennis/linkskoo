package com.digitaldream.ddl.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.ddl.Adapters.ClassTermsAdapter;
import com.digitaldream.ddl.DatabaseHelper;
import com.digitaldream.ddl.Models.ClassNameTable;
import com.digitaldream.ddl.Models.ClassTermResulsModel;
import com.digitaldream.ddl.R;
import com.digitaldream.ddl.Models.TeachersTable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class ClassResultDetails extends AppCompatActivity {
    private RecyclerView classResultRecycler;
    private ClassTermsAdapter adapter;
    private List<ClassTermResulsModel> classResultList;
    public static String classId;
    private TextView className;
    private RelativeLayout rootView;
    private Toolbar toolbar;
    public static String class_name;
    private RelativeLayout classEdit;
    private String db;
    private DatabaseHelper databaseHelper;
    private Dao<ClassNameTable,Long> classDao;
    private Dao<TeachersTable,Long> teacherDao;
    private TextView formTeacher;
    private RelativeLayout emptyLayout;
    private String levelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_result_details);
        rootView = findViewById(R.id.class_details_root);
       // rootView.setVisibility(View.INVISIBLE);
        classResultRecycler = findViewById(R.id.term_results);
        className = findViewById(R.id.class_name_class);
        formTeacher = findViewById(R.id.class_form_teacher);
        emptyLayout = findViewById(R.id.class_details_empty_state);

        databaseHelper = new DatabaseHelper(this);
        try {
            classDao = DaoManager.createDao(databaseHelper.getConnectionSource(),ClassNameTable.class);
            teacherDao = DaoManager.createDao(databaseHelper.getConnectionSource(),TeachersTable.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db","");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        classResultRecycler.setLayoutManager(layoutManager);
        classResultRecycler.setHasFixedSize(true);
        classResultList = new ArrayList<>();
        adapter = new ClassTermsAdapter(classResultList, ClassResultDetails.this);
        classResultRecycler.setAdapter(adapter);

        Intent i = getIntent();
        classId = i.getStringExtra("class_id");
        class_name = i.getStringExtra("class_name").toUpperCase();
        className.setText(class_name);
        levelId = i.getStringExtra("levelId");

        QueryBuilder<ClassNameTable,Long> queryBuilder=classDao.queryBuilder();
        try {
            queryBuilder.where().eq("classId",classId);
            List<ClassNameTable> classList = queryBuilder.query();

            QueryBuilder<TeachersTable,Long> queryBuilder1=teacherDao.queryBuilder();
            queryBuilder1.where().eq("staffId",classList.get(0).getFormTeacher());
            List<TeachersTable> teacherList =queryBuilder1.query();
            try {
                formTeacher.setText(teacherList.get(0).getStaffSurname().toUpperCase() + " " + teacherList.get(0).getStaffFirstname().toUpperCase());
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        classEdit = findViewById(R.id.edit_class_btn);
        classEdit.setOnClickListener(v -> {
            Intent i1 = new Intent(ClassResultDetails.this, AddClass.class);
            i1.putExtra("class_id",classId);
            i1.putExtra("from","classEdit");
            startActivity(i1);
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);
        getClassTermsResultsApi();

        RelativeLayout viewStudent = findViewById(R.id.view_student);
        viewStudent.setOnClickListener(v -> {
            Intent intent = new Intent(ClassResultDetails.this,StudentContacts.class);
            intent.putExtra("levelId",levelId);
            intent.putExtra("classId",classId);
            intent.putExtra("from","class_detail");
            startActivity(intent);
        });
        RelativeLayout registerCourse = findViewById(R.id.course_registration);
        registerCourse.setOnClickListener(v -> {
            Intent intent = new Intent(ClassResultDetails.this,RegYearList.class);
            intent.putExtra("levelId",levelId);
            intent.putExtra("classId",classId);
            startActivity(intent);
        });

        RelativeLayout courseAttendance =
                findViewById(R.id.course_attendance);
        courseAttendance.setOnClickListener(v -> {
            classResultList.size();
            Intent intent = new Intent(ClassResultDetails.this, AttendanceActivity.class);
            intent.putExtra("levelId",levelId);
            intent.putExtra("classId",classId);
            intent.putExtra("className", class_name);
            intent.putExtra("from", "result");
            startActivity(intent);

        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_class_menu,menu);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();

        QueryBuilder<ClassNameTable,Long> queryBuilder=classDao.queryBuilder();
        try {
            queryBuilder.where().eq("classId",classId);
            List<ClassNameTable> classList = queryBuilder.query();
            className.setText(classList.get(0).getClassName().toUpperCase());


            QueryBuilder<TeachersTable,Long> queryBuilder1=teacherDao.queryBuilder();
            queryBuilder1.where().eq("staffId",classList.get(0).getFormTeacher());
            List<TeachersTable> teacherList =queryBuilder1.query();
            if(teacherList.size()>0) {
                formTeacher.setText(teacherList.get(0).getStaffSurname().toUpperCase() + " " + teacherList.get(0).getStaffFirstname().toUpperCase());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getClassTermsResultsApi(){

        String url = Login.urlBase+"/jsonTerms.php?class="+classId+"&_db="+db;
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                rootView.setVisibility(View.VISIBLE);
                dialog1.dismiss();
                //rootView.setVisibility(View.VISIBLE);
                Log.i("response",response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if(jsonArray.get(0) instanceof JSONObject){
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        Iterator<String> keys = jsonObject.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            String schoolYear = key;
                            boolean first_term = false;
                            boolean second_term = false;
                            boolean third_term = false;
                            if (jsonObject.get(key) instanceof JSONObject) {
                                // do something with jsonObject here
                                JSONObject object1 = jsonObject.getJSONObject(key);

                                JSONObject termObject = object1.getJSONObject(
                                        "terms");

                                if (termObject.has("1")) {
                                    first_term = true;
                                }
                                if (termObject.has("2")) {
                                    second_term = true;
                                }
                                if (termObject.has("3")) {
                                    third_term = true;
                                }


                            }

                            classResultList.add(new ClassTermResulsModel(schoolYear, first_term, second_term, third_term));
                            emptyLayout.setVisibility(View.GONE);
                            classResultRecycler.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                    }else {
                        classResultRecycler.setVisibility(View.GONE);
                        emptyLayout.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, error -> {
            dialog1.dismiss();
            Toast.makeText(this, "Something went wrong!",
                    Toast.LENGTH_SHORT).show();
            classResultRecycler.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            ImageView imageView = findViewById(R.id.image);
            TextView  textView = findViewById(R.id.error_message);
            imageView.setImageResource(R.drawable.no_internet);
            textView.setText("Seems like you're not connected to the internet!");
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            case R.id.delete_menu:
                AlertDialog.Builder builder = new AlertDialog.Builder(ClassResultDetails.this);
                builder.setMessage("Delete ?");
                builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callDeleteClassApi();
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
                break;

        }
        return false;
    }

    private void callDeleteClassApi() {
            final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .textMarginTop(10)
                    .fadeColor(Color.DKGRAY).build();
            dialog1.setCanceledOnTouchOutside(false);
            dialog1.show();

            String url = "http://linkskool.com/newportal/api/deleteClass.php?id="+classId+"&_db="+db;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    dialog1.dismiss();
                    Log.i("response",response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status =  jsonObject.getString("status");
                        if(status.equals("success")){
                            DeleteBuilder<ClassNameTable, Long> deleteBuilder = classDao.deleteBuilder();
                            deleteBuilder.where().eq("classId",classId );
                            deleteBuilder.delete();
                            onBackPressed();
                            Toast.makeText(ClassResultDetails.this,"Operation was successful",Toast.LENGTH_SHORT).show();
                        }else if(status.equals("failed")){
                            Toast.makeText(ClassResultDetails.this,"Operation failed",Toast.LENGTH_SHORT).show();

                        }
                    } catch (JSONException | SQLException e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog1.dismiss();
                    Toast.makeText(ClassResultDetails.this,"Error connecting to server",Toast.LENGTH_SHORT).show();
                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(ClassResultDetails.this);
            requestQueue.add(stringRequest);
        }

}
