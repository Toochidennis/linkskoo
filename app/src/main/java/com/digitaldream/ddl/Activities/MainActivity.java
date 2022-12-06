package com.digitaldream.ddl.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.digitaldream.ddl.DatabaseHelper;
import com.digitaldream.ddl.ForceUpdateAsync;
import com.digitaldream.ddl.Models.ClassNameTable;
import com.digitaldream.ddl.Models.LevelTable;
import com.digitaldream.ddl.Models.NewsTable;
import com.digitaldream.ddl.Models.StudentTable;
import com.digitaldream.ddl.R;
import com.digitaldream.ddl.Models.TeachersTable;
import com.j256.ormlite.dao.Dao;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    private String url = "http://www.linkskool.com/newportal/api/mobile.php";
    private DatabaseHelper databaseHelper;
    private Dao<StudentTable,Long> studentDao;
    private Dao<TeachersTable,Long> teacherDao;
    private Dao<ClassNameTable,Long> classDao;
    private Dao<LevelTable,Long> levelDao;
    private List<StudentTable> student;
    private List<TeachersTable> teacher;
    private List<ClassNameTable> classnames;
    private List<LevelTable> levelNames;
    private List<NewsTable> newsList;
    private Dao<NewsTable,Long> newsDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // TextView linskoolText = findViewById(R.id.linkskool_text);
       // linskoolText.setVisibility(View.GONE);

        databaseHelper = new DatabaseHelper(this);

        //forceUpdate();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
                boolean isLogged = sharedPreferences.getBoolean("loginStatus",false);
                String who = sharedPreferences.getString("who","");
                if(databaseHelper.getWritableDatabase()==null){
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                    finish();
                }

                else if(isLogged && who.equals("admin")){
                    Intent intent = new Intent(MainActivity.this, Dashboard.class);
                    startActivity(intent);
                    finish();
                }
                else if(isLogged && who.equals("staff")){
                    Intent intent = new Intent(MainActivity.this, StaffDashboardActivity.class);
                    startActivity(intent);
                    finish();
                }else if(isLogged && who.equals("student")){
                    Intent intent = new Intent(MainActivity.this, StudentDashboardActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                    finish();}
            }
        },SPLASH_DISPLAY_LENGTH);
        ImageView linkskool =findViewById(R.id.logo);
        long startTimev=100;



        Animation infromBottom = inFromBottomAnimation(1500,startTimev);
        linkskool.startAnimation(infromBottom);
        infromBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
               // linskoolText.setVisibility(View.VISIBLE);
                final Animation in = new AlphaAnimation(0.0f, 1.0f);
                in.setDuration(1000);
              //  linskoolText.startAnimation(in);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });



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
        new ForceUpdateAsync(currentVersion,MainActivity.this).execute();
    }

    private Animation inFromLeftAnimation(int duration,Long startTime) {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(duration);
        inFromLeft.setStartOffset(startTime);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }
    private Animation inFromBottomAnimation(int duration,Long startTime) {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(duration);
        inFromLeft.setStartOffset(startTime);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }
}


