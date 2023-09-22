package com.digitaldream.linkskool.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.models.CourseOutlineTable;
import com.digitaldream.linkskool.utils.FunctionUtils;

import java.util.ArrayList;
import java.util.List;

public class StudentELearningAdapter extends
        RecyclerView.Adapter<StudentELearningAdapter.StudentElearningCourseViewHolder> {
    private final List<CourseOutlineTable> courseList;
    OnCourseClickListener onCourseClickListener;

    public StudentELearningAdapter(List<CourseOutlineTable> courseList, OnCourseClickListener onCourseClickListener) {
        this.courseList = courseList;
        this.onCourseClickListener = onCourseClickListener;

    }

    @NonNull
    @Override
    public StudentElearningCourseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.elearning_course_item, viewGroup, false);
        return new StudentElearningCourseViewHolder(view, onCourseClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentElearningCourseViewHolder holder, int i) {
        CourseOutlineTable ct = courseList.get(i);

        holder.bind(ct);

    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    static class StudentElearningCourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView courseName, courseInitials;
        OnCourseClickListener onCourseClickListener;
        LinearLayout linearLayout;

        public StudentElearningCourseViewHolder(@NonNull View itemView, OnCourseClickListener onCourseClickListener) {
            super(itemView);
            courseName = itemView.findViewById(R.id.course_name);
            courseInitials = itemView.findViewById(R.id.course_initials);
            linearLayout = itemView.findViewById(R.id.initials_bg);
            this.onCourseClickListener = onCourseClickListener;
            itemView.setOnClickListener(this);

        }

        void bind(CourseOutlineTable sCourseOutlineTable) {
            String mCourseName = sCourseOutlineTable.getCourseName();
            courseName.setText(mCourseName.toUpperCase());
            courseInitials.setText(mCourseName.substring(0, 1).toUpperCase());

            FunctionUtils.getRandomColor(linearLayout);
        }

        @Override
        public void onClick(View v) {
            onCourseClickListener.onCourseClick(getAdapterPosition());
        }
    }

    public interface OnCourseClickListener {
        void onCourseClick(int position);
    }
}
