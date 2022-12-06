package com.digitaldream.ddl.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitaldream.ddl.Models.ViewResponseModel;
import com.digitaldream.ddl.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewResponseAdapter extends RecyclerView.Adapter<ViewResponseAdapter.ViewResponseVH>{
    private Context context;
    private List<ViewResponseModel> list;
    OnResponseClickListener onResponseClickListener;

    public ViewResponseAdapter(Context context, List<ViewResponseModel> list,OnResponseClickListener onResponseClickListener) {
        this.context = context;
        this.list = list;
        this.onResponseClickListener = onResponseClickListener;
    }

    @NonNull
    @Override
    public ViewResponseVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_response_item,parent,false);
        return new ViewResponseVH(view,onResponseClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewResponseVH holder, int position) {
        ViewResponseModel vm = list.get(position);
        GradientDrawable gd = (GradientDrawable) holder.bg.getBackground().mutate();
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        gd.setColor(currentColor);
        //gd.invalidateSelf();
        holder.bg.setBackground(gd);

        holder.number.setText(String.valueOf(position+1));
        holder.studentName.setText(vm.getStudentName());
        String date = vm.getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date1 = simpleDateFormat.parse(date);
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd MMM, yyyy");
            holder.date.setText(simpleDateFormat1.format(date1));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.score.setText(vm.getScore());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewResponseVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView number,studentName,score,date;
        LinearLayout bg;
        OnResponseClickListener onResponseClickListener;
        public ViewResponseVH(@NonNull View itemView,OnResponseClickListener onResponseClickListener) {
            super(itemView);
            number = itemView.findViewById(R.id.number);
            studentName = itemView.findViewById(R.id.student_name);
            score = itemView.findViewById(R.id.score);
            date = itemView.findViewById(R.id.date);
            bg = itemView.findViewById(R.id.num_bg);
            itemView.setOnClickListener(this);
            this.onResponseClickListener = onResponseClickListener;

        }

        @Override
        public void onClick(View v) {
            onResponseClickListener.onResponseClick(getAdapterPosition());
        }
    }

    public interface OnResponseClickListener{
        void onResponseClick(int position);
    }
}
