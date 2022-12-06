package com.digitaldream.ddl.Adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.digitaldream.ddl.Models.StudentResultDownloadTable;
import com.digitaldream.ddl.R;
import com.digitaldream.ddl.ViewResultWebView;

import java.util.List;

public class StudentResultDownloadAdapter extends RecyclerView.Adapter<StudentResultDownloadAdapter.StudentResultDownloadViewHolder> {
    Context context;
    private List<StudentResultDownloadTable> studentResultDownloadList;
    OnStudentResultDownloadClickListener onStudentResultDownloadClickListener;

    public StudentResultDownloadAdapter(Context context, List<StudentResultDownloadTable> studentResultDownloadList, OnStudentResultDownloadClickListener onStudentResultDownloadClickListener) {
        this.context = context;
        this.studentResultDownloadList = studentResultDownloadList;
        this.onStudentResultDownloadClickListener = onStudentResultDownloadClickListener;
    }

    @NonNull
    @Override
    public StudentResultDownloadViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.results_item,viewGroup,false);
        return new StudentResultDownloadViewHolder(view,onStudentResultDownloadClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentResultDownloadViewHolder studentResultDownloadViewHolder, int i) {
        final StudentResultDownloadTable model = studentResultDownloadList.get(i);
        studentResultDownloadViewHolder.classname.setText(model.getLevelName().toUpperCase());
        studentResultDownloadViewHolder.resultYear.setText(model.getSchoolYear()+" session");
        if(model.getFirstTerm().equals("")){
            studentResultDownloadViewHolder.btn1.setVisibility(View.GONE);

        }else{
            studentResultDownloadViewHolder.btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ViewResultWebView.class);
                    intent.putExtra("level",model.getLevel());
                    intent.putExtra("term","1");
                    intent.putExtra("studentId",model.getStudentId());
                    intent.putExtra("year",model.getSchoolYear());
                    intent.putExtra("classId",model.getClassId());
                    context.startActivity(intent);
                }
            });

        }
        if(model.getSecondTerm().equals("")){
            studentResultDownloadViewHolder.btn2.setVisibility(View.GONE);

        }else{
            studentResultDownloadViewHolder.btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,ViewResultWebView.class);
                    intent.putExtra("level",model.getLevel());
                    intent.putExtra("term","2");
                    intent.putExtra("studentId",model.getStudentId());
                    intent.putExtra("year",model.getSchoolYear());
                    intent.putExtra("classId",model.getClassId());
                    context.startActivity(intent);
                }
            });

        }
        if(model.getThirdTerm().equals("")){
            studentResultDownloadViewHolder.btn3.setVisibility(View.GONE);
        }else{
            studentResultDownloadViewHolder.btn3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,ViewResultWebView.class);
                    intent.putExtra("level",model.getLevel());
                    intent.putExtra("term","3");
                    intent.putExtra("studentId",model.getStudentId());
                    intent.putExtra("year",model.getSchoolYear());
                    intent.putExtra("classId",model.getClassId());
                    context.startActivity(intent);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        if(studentResultDownloadList.isEmpty()){
            return 0;
        }else {
            return studentResultDownloadList.size();
        }
    }

    class StudentResultDownloadViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView resultTerm,classname,resultYear;
        OnStudentResultDownloadClickListener onStudentResultDownloadClickListener;
        CardView btn1,btn2,btn3;

        public StudentResultDownloadViewHolder(@NonNull View itemView, OnStudentResultDownloadClickListener onStudentResultDownloadClickListener) {
            super(itemView);
            resultTerm = itemView.findViewById(R.id.result_term);
            btn1 = itemView.findViewById(R.id.btn1);
            btn2 = itemView.findViewById(R.id.btn2);
            btn3 = itemView.findViewById(R.id.btn3);
            resultYear = itemView.findViewById(R.id.result_year);
            classname = itemView.findViewById(R.id.result_class_name);
            this.onStudentResultDownloadClickListener = onStudentResultDownloadClickListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onStudentResultDownloadClickListener.onStudentResultDownloadClick(getAdapterPosition());
        }
    }
    public interface OnStudentResultDownloadClickListener{
        void onStudentResultDownloadClick(int position);
    }
}
