package com.digitaldream.ddl.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;

import com.digitaldream.ddl.Fragments.AdminDashbordFragment;
import com.digitaldream.ddl.Fragments.AdminELearning;
import com.digitaldream.ddl.Fragments.AdminResultFragment;
import com.digitaldream.ddl.Fragments.ExamFragment;
import com.digitaldream.ddl.Fragments.FlashCardList;
import com.digitaldream.ddl.Models.CourseOutlineTable;
import com.digitaldream.ddl.Models.ExamType;
import com.digitaldream.ddl.Models.VideoTable;
import com.digitaldream.ddl.Models.VideoUtilTable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.digitaldream.ddl.Models.CourseTable;
import com.digitaldream.ddl.DatabaseHelper;
import com.digitaldream.ddl.ForceUpdateAsync;
import com.digitaldream.ddl.Models.LevelTable;
import com.digitaldream.ddl.Models.AssessmentModel;
import com.digitaldream.ddl.Models.ClassNameTable;
import com.digitaldream.ddl.Models.FormClassModel;
import com.digitaldream.ddl.Models.GeneralSettingModel;
import com.digitaldream.ddl.Models.GradeModel;
import com.digitaldream.ddl.Models.TeacherCourseModel;
import com.digitaldream.ddl.Models.TeacherCourseModelCopy;
import com.digitaldream.ddl.NewsAdapter;
import com.digitaldream.ddl.Models.NewsTable;
import com.digitaldream.ddl.R;
import com.digitaldream.ddl.Models.StudentTable;
import com.digitaldream.ddl.Models.TeachersTable;
import com.digitaldream.ddl.Utils.ContactUsDialog;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

public class Dashboard extends AppCompatActivity implements NewsAdapter.OnNewsClickListener {
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
    private LinearLayout news_empty_state;
    private RelativeLayout studentContainer,teacherContainer,resultContainer,classesContainer;
    private Dao<GeneralSettingModel,Long> generalSettingDao;
    private List<GeneralSettingModel> generalSettingsList;
    public static String db;
    private String user_name,school_name,userId;
    private TextView schoolName,user;
    private SwipeRefreshLayout newsRefresh;
    private ContactUsDialog dialog;
    private boolean fromLogin = false;
    private boolean isFirstTime=false;
    private BottomNavigationView bottomNavigationView;
    public static String check=null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        check="";


        setSupportActionBar(toolbar);
        Intent i = getIntent();
        String from = i.getStringExtra("from");
        databaseHelper = new DatabaseHelper(this);
        try {
            generalSettingDao = DaoManager.createDao(databaseHelper.getConnectionSource(), GeneralSettingModel.class);
            generalSettingsList = generalSettingDao.queryForAll();


        } catch (SQLException e) {
            e.printStackTrace();
        }
        /*getSupportActionBar().setTitle("Dashboard");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);*/
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        drawerLayout.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

        View headerView = navigationView.getHeaderView(0);
        user = headerView.findViewById(R.id.user);
        schoolName = headerView.findViewById(R.id.school_name);
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        try {
            school_name = generalSettingsList.get(0).getSchoolName().toLowerCase();
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        userId = sharedPreferences.getString("user_id","");
        user_name = sharedPreferences.getString("user","User ID: "+ userId);
        db = sharedPreferences.getString("db","");

        /*String session = generalSettingsList.get(0).getSchoolYear();
        //String term = generalSettingsList.get(0).getSchoolTerm();
        if(term.equals("1")){
            term = term+"st term";
        }else if(term.equals("2")){
            term = term+"nd term";
        }else if(term.equals("3")){
            term = term+"rd term";
        }
        try {
            int previousYear = Integer.parseInt(session) - 1;
            schoolSession.setText(String.valueOf(previousYear) + "/" + session + " " + term);
        }catch (Exception e){
            e.printStackTrace();
        }*/


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
        schoolName.setText(builder.toString());
        //school_Name.setText(builder.toString());
        bottomNavigationView = findViewById(R.id.bottom_navigation_student);
        if (from!=null && from.equals("testupload")){
            FlashCardList.refresh=true;
            bottomNavigationView.getMenu().findItem(R.id.flashcard).setChecked(true);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FlashCardList()).commit();

        }else {
            bottomNavigationView.getMenu().findItem(R.id.student_dashboard).setChecked(true);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AdminDashbordFragment()).commit();
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.dashboard:
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        return  true;
                    case R.id.student_contacts:
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        Intent intent = new Intent(Dashboard.this,StudentContacts.class);
                        startActivity(intent);
                        return  true;
                    case R.id.view_result:
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        Intent intent2 = new Intent(Dashboard.this,ViewResult.class);
                        startActivity(intent2);
                        return  true;
                    case R.id.teachers_contacts:
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        Intent intent3 = new Intent(Dashboard.this,TeacherContacts.class);
                        startActivity(intent3);
                        return  true;

