package com.digitaldream.ddl.Models;

public class SubjectResultModel {
    private String courseName;
    private String courseId;
    private String classId;

    public SubjectResultModel(String courseName, String courseId, String classId) {
        this.courseName = courseName;
        this.courseId = courseId;
        this.classId = classId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getClassId() {
        return classId;
    }
}
