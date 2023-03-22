package com.digitaldream.ddl.config;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.digitaldream.ddl.R;
import com.digitaldream.ddl.models.AssessmentModel;
import com.digitaldream.ddl.models.ClassNameTable;
import com.digitaldream.ddl.models.CommentTable;
import com.digitaldream.ddl.models.CourseOutlineTable;
import com.digitaldream.ddl.models.CourseTable;
import com.digitaldream.ddl.models.Exam;
import com.digitaldream.ddl.models.ExamQuestions;
import com.digitaldream.ddl.models.ExamType;
import com.digitaldream.ddl.models.FeedModel;
import com.digitaldream.ddl.models.FormClassModel;
import com.digitaldream.ddl.models.GeneralSettingModel;
import com.digitaldream.ddl.models.GradeModel;
import com.digitaldream.ddl.models.LevelTable;
import com.digitaldream.ddl.models.NewsTable;
import com.digitaldream.ddl.models.StaffTableUtil;
import com.digitaldream.ddl.models.StudentCourses;
import com.digitaldream.ddl.models.StudentResultDownloadTable;
import com.digitaldream.ddl.models.StudentTable;
import com.digitaldream.ddl.models.TeacherCourseModel;
import com.digitaldream.ddl.models.TeacherCourseModelCopy;
import com.digitaldream.ddl.models.TeachersTable;
import com.digitaldream.ddl.models.VideoTable;
import com.digitaldream.ddl.models.VideoUtilTable;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "linkskool.db";
    private static final int DATABASE_VERSION =7;
    Context context;

    private Dao<StudentTable,Long> studentDao;
    private Dao<TeachersTable,Long> teachersDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, StudentTable.class);
            TableUtils.createTableIfNotExists(connectionSource,TeachersTable.class);
            TableUtils.createTableIfNotExists(connectionSource, ClassNameTable.class);
            TableUtils.createTableIfNotExists(connectionSource,LevelTable.class);
            TableUtils.createTableIfNotExists(connectionSource, NewsTable.class);
            TableUtils.createTableIfNotExists(connectionSource, CourseTable.class);
            TableUtils.createTableIfNotExists(connectionSource,StudentCourses.class);
            TableUtils.createTableIfNotExists(connectionSource,StudentResultDownloadTable.class);
            TableUtils.createTableIfNotExists(connectionSource,VideoTable.class);
            TableUtils.createTableIfNotExists(connectionSource, GradeModel.class);
            TableUtils.createTableIfNotExists(connectionSource, GeneralSettingModel.class);
            TableUtils.createTableIfNotExists(connectionSource, AssessmentModel.class);
            TableUtils.createTableIfNotExists(connectionSource, VideoUtilTable.class);
            TableUtils.createTableIfNotExists(connectionSource, Exam.class);
            TableUtils.createTableIfNotExists(connectionSource, ExamQuestions.class);
            TableUtils.createTableIfNotExists(connectionSource, ExamType.class);
            TableUtils.createTableIfNotExists(connectionSource, StaffTableUtil.class);
            TableUtils.createTableIfNotExists(connectionSource, FormClassModel.class);
            TableUtils.createTableIfNotExists(connectionSource, TeacherCourseModel.class);
            TableUtils.createTableIfNotExists(connectionSource,TeacherCourseModelCopy.class);
            TableUtils.createTableIfNotExists(connectionSource, CourseOutlineTable.class);
            TableUtils.createTableIfNotExists(connectionSource, CommentTable.class);
            TableUtils.createTableIfNotExists(connectionSource, FeedModel.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource,StudentTable.class,true);
            TableUtils.dropTable(connectionSource,TeachersTable.class,true);
            TableUtils.dropTable(connectionSource,ClassNameTable.class,true);
            TableUtils.dropTable(connectionSource, LevelTable.class,true);
            TableUtils.dropTable(connectionSource,NewsTable.class,true);
            TableUtils.dropTable(connectionSource,CourseTable.class,true);
            TableUtils.dropTable(connectionSource, StudentCourses.class,true);
            TableUtils.dropTable(connectionSource, StudentResultDownloadTable.class,true);
            TableUtils.dropTable(connectionSource, VideoTable.class,true);
            TableUtils.dropTable(connectionSource,GradeModel.class,true);
            TableUtils.dropTable(connectionSource,GeneralSettingModel.class,true);
            TableUtils.dropTable(connectionSource,AssessmentModel.class,true);
            TableUtils.dropTable(connectionSource,VideoUtilTable.class,true);
            TableUtils.dropTable(connectionSource,Exam.class,true);
            TableUtils.dropTable(connectionSource,ExamType.class,true);
            TableUtils.dropTable(connectionSource,ExamQuestions.class,true);
            TableUtils.dropTable(connectionSource,StaffTableUtil.class,true);
            TableUtils.dropTable(connectionSource,FormClassModel.class,true);
            TableUtils.dropTable(connectionSource,TeacherCourseModel.class,true);
            TableUtils.dropTable(connectionSource, TeacherCourseModelCopy.class,true);
            TableUtils.dropTable(connectionSource,CourseOutlineTable.class,true);
            TableUtils.dropTable(connectionSource,CommentTable.class,true);
            TableUtils.dropTable(connectionSource,FeedModel.class,true);
            onCreate(database);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

    public Dao<StudentTable,Long> getStudentDao() throws SQLException {
        if(studentDao==null){
            studentDao = getDao(StudentTable.class);
        }
        return studentDao;
    }

    public Dao<TeachersTable,Long> getTeachersDao() throws SQLException {
        if(teachersDao==null){
            teachersDao = getDao(TeachersTable.class);
        }
        return teachersDao;
    }
}
