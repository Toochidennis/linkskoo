package com.digitaldream.ddl.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.digitaldream.ddl.Activities.ClassResultDetails;
import com.digitaldream.ddl.Activities.ClassResultDownload;
import com.digitaldream.ddl.Models.ClassTermResulsModel;
import com.digitaldream.ddl.R;
import com.digitaldream.ddl.Activities.ViewClassResultWebview;

import java.util.List;
import java.util.Random;

public class ClassTermsAdapter extends RecyclerView.Adapter<ClassTermsAdapter.ClassTermResultViewHolder> {
    private List<ClassTermResulsModel> classTermResultList;
    private Context context;

    public ClassTermsAdapter(List<ClassTermResulsModel> classTermResultList, Context context) {
        this.classTermResultList = classTermResultList;
        this.context = context;
    }

    @NonNull
    @Override
    public ClassTermResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.class_term_item,viewGroup,false);
        return new ClassTermResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassTermResultViewHolder classTermResultViewHolder, int i) {
        final ClassTermResulsModel cmd = classTermResultList.get(i);
        if(!cmd.getFirstTerm()){
            classTermResultViewHolder.firstTermContainer.setVisibility(View.GONE);
        }
        if(!cmd.getSecondTerm()){
            classTermResultViewHolder.secondTermContainer.setVisibility(View.GONE);
        }
        if(!cmd.getThirdTerm()){
            classTermResultViewHolder.thirdTermContainer.setVisibility(View.GONE);
        }
        int previousYear = Integer.parseInt(cmd.getSchoolSession())-1;
        classTermResultViewHolder.session.setText(String.valueOf(previousYear)+" / "+cmd.getSchoolSession());
        classTermResultViewHolder.courses1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ClassResultDownload.class);
                intent.putExtra("class_id", ClassResultDetails.classId);
                intent.putExtra("session",cmd.getSchoolSession());
                intent.putExtra("class_name",ClassResultDetails.class_name);
                intent.putExtra("term","1");
                context.startActivity(intent);
            }
        });
        classTermResultViewHolder.courses2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ClassResultDownload.class);
                intent.putExtra("class_id", ClassResultDetails.classId);
                intent.putExtra("session",cmd.getSchoolSession());
                intent.putExtra("term","2");
                context.startActivity(intent);
            }
        });

        classTermResultViewHolder.courses3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ClassResultDownload.class);
                intent.putExtra("class_id", ClassResultDetails.classId);
                intent.putExtra("session",cmd.getSchoolSession());
                intent.putExtra("term","3");
                context.startActivity(intent);
            }
        });
        GradientDrawable gd1 = (GradientDrawable) classTermResultViewHolder.first_bg.getBackground().mutate();
        GradientDrawable gd2 = (GradientDrawable) classTermResultViewHolder.second_bg.getBackground().mutate();
        GradientDrawable gd3 = (GradientDrawable) classTermResultViewHolder.third_bg.getBackground().mutate();


        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        gd1.setColor(currentColor);
        classTermResultViewHolder.first_bg.setBackground(gd1);
        int currentColor2 = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        gd2.setColor(currentColor2);
        classTermResultViewHolder.second_bg.setBackground(gd2);
        int currentColor3 = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        gd3.setColor(currentColor3);
        classTermResultViewHolder.third_bg.setBackground(gd3);


        classTermResultViewHolder.compositeResult1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewClassResultWebview.class);
                intent.putExtra("classId",ClassResultDetails.classId);
                intent.putExtra("session",cmd.getSchoolSession());
                intent.putExtra("term","1");
                context.startActivity(intent);
            }
        });

        classTermResultViewHolder.compositeResult2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewClassResultWebview.class);
                intent.putExtra("classId",ClassResultDetails.classId);
                intent.putExtra("session",cmd.getSchoolSession());
                intent.putExtra("term","2");
                context.startActivity(intent);
            }
        });

        classTermResultViewHolder.compositeResult3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewClassResultWebview.class);
                intent.putExtra("classId",ClassResultDetails.classId);
                intent.putExtra("session",cmd.getSchoolSession());
                intent.putExtra("term","3");
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return classTermResultList.size();
    }

    class ClassTermResultViewHolder extends RecyclerView.ViewHolder{
        private TextView session,viewAnnual;
        private ImageView courses1,courses2,courses3,compositeResult1,compositeResult2,compositeResult3;
        private RelativeLayout firstTermContainer,secondTermContainer,thirdTermContainer;
        private LinearLayout first_bg,second_bg,third_bg;
        TextView initial;

        public ClassTermResultViewHolder(@NonNull View itemView) {
            super(itemView);
            session = itemView.findViewById(R.id.session);
            viewAnnual = itemView.findViewById(R.id.view_annual_result);
            courses1 = itemView.findViewById(R.id.course_1);
            courses2 = itemView.findViewById(R.id.course_2);
            courses3 = itemView.findViewById(R.id.course_3);
            compositeResult1 = itemView.findViewById(R.id.composite_result);
            compositeResult2 = itemView.findViewById(R.id.composite_result_2);
            compositeResult3 = itemView.findViewById(R.id.composite_result_3);
            firstTermContainer = itemView.findViewById(R.id.first_term_container);
            secondTermContainer = itemView.findViewById(R.id.second_term_container);
            thirdTermContainer = itemView.findViewById(R.id.third_term_container);
            first_bg = itemView.findViewById(R.id.bg_color);
            second_bg = itemView.findViewById(R.id.bg_color2);
            third_bg =itemView.findViewById(R.id.bg_color3);
            initial = itemView.findViewById(R.id.initial);

        }
    }
}
