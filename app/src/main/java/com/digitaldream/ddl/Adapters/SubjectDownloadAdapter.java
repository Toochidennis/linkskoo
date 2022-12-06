package com.digitaldream.ddl.Adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitaldream.ddl.Activities.SubjectResultUtil;
import com.digitaldream.ddl.Models.SubjectResultModel;
import com.digitaldream.ddl.R;

import java.util.List;

public class SubjectDownloadAdapter extends RecyclerView.Adapter<SubjectDownloadAdapter.SubjectDownloadViewHolder> {
    private Context context;
    private List<SubjectResultModel> subjectDownloadList;
    OnSubjectDownloadClickListener onSubjectDownloadClickListener;

    public SubjectDownloadAdapter(Context context, List<SubjectResultModel> subjectDownloadList, OnSubjectDownloadClickListener onSubjectDownloadClickListener) {
        this.context = context;
        this.subjectDownloadList = subjectDownloadList;
        this.onSubjectDownloadClickListener = onSubjectDownloadClickListener;
    }

    @NonNull
    @Override
    public SubjectDownloadViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.subject_download_item,viewGroup,false);
        return new SubjectDownloadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectDownloadViewHolder subjectDownloadViewHolder, int i) {
        final SubjectResultModel smd=subjectDownloadList.get(i);
        subjectDownloadViewHolder.courseName.setText(smd.getCourseName().toUpperCase());
        subjectDownloadViewHolder.editResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, SubjectResultUtil.class);
                intent.putExtra("courseId",smd.getCourseId());
                intent.putExtra("class_id",smd.getClassId());
                intent.putExtra("status","edit");
                context.startActivity(intent);
            }
        });

        subjectDownloadViewHolder.viewResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SubjectResultUtil.class);
                intent.putExtra("courseId",smd.getCourseId());
                intent.putExtra("class_id",smd.getClassId());
                intent.putExtra("status","view");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectDownloadList.size();
    }

    class SubjectDownloadViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView courseName;
        private ImageView viewResult,editResult;
        OnSubjectDownloadClickListener onSubjectDownloadClickListener;

        public SubjectDownloadViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.course_name_result);
            viewResult = itemView.findViewById(R.id.class_view_result);
            editResult = itemView.findViewById(R.id.class_edit_result);
        }

        @Override
        public void onClick(View view) {
            onSubjectDownloadClickListener.onSubjectDownloadClick(getAdapterPosition());
        }

    }
    public interface OnSubjectDownloadClickListener{
        void onSubjectDownloadClick(int position);
    }
}
