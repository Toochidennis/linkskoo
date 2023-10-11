package com.digitaldream.toyibatskool.utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.digitaldream.toyibatskool.R;

final class HeaderViewHolder extends RecyclerView.ViewHolder {
    final TextView title;
    public HeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        title =itemView.findViewById(R.id.coursename);
    }
}
