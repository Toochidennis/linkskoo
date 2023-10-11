package com.digitaldream.toyibatskool.config;

import com.digitaldream.toyibatskool.models.AssessmentModel;
import com.digitaldream.toyibatskool.models.ClassNameTable;
import com.digitaldream.toyibatskool.models.CommentTable;
import com.digitaldream.toyibatskool.models.CourseOutlineTable;
import com.digitaldream.toyibatskool.models.CourseTable;
import com.digitaldream.toyibatskool.models.Exam;
import com.digitaldream.toyibatskool.models.ExamQuestions;
import com.digitaldream.toyibatskool.models.ExamType;
import com.digitaldream.toyibatskool.models.FormClassModel;
import com.digitaldream.toyibatskool.models.GeneralSettingModel;
import com.digitaldream.toyibatskool.models.GradeModel;
import com.digitaldream.toyibatskool.models.LevelTable;
import com.digitaldream.toyibatskool.models.NewsTable;
import com.digitaldream.toyibatskool.models.StaffTableUtil;
import com.digitaldream.toyibatskool.models.StudentCourses;
import com.digitaldream.toyibatskool.models.StudentResultDownloadTable;
import com.digitaldream.toyibatskool.models.StudentTable;
import com.digitaldream.toyibatskool.models.TeacherCourseModel;
import com.digitaldream.toyibatskool.models.TeacherCourseModelCopy;
import com.digitaldream.toyibatskool.models.TeachersTable;
import com.digitaldream.toyibatskool.models.VideoTable;
import com.digitaldream.toyibatskool.models.VideoUtilTable;
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
