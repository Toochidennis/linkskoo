package com.digitaldream.ddl.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.digitaldream.ddl.Models.ExamType;
import com.digitaldream.ddl.R;

import java.util.List;

public class CBTExamTypeAdapter extends RecyclerView.Adapter<CBTExamTypeAdapter.ViewHolder> {

    private final Context mContext;
    private final List<ExamType> mExamTypeList;
    private final OnExamClickListener mOnExamClickListener;

    public CBTExamTypeAdapter(Context sContext, List<ExamType> sExamTypeList, OnExamClickListener sOnExamClickListener) {
        mContext = sContext;
        mExamTypeList = sExamTypeList;
        mOnExamClickListener = sOnExamClickListener;
    }

    private final int[] colors = {R.color.color_1, R.color.color_2,
            R.color.color_3, R.color.color_4, R.color.color_2, R.color.color_3
            , R.color.color_4, R.color.color_1, R.color.color1};


    @NonNull
    @Override
    public CBTExamTypeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(mContext).inflate(R.layout.fragment_cbt_exam_type_item, parent, false);
        return new ViewHolder(view, mOnExamClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CBTExamTypeAdapter.ViewHolder holder, int position) {
        String name = mExamTypeList.get(position).getExamName();
        String[] strings = name.toLowerCase().split(" ");
        StringBuilder stringBuilder = new StringBuilder();

        for (String letter : strings) {
            try {
                String initial = letter.substring(0, 1).toUpperCase();
                stringBuilder.append(initial);
            } catch (Exception sE) {
                sE.printStackTrace();
            }
        }

        holder.mTextView.setText(stringBuilder.toString());

        holder.mCardView.setCardBackgroundColor(ContextCompat.getColor(mContext,
                colors[position % 6]));

    }

    @Override
    public int getItemCount() {
        return mExamTypeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final OnExamClickListener mOnExamClickListener;
        private final CardView mCardView;
        private final TextView mTextView;

        public ViewHolder(@NonNull View itemView, OnExamClickListener sOnExamClickListener) {
            super(itemView);
            mOnExamClickListener = sOnExamClickListener;
            mCardView = itemView.findViewById(R.id.grid_layout);
            mTextView = itemView.findViewById(R.id.exam_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View sView) {
            mOnExamClickListener.onExamClick(getAdapterPosition());
        }
    }


    public interface OnExamClickListener {
        void onExamClick(int position);
    }
}
