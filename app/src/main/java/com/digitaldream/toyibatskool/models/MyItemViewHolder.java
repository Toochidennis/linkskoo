package com.digitaldream.toyibatskool.models;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitaldream.toyibatskool.R;

final class MyItemViewHolder extends RecyclerView.ViewHolder {
    final TextView classNames;
    final ImageView clearBtn;

    public MyItemViewHolder(@NonNull View itemView) {
        super(itemView);
        classNames = itemView.findViewById(R.id.classnames);
        clearBtn = itemView.findViewById(R.id.course_clear);
    }

}
