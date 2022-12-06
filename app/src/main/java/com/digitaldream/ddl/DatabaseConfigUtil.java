package com.digitaldream.ddl;

import com.digitaldream.ddl.Models.AssessmentModel;
import com.digitaldream.ddl.Models.ClassNameTable;
import com.digitaldream.ddl.Models.CommentTable;
import com.digitaldream.ddl.Models.CourseOutlineTable;
import com.digitaldream.ddl.Models.CourseTable;
import com.digitaldream.ddl.Models.Exam;
import com.digitaldream.ddl.Models.ExamQuestions;
import com.digitaldream.ddl.Models.ExamType;
import com.digitaldream.ddl.Models.FormClassModel;
import com.digitaldream.ddl.Models.GeneralSettingModel;
import com.digitaldream.ddl.Models.GradeModel;
import com.digitaldream.ddl.Models.LevelTable;
import com.digitaldream.ddl.Models.NewsTable;
import com.digitaldream.ddl.Models.StaffTableUtil;
import com.digitaldream.ddl.Models.StudentCourses;
import com.digitaldream.ddl.Models.StudentResultDownloadTable;
import com.digitaldream.ddl.Models.StudentTable;
import com.digitaldream.ddl.Models.TeacherCourseModel;
import com.digitaldream.ddl.Models.TeacherCourseModelCopy;
import com.digitaldream.ddl.Models.TeachersTable;
import com.digitaldream.ddl.Models.VideoTable;
import com.digitaldream.ddl.Models.VideoUtilTable;
import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;

public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    private static final Class<?> [] classes = new Class[]{StudentTable.class, TeachersTable.class, ClassNameTable.class, LevelTable.class, NewsTable.class, CourseTable.class, StudentResultDownloadTable.class, StudentCourses.class, VideoTable.class, GradeModel.class, GeneralSettingModel.class, VideoUtilTable.class
    , AssessmentModel.class, Exam.class, ExamQuestions.class, ExamType.class, StaffTableUtil.class, FormClassModel.class, TeacherCourseModel.class, TeacherCourseModelCopy.class, CourseOutlineTable.class, CommentTable.class};

    public static void main(String[] args) throws IOException, SQLException {
        writeConfigFile("ormlite_config",classes);
    }
}
