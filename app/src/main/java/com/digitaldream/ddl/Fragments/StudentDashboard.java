package com.digitaldream.ddl.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.ddl.Activities.AnswerView;
import com.digitaldream.ddl.Activities.Login;
import com.digitaldream.ddl.Activities.NewsView;
import com.digitaldream.ddl.Activities.QuestionView;
import com.digitaldream.ddl.Adapters.QAAdapter;
import com.digitaldream.ddl.Utils.CustomDialog;
import com.digitaldream.ddl.Utils.QuestionAccessViewSheet;
import com.digitaldream.ddl.Utils.QuestionBottomSheet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitaldream.ddl.DatabaseHelper;
import com.digitaldream.ddl.Models.NewsTable;
import com.digitaldream.ddl.R;
import com.digitaldream.ddl.Models.StudentCourses;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class StudentDashboard extends Fragment implements  QAAdapter.OnQuestionClickListener , QuestionAccessViewSheet.OnQuestionSubmitListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView schoolname,username,student_class,studentInitials;
    private RecyclerView qaRecycler,courseRecycler;
    private Dao<NewsTable,Long> newsDao;
    private List<NewsTable> newsTitleList=new ArrayList<>();
    private DatabaseHelper databaseHelper;
    private LinearLayout news_empty_state;
    private Dao<StudentCourses,Long> studentCoursesDao;
    private List<StudentCourses> studentCourses;
    private CardView courseList_empty_state;
    public static String level,db;
    List<QAAdapter.QAObject> list;
    private QAAdapter adapter;
    private LinearLayout emptyState;
    private QAAdapter.QAObject feed;
    public static QuestionBottomSheet questionBottomSheet=null;
    private static String json="";
    private boolean showDialog = true;
    CustomDialog dialog = null;
    private boolean allowRefresh = false;
    public static boolean refresh=false;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_student_dashboard, container, false);
        try {
            databaseHelper = new DatabaseHelper(getContext());
            newsDao = DaoManager.createDao(databaseHelper.getConnectionSource(),NewsTable.class);
            studentCoursesDao = DaoManager.createDao(databaseHelper.getConnectionSource(),StudentCourses.class);
            newsTitleList = newsDao.queryForAll();
            studentCourses = studentCoursesDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        toolbar = view.findViewById(R.id.toolbar);
        drawerLayout = view.findViewById(R.id.drawer_layout);
        navigationView = view.findViewById(R.id.navigation_view);
        qaRecycler = view.findViewById(R.id.qa_recycler);
        student_class = view.findViewById(R.id.student_class);
        studentInitials = view.findViewById(R.id.initials_student);
        emptyState = view.findViewById(R.id.qa_empty_state);

        qaRecycler.setNestedScrollingEnabled(false);




        ((AppCompatActivity)(getActivity())).setSupportActionBar(toolbar);
        ActionBar actionBar =  ((AppCompatActivity)(getActivity())).getSupportActionBar();

        schoolname = view.findViewById(R.id.school_name);
        username = view.findViewById(R.id.student_user);
        news_empty_state = view.findViewById(R.id.news_empty_state);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        String schoolName = sharedPreferences.getString("school_name","");
        String studentClass = sharedPreferences.getString("student_class","");
        level = sharedPreferences.getString("level","");
        db = sharedPreferences.getString("db","");
        student_class.setText(studentClass.toUpperCase());
        String[] strArr = schoolName.split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        for(String s : strArr){
            try {
                String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                stringBuilder.append(cap + " ");

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        String user = sharedPreferences.getString("user","");
        String[] strArr1 = user.split(" ");
        StringBuilder stringBuilder1 = new StringBuilder();
        for(String s : strArr1){
            try {
                String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                stringBuilder1.append(cap + " ");

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        username.setText(stringBuilder1.toString());
        actionBar.setTitle(stringBuilder.toString());
        String student_initial = user.substring(0,1).toUpperCase();
        studentInitials.setText(student_initial);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        qaRecycler.setLayoutManager(layoutManager);
        list=new ArrayList<>();

        adapter = new QAAdapter(getContext(),list,this);
        qaRecycler.setAdapter(adapter);


        FloatingActionButton addQuestionBtn = view.findViewById(R.id.add_question);
        addQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = ((FragmentActivity) getContext())
                        .getSupportFragmentManager()
                        .beginTransaction();
                questionBottomSheet = new QuestionBottomSheet();
                questionBottomSheet.show(transaction, "questionBottomSheet");


            }
        });

        QuestionAccessViewSheet q = QuestionAccessViewSheet.newInstance();
        q.setOnQuestionSubmittListener(new QuestionAccessViewSheet.OnQuestionSubmitListener() {
            @Override
            public void onSubmit() {
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!json.isEmpty()) {
            if(refresh){
                getFeed(level);
            }else {
                buildJSON(json);
            }
        }else {
            getFeed(level);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }



    @Override
    public void onQuestionClick(int position) {
        QAAdapter.QAObject object = list.get(position);
        if(object.getFeedType().equals("20")) {
            Intent intent = new Intent(getContext(), QuestionView.class);
            intent.putExtra("feed",object);
            startActivity(intent);
        }else if(object.getFeedType().equals("21")){
            Intent intent = new Intent(getContext(), AnswerView.class);
            intent.putExtra("feed", object);
            startActivity(intent);
        }else{
            Intent intent = new Intent(getContext(), NewsView.class);
            intent.putExtra("feed", object);
            startActivity(intent);
        }
    }




    public  void getFeed(String levelId){

            dialog = new CustomDialog(getActivity());
            dialog.show();

        String url = Login.urlBase+"/getFeed.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                json = response;
                list.clear();
                buildJSON(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id",levelId);
                params.put("_db",db);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void buildJSON(String response){
        try {
            JSONArray jsonArray = new JSONArray(response);
            for(int a=0;a<jsonArray.length();a++){
                JSONObject object = jsonArray.getJSONObject(a);
                String id = object.getString("id");
                String title = object.getString("title");
                String user = object.getString("author_name");
                String date = object.getString("upload_date");
                String commentsNo = object.getString("no_of_comment");
                String shareCount = object.getString("no_of_share");
                String upvotes = object.getString("no_of_like");
                String parent = object.getString("parent");
                String desc = object.getString("description");
                String type1 = object.getString("type");
                String body = "";
                body=object.optString("body");

                feed = new QAAdapter.QAObject();
                feed.setUser(user);

                    feed.setQuestionId(id);
                if(title==null || title.isEmpty()){
                    feed.setQuestion(desc);

                }else {
                    feed.setQuestion(title);
                }
                feed.setAnswer("");
                    feed.setPicUrl("");
                    feed.setId(id);
                    feed.setAnswerId(id);
                    if (!body.isEmpty()){

                        Object json = new JSONTokener(body).nextValue();

                        if(json instanceof JSONArray) {

                            JSONArray answer = new JSONArray(body);
                            boolean checktext =true;boolean checkImage = true;
                            for (int c = 0; c < answer.length(); c++) {
                                JSONObject object1 = answer.optJSONObject(c);
                                String type = object1.optString("type").trim();

                                if (type.equalsIgnoreCase("text")&&checktext) {
                                    String content = object1.optString("content");

                                    feed.setPreText(content);
                                    checktext=false;
                                }
                                if (type.equalsIgnoreCase("image")&&checkImage) {
                                    String content = object1.optString("src");
                                    feed.setPicUrl(content);
                                    checkImage=false;
                                }
                            }
                        }
                        feed.setAnswer(body);

                    //feed.setQuestion(desc);
                }

                feed.setDate(date);
                feed.setCommentNo(commentsNo);
                feed.setLikesNo(upvotes);
                feed.setShareNo(shareCount);
                feed.setFeedType(type1);
                list.add(feed);

            }

            Collections.reverse(list);
            if(list.isEmpty()){
                emptyState.setVisibility(View.VISIBLE);

            }else {
                emptyState.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSubmit() {
    }
}
