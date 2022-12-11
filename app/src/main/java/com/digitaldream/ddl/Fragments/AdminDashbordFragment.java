package com.digitaldream.ddl.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.ddl.Activities.AnswerView;
import com.digitaldream.ddl.Activities.ClassListUtil;
import com.digitaldream.ddl.Activities.Login;
import com.digitaldream.ddl.Activities.NewsView;
import com.digitaldream.ddl.Activities.QuestionView;
import com.digitaldream.ddl.Activities.StudentContacts;
import com.digitaldream.ddl.Activities.TeacherContacts;
import com.digitaldream.ddl.Adapters.QAAdapter;
import com.digitaldream.ddl.DatabaseHelper;
import com.digitaldream.ddl.Models.ClassNameTable;
import com.digitaldream.ddl.Models.GeneralSettingModel;
import com.digitaldream.ddl.Models.NewsTable;
import com.digitaldream.ddl.Models.StudentTable;
import com.digitaldream.ddl.Models.TeachersTable;
import com.digitaldream.ddl.NewsAdapter;
import com.digitaldream.ddl.R;
import com.digitaldream.ddl.Utils.AddNewsBottomSheet;
import com.digitaldream.ddl.Utils.ContactUsDialog;
import com.digitaldream.ddl.Utils.CustomDialog;
import com.digitaldream.ddl.Utils.QuestionBottomSheet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
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
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminDashbordFragment extends Fragment implements NewsAdapter.OnNewsClickListener, QAAdapter.OnQuestionClickListener {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView teacherCount,studentCount,newsHeader,userName,school_Name,classesCount,schoolSession;
    private ViewPager viewPager;
    private List<NewsTable> newsTitleList;
    private RecyclerView recyclerView;
    private Dao<NewsTable,Long> newsDao;
    private DatabaseHelper databaseHelper;
    private Dao<StudentTable,Long> studentDao;
    private Dao<TeachersTable,Long> teacherDao;
    private Dao<ClassNameTable,Long> classDao;
    private List<StudentTable> studentList;
    private List<TeachersTable> teacherList;
    private List<ClassNameTable> classList;
    private RecyclerView qaRecycler;
    private LinearLayout news_empty_state;
    private LinearLayout studentContainer,teacherContainer,resultContainer,classesContainer;
    private Dao<GeneralSettingModel,Long> generalSettingDao;
    private List<GeneralSettingModel> generalSettingsList;
    public static String db;
    private String user_name,school_name,userId;
    private TextView schoolName,user, errorMessage;
    private SwipeRefreshLayout newsRefresh;
    private ContactUsDialog dialog;
    private boolean fromLogin = false;
    private boolean isFirstTime=false;
    private BottomNavigationView bottomNavigationView;
    NewsAdapter newsAdapter;
    RelativeLayout newsTitleContainer;
    private LinearLayout emptyState;
    private QAAdapter.QAObject feed;
    List<QAAdapter.QAObject> list;
    private QAAdapter adapter;
    public static QuestionBottomSheet questionBottomSheet=null;
    public static String json="";
    public static boolean refresh=false;
    private LinearLayoutManager layoutManager;
    private final String LIST_KEY="2";
    private Parcelable listState;
    private Bundle bundle;
    private int currentPage=1;
    private ProgressBar progressBar;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_dashbord, container, false);
        setHasOptionsMenu(true);
        databaseHelper = new DatabaseHelper(getContext());
        emptyState = view.findViewById(R.id.qa_empty_state);
        errorMessage = view.findViewById(R.id.error_message);


        try {
            newsDao = DaoManager.createDao(databaseHelper.getConnectionSource(), NewsTable.class);
            studentDao = DaoManager.createDao(databaseHelper.getConnectionSource(), StudentTable.class);
            teacherDao = DaoManager.createDao(databaseHelper.getConnectionSource(), TeachersTable.class);
            classDao = DaoManager.createDao(databaseHelper.getConnectionSource(), ClassNameTable.class);
            generalSettingDao = DaoManager.createDao(databaseHelper.getConnectionSource(), GeneralSettingModel.class);
            studentList = studentDao.queryForAll();
            teacherList = teacherDao.queryForAll();
            newsTitleList = newsDao.queryForAll();
            classList = classDao.queryForAll();
            generalSettingsList = generalSettingDao.queryForAll();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        fromLogin = getActivity().getIntent().getBooleanExtra("isFromLogin",false);
        if(fromLogin==true){
            isFirstTime = true;
        }
        toolbar = view.findViewById(R.id.toolbar);

        studentCount = view.findViewById(R.id.no_of_student_txt1);
        teacherCount = view.findViewById(R.id.no_of_teacher_txt1);
        classesCount = view.findViewById(R.id.no_of_classes);
        studentContainer = view.findViewById(R.id.student_no_cont);
        teacherContainer =view.findViewById(R.id.teacher_no_container);
        classesContainer = view.findViewById(R.id.class_no_container);


        try {
            studentCount.setText(String.valueOf(studentList.size()));
            teacherCount.setText(String.valueOf(teacherList.size()));
            classesCount.setText(String.valueOf(classList.size()));
        }catch (Exception e){
            e.printStackTrace();
        }



        news_empty_state = view.findViewById(R.id.qa_empty_state);
        userName = view.findViewById(R.id.user_name);
        school_Name = view.findViewById(R.id.school_name_1);

        ((AppCompatActivity)(getActivity())).setSupportActionBar(toolbar);
        ActionBar actionBar =  ((AppCompatActivity)(getActivity())).getSupportActionBar();


        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        school_name = generalSettingsList.get(0).getSchoolName().toLowerCase();
        userId = sharedPreferences.getString("user_id","");
        user_name = sharedPreferences.getString("user","User ID: "+ userId);
        db = sharedPreferences.getString("db","");

        String session = generalSettingsList.get(0).getSchoolYear();
        String term = generalSettingsList.get(0).getSchoolTerm();
        if(term.equals("1")){
            term = term+"st term";
        }else if(term.equals("2")){
            term = term+"nd term";
        }else if(term.equals("3")){
            term = term+"rd term";

        }


        if(!user_name.equals("null")){
            try {
                user_name = user_name.substring(0, 1).toUpperCase() + user_name.substring(1);
                school_name = school_name.substring(0, 1).toUpperCase() + school_name.substring(1);
                user.setText(user_name);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        String[] strArray = school_name.split(" ");
        StringBuilder builder = new StringBuilder();
        try {
            for (String s : strArray) {
                String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                builder.append(cap + " ");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        school_Name.setText(builder.toString());

        studentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), StudentContacts.class);
                startActivity(intent);
            }
        });

        teacherContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(getContext(), TeacherContacts.class);
                startActivity(intent3);
            }
        });



        classesContainer.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ClassListUtil.class);
            startActivity(intent);
        });

        qaRecycler = view.findViewById(R.id.qa_recycler);


        qaRecycler.setNestedScrollingEnabled(false);
        layoutManager = new LinearLayoutManager(getContext());
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
                AddNewsBottomSheet addNewsBottomSheet = new AddNewsBottomSheet();
                addNewsBottomSheet.show(transaction, "newsBottomSheet");
                //Intent intent = new Intent(getContext(), AddNews.class);
                //startActivity(intent);
            }
        });
        if(json.isEmpty()) {
            getFeed();
        }else {
            if(refresh){
                getFeed();
            }else {
                parseJSON(json);
            }
        }
        progressBar = view.findViewById(R.id.progress_bar);
        NestedScrollView nestedScrollView = view.findViewById(R.id.scroll_view);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                progressBar.setVisibility(View.VISIBLE);
                if(scrollY==v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
                   Toast.makeText(getContext(),"bottom",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }



    public String stripHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(html).toString();
        }
    }


    @Override
    public void onNewsClick(int position) {

    }

    @Override
    public void onPause() {
        super.onPause();
        bundle = new Bundle();
       listState = Objects.requireNonNull(qaRecycler.getLayoutManager()).onSaveInstanceState();
        bundle.putParcelable(LIST_KEY, listState);
    }

    @Override
    public void onResume() {
        super.onResume();
        qaRecycler.smoothScrollToPosition(5);
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

    private void getFeed(){
        CustomDialog dialog = new CustomDialog(getActivity());
        dialog.show();

        String url = Login.urlBase+"/getFeed.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response", "cn"+response);
                dialog.dismiss();
                if(progressBar!=null){

                }
                json=response;
                parseJSON(response);

            }
        }, error -> {
            dialog.dismiss();
           /* Toast.makeText(getContext(), "Something went wrong!",
                    Toast.LENGTH_SHORT).show();*/
            qaRecycler.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            errorMessage.setText("Failed to load News, please try again!");
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("id","0");
                params.put("_db",db);
                params.put("page", String.valueOf(currentPage));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void parseJSON(String response){
        try {
            list.clear();
            JSONArray jsonArray = new JSONArray(response);
            for (int a = 0; a < jsonArray.length(); a++) {
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
                body = object.optString("body");


                feed = new QAAdapter.QAObject();
                feed.setUser(user);
                feed.setId(id);

                    feed.setQuestionId(id);
                    if(title==null || title.isEmpty()){
                        feed.setQuestion(desc);

                    }else {
                        feed.setQuestion(title);
                    }

                    if (!body.isEmpty()) {

                        Object json = new JSONTokener(body).nextValue();

                        if (json instanceof JSONArray) {

                            JSONArray answer = new JSONArray(body);
                            boolean checktext = true;
                            boolean checkImage = true;
                            for (int c = 0; c < answer.length(); c++) {
                                JSONObject object1 = answer.optJSONObject(c);
                                String type = object1.optString("type").trim();

                                if (type.equalsIgnoreCase("text") && checktext) {
                                    String content = object1.optString("content");

                                    feed.setPreText(content);
                                    checktext = false;
                                }
                                if (type.equalsIgnoreCase("image") && checkImage) {
                                    String content = object1.optString("src");
                                    feed.setPicUrl(content);
                                    checkImage = false;
                                }
                            }
                        }
                        feed.setAnswer(body);
                    }


                feed.setDate(date);
                feed.setCommentNo(commentsNo);
                feed.setLikesNo(upvotes);
                feed.setShareNo(shareCount);
                feed.setFeedType(type1);
                list.add(feed);

            }

            Collections.reverse(list);
            if (list.isEmpty()) {
                emptyState.setVisibility(View.VISIBLE);

            } else {
                emptyState.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
