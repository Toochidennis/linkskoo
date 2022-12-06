package com.digitaldream.ddl.Activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.digitaldream.ddl.Adapters.StudentElearningCourseAdapter;
import com.digitaldream.ddl.DatabaseHelper;
import com.digitaldream.ddl.Models.CourseOutlineTable;
import com.digitaldream.ddl.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class StudentElearningCourses extends AppCompatActivity implements StudentElearningCourseAdapter.OnCourseClickListener {
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private DatabaseHelper databaseHelper;
    private Dao<CourseOutlineTable,Long> courseOutlineDao;
    private List<CourseOutlineTable> courseList;
    private String levelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_elearning_courses);
        recyclerView = findViewById(R.id.e_learning_student_course_recycler);
        toolbar = findViewById(R.id.toolbar);
        String levelName = getIntent().getStringExtra("levelName");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(levelName.toUpperCase()+" Courses");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        levelId = getIntent().getStringExtra("levelId");
        Log.i("level",""+levelId);
        databaseHelper = new DatabaseHelper(this);
        try {
            courseOutlineDao = DaoManager.createDao(databaseHelper.getConnectionSource(), CourseOutlineTable.class);
            QueryBuilder<CourseOutlineTable,Long> queryBuilder = courseOutlineDao.queryBuilder();
            queryBuilder.groupBy("courseId").where().eq("levelId",levelId);
            courseList = queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        StudentElearningCourseAdapter adapter = new StudentElearningCourseAdapter(this,courseList,this);
        recyclerView.setAdapter(adapter);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ((item.getItemId())){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    public void onCourseClick(int position) {
        Intent intent = new Intent(this,CourseOutlines.class);
        intent.putExtra("levelId",courseList.get(position).getLevelId());
        intent.putExtra("courseId",courseList.get(position).getCourseId());
        startActivity(intent);
    }
}