                    case R.id.logout:
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        logout();
                        return  true;
                    default:
                        drawerLayout.closeDrawers();
                        return  true;
                }

            }

        });
        navigationView.getMenu().getItem(0).setChecked(true);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.student_dashboard:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AdminDashbordFragment()).commit();
                        return true;
                    case R.id.student_results:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AdminResultFragment()).commit();
                        return true;

                    case R.id.flashcard:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FlashCardList()).commit();
                        return true;
                    case R.id.student_cbt:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ExamFragment()).commit();
                        return true;

                    case R.id.student_elearning:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AdminELearning()).commit();
                        return true;
                }
                return false;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.settings:
                Intent i = new Intent(Dashboard.this, Settings.class);
                startActivity(i);
                break;
            case R.id.info:
                ContactUsDialog dialog = new ContactUsDialog(this);
                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                break;
        }
        return true;
    }



    @Override
    public void onNewsClick(int position) {

        Intent intent = new Intent(this, NewsPage.class);
        intent.putExtra("newsId",newsTitleList.get(position).getNewsId());
        startActivity(intent);
    }

    private void logout(){
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("loginStatus",false);
        editor.putString("user","");
        editor.putString("school_name","");
        editor.apply();
        try {
            TableUtils.clearTable(databaseHelper.getConnectionSource(),StudentTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),TeachersTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),ClassNameTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), LevelTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),NewsTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), CourseTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), GradeModel.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), GeneralSettingModel.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), AssessmentModel.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), TeacherCourseModelCopy.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), TeacherCourseModel.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), FormClassModel.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), CourseOutlineTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), VideoTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), VideoUtilTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), ExamType.class);



        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(Dashboard.this, Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(fromLogin && isFirstTime) {
            dialog = new ContactUsDialog(this);
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            isFirstTime=false;
        }else {
                forceUpdate();

        }
        navigationView.getMenu().getItem(0).setChecked(true);



        /*int seletedItemId = bottomNavigationView.getSelectedItemId();
        switch (seletedItemId){
                case R.id.student_dashboard:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AdminDashbordFragment()).commit();
                    break;
                case R.id.student_results:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AdminResultFragment()).commit();
                    break;

                case R.id.flashcard:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FlashCardList()).commit();
                    break;
                case R.id.student_cbt:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ExamFragment()).commit();
                    break;

                case R.id.student_elearning:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AdminElearning()).commit();
                    break;

        }*/
    }

    @Override
    public void onBackPressed() {
        int seletedItemId = bottomNavigationView.getSelectedItemId();
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else if (R.id.student_dashboard != seletedItemId) {
            setHomeItem(Dashboard.this);
        }
        else {
            super.onBackPressed();
        }
    }

    public void forceUpdate(){
        PackageManager packageManager = this.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo =  packageManager.getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String currentVersion = packageInfo.versionName;
        new ForceUpdateAsync(currentVersion,Dashboard.this).execute();
    }


    public String stripHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(html).toString();
        }
    }

    public static void setHomeItem(Activity activity) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                activity.findViewById(R.id.bottom_navigation_student);
        bottomNavigationView.setSelectedItemId(R.id.student_dashboard);
    }

}
