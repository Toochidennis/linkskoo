package com.digitaldream.ddl;

import com.digitaldream.ddl.models.AssessmentModel;
import com.digitaldream.ddl.models.ClassNameTable;
import com.digitaldream.ddl.models.CommentTable;
import com.digitaldream.ddl.models.CourseOutlineTable;
import com.digitaldream.ddl.models.CourseTable;
import com.digitaldream.ddl.models.Exam;
import com.digitaldream.ddl.models.ExamQuestions;
import com.digitaldream.ddl.models.ExamType;
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
import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;

public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    private static final Class<?>[] classes = new Class[]{StudentTable.class,
            TeachersTable.class, ClassNameTable.class, LevelTable.class,
            NewsTable.class, CourseTable.class,
            StudentResultDownloadTable.class, StudentCourses.class,
            VideoTable.class, GradeModel.class, GeneralSettingModel.class,
            VideoUtilTable.class
            , AssessmentModel.class, Exam.class, ExamQuestions.class,
            ExamType.class, StaffTableUtil.class, FormClassModel.class,
            TeacherCourseModel.class, TeacherCourseModelCopy.class,
            CourseOutlineTable.class, CommentTable.class};

    public static void main(String[] args) throws IOException, SQLException {
        writeConfigFile("ormlite_config", classes);
    }
}
