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
import com.digitaldream.winskool.activities.PaystackPaymentActivity;
import com.digitaldream.winskool.activities.PaymentActivity;


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
        toolbar.setTitle("Payment");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(sView -> requireActivity().onBackPressed());

        Button button = view.findViewById(R.id.view_details);
        Button btnPayNow = view.findViewById(R.id.btn_pay_now);

        button.setOnClickListener(sView ->
                startActivity(new Intent(getActivity(),
                        PaymentActivity.class).putExtra("from",
                        "fee_details")));

        btnPayNow.setOnClickListener(sView ->
                startActivity(new Intent(getActivity(), PaystackPaymentActivity.class)
                        .putExtra("amount", 100)));

        return view;
    }
}