package com.digitaldream.ddl.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitaldream.ddl.Models.CourseOutlineTable;
import com.digitaldream.ddl.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StudentElearningCourseAdapter extends RecyclerView.Adapter<StudentElearningCourseAdapter.StudentElearningCourseViewHolder> {
    private Context context;
    private List<CourseOutlineTable> courseList;
    OnCourseClickListener onCourseClickListener;
    private List<Object> list = new ArrayList<>();
    private List<CourseOutlineTable> courseLists= new ArrayList<>();

    public StudentElearningCourseAdapter(Context context, List<CourseOutlineTable> courseList, OnCourseClickListener onCourseClickListener) {
        this.context = context;
        this.courseList = courseList;
        this.onCourseClickListener = onCourseClickListener;

    }

    @NonNull
    @Override
    public StudentElearningCourseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.elearning_course_item,viewGroup,false);
        return new StudentElearningCourseViewHolder(view,onCourseClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentElearningCourseViewHolder studentElearningCourseViewHolder, int i) {
        CourseOutlineTable ct = courseList.get(i);
        String courseName = ct.getCourseName();
        studentElearningCourseViewHolder.courseName.setText(courseName.toUpperCase());
        studentElearningCourseViewHolder.courseInitials.setText(courseName.substring(0,1).toUpperCase());
        GradientDrawable gd = (GradientDrawable) studentElearningCourseViewHolder.linearLayout.getBackground().mutate();
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        gd.setColor(currentColor);
        //gd.invalidateSelf();
        studentElearningCourseViewHolder.linearLayout.setBackground(gd);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    class StudentElearningCourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView courseName,courseInitials;
        OnCourseClickListener onCourseClickListener;
        LinearLayout linearLayout;

        public StudentElearningCourseViewHolder(@NonNull View itemView, OnCourseClickListener onCourseClickListener) {
            super(itemView);
            courseName = itemView.findViewById(R.id.elearning_course);
            courseInitials = itemView.findViewById(R.id.course_initials);
            linearLayout = itemView.findViewById(R.id.initials_bg);
            this.onCourseClickListener = onCourseClickListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onCourseClickListener.onCourseClick(getAdapterPosition());
        }
    }

    public interface OnCourseClickListener{
        void onCourseClick(int position);
    }
}
