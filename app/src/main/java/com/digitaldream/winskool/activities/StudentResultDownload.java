package com.digitaldream.winskool.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.models.LevelTable;
import com.digitaldream.winskool.models.StudentResultDownloadTable;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.adapters.StudentResultDownloadAdapter;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class StudentResultDownload extends AppCompatActivity implements StudentResultDownloadAdapter.OnStudentResultDownloadClickListener {
    private Toolbar toolbar;
    private List<StudentResultDownloadTable> studentResultDownloadList;
    private RecyclerView recyclerView;
    private String studentId,studentName;
    private TextView student_name;
    private StringBuilder builder;
    private DatabaseHelper databaseHelper;
    private Dao<LevelTable,Long> levelDao;
    private FrameLayout unemptyState,emptyState;
    private FloatingActionButton addResult,addResult2;
    private Dao<StudentResultDownloadTable,Long> studentResultDao;
    private TextView initialDisplay;
    private RelativeLayout call,sms,whatsapp,email;
    private ImageButton callIcon,smsIcon,emailIcon,whatsappIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_result_download);
        callIcon = findViewById(R.id.call_icon);
        smsIcon = findViewById(R.id.sms_icon);
        emailIcon = findViewById(R.id.email_icon);
        whatsappIcon = findViewById(R.id.whatsapp_icon);

        toolbar = findViewById(R.id.toolbar);
        student_name = findViewById(R.id.student_name_result);
        unemptyState = findViewById(R.id.staff_result_unempty_state);
        emptyState = findViewById(R.id.staff_result_download_empty_state);

        unemptyState.setVisibility(View.GONE);

        try {
            databaseHelper = new DatabaseHelper(this);
            levelDao = DaoManager.createDao(databaseHelper.getConnectionSource(),LevelTable.class);
            studentResultDao = DaoManager.createDao(databaseHelper.getConnectionSource(),StudentResultDownloadTable.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setTitle("Student Result");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);
        Intent i =getIntent();
        studentId = i.getStringExtra("student_id");
        studentName = i.getStringExtra("student_name").toLowerCase();
        final String phoneNumber = i.getStringExtra("phone_number");
        final String emailAddress = i.getStringExtra("email");
        String[] strArray = studentName.split(" ");
        builder = new StringBuilder();
        for (String s : strArray) {
            try {
                String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                builder.append(cap + " ");
            }catch (StringIndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }
        getStudentPreviousResult(studentId);
        studentResultDownloadList = new ArrayList<>();

        recyclerView = findViewById(R.id.student_result_download_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        initialDisplay = findViewById(R.id.initials_student_result);
        String initial = studentName.substring(0,1).toUpperCase();
        initialDisplay.setText(initial);

        call = findViewById(R.id.call_sbd);
        sms = findViewById(R.id.sms_sbd);
        whatsapp = findViewById(R.id.whatsapp_sbd);
        email = findViewById(R.id.email_sbd);


        if(phoneNumber.isEmpty()) {
            call.setEnabled(false);
            callIcon.setColorFilter(ContextCompat.getColor(this, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            sms.setEnabled(false);
            smsIcon.setColorFilter(ContextCompat.getColor(this, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            whatsapp.setEnabled(false);
            whatsappIcon.setColorFilter(ContextCompat.getColor(this, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);

        }


        if(emailAddress.isEmpty()){
            email.setEnabled(false);
            emailIcon.setColorFilter(ContextCompat.getColor(this, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!phoneNumber.isEmpty()) {
                    Intent i = new Intent(android.content.Intent.ACTION_DIAL,
                            Uri.parse("tel:" + phoneNumber));
                    startActivity(i);
                }else{
                    Toast.makeText(StudentResultDownload.this,"phone number is not available",Toast.LENGTH_SHORT).show();
                }
            }
        });
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!phoneNumber.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("smsto:" + phoneNumber));
                    startActivity(intent);
                }else{
                    Toast.makeText(StudentResultDownload.this,"phone number is not available",Toast.LENGTH_SHORT).show();
                }
            }
        });

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!phoneNumber.isEmpty()) {
                    Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + "234" + phoneNumber + "&text=" + "");
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);

                    startActivity(sendIntent);
                }else{
                    Toast.makeText(StudentResultDownload.this,"phone number is not available",Toast.LENGTH_SHORT).show();
                }
            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!emailAddress.isEmpty()) {
                    Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                    Uri data = Uri.parse("mailto:?subject=" + "subject text" + "&body=" + "body text " + "&to=" + emailAddress);
                    emailIntent.setData(data);
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                }else{
                    Toast.makeText(StudentResultDownload.this,"email is not available",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStudentResultDownloadClick(int position) {

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

    private void getStudentPreviousResult(final String id){
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(StudentResultDownload.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        String db = sharedPreferences.getString("db","");
        String url = Login.urlBase+"/jsonResult.php?id="+id+"&_db="+db;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog1.dismiss();
                Log.i("result",response);
                student_name.setText(builder.toString());
                emptyState.setVisibility(View.VISIBLE);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                        JSONObject object = jsonArray.getJSONObject(0);
                    Iterator<String> keys = object.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String schoolYear = key;
                        if (object.get(key) instanceof JSONObject) {
                            // do something with jsonObject here
                            JSONObject object1 = object.getJSONObject(key);
                            String className = object1.getString("class_name");
                            String levelID = object1.getString("level");
                            String classId = object1.getString("class_id");
                            JSONObject termObject = object1.getJSONObject("terms");
                            String first_term = "";
                            String second_term = "";
                            String third_term = "";

                            if (termObject.has("1")) {
                                first_term = termObject.getString("1");
                                first_term = "1st";

                            }
                            if (termObject.has("2")) {

                                second_term = termObject.getString("2");
                                second_term = "2nd";

                            }
                            if (termObject.has("3")) {
                                third_term = termObject.getString("3");
                                third_term = "3rd";

                            }
                                QueryBuilder<StudentResultDownloadTable, Long> queryBuilder = studentResultDao.queryBuilder();
                                queryBuilder.where().eq("level", levelID);
                                List<StudentResultDownloadTable> levelList = queryBuilder.query();
                                if(levelList.isEmpty()) {

                                    StudentResultDownloadTable st = new StudentResultDownloadTable();

                                    st.setFirstTerm(first_term);
                                    st.setSecondTerm(second_term);
                                    st.setThirdTerm(third_term);
                                    st.setLevel(levelID);
                                    st.setStudentId(studentId);
                                    //st.setLevelName(levelList.get(0).getLevelName());
                                    st.setLevelName(className);
                                    st.setSchoolYear(schoolYear);
                                    st.setClassId(classId);

                                    studentResultDao.create(st);
                                }

                            }

                    }

                    studentResultDownloadList = studentResultDao.queryForAll();
                    if (!studentResultDownloadList.isEmpty()) {
                        emptyState.setVisibility(View.GONE);
                        unemptyState.setVisibility(View.VISIBLE);
                        StudentResultDownloadAdapter studentResultDownloadAdapter = new StudentResultDownloadAdapter(StudentResultDownload.this, studentResultDownloadList, StudentResultDownload.this);
                        recyclerView.setAdapter(studentResultDownloadAdapter);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog1.dismiss();
                Toast.makeText(StudentResultDownload.this,"something went wrong",Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            TableUtils.clearTable(databaseHelper.getConnectionSource(), StudentResultDownloadTable.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
