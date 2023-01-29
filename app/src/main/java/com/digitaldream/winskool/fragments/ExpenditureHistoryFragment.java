package com.digitaldream.winskool.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.digitaldream.winskool.R;
import com.digitaldream.winskool.activities.PaymentActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;


public class ExpenditureHistoryFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExpenditureHistoryFragment() {
        // Required empty public constructor
    }


    public static ExpenditureHistoryFragment newInstance(String param1, String param2) {
        ExpenditureHistoryFragment fragment = new ExpenditureHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_expenditure, container,
                false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        setHasOptionsMenu(true);
        toolbar.setTitle("Expenditure History");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(sView -> Objects.requireNonNull(
                getActivity()).onBackPressed());

        FloatingActionButton floatingActionButton =
                view.findViewById(R.id.add_expenditure);

        floatingActionButton.setOnClickListener(sView -> {

            startActivity(new Intent(getContext(), PaymentActivity.class).putExtra("from",
                    "add_expenditure"));
        });



        return view;
    }
}