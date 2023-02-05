package com.digitaldream.winskool.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.digitaldream.winskool.R;
import com.digitaldream.winskool.activities.PaymentActivity;

import java.util.Objects;


public class StudentPaymentFragment extends Fragment {

    public StudentPaymentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_student_payment, container,
                false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        setHasOptionsMenu(true);
        toolbar.setTitle("Payment");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(sView -> Objects.requireNonNull(
                getActivity()).onBackPressed());

        Button button = view.findViewById(R.id.view_details);

        button.setOnClickListener(sView->{
            startActivity(new Intent(getActivity(), PaymentActivity.class).putExtra("from",
                    "fee_details"));
        });

       return view;
    }
}