package com.digitaldream.winskool.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.digitaldream.winskool.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;


public class ExpenditureFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExpenditureFragment() {
        // Required empty public constructor
    }


    public static ExpenditureFragment newInstance(String param1, String param2) {
        ExpenditureFragment fragment = new ExpenditureFragment();
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
        View view = inflater.inflate(R.layout.fragment_expenditure, container,
                false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        setHasOptionsMenu(true);
        toolbar.setTitle("Expenditure");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(sView-> Objects.requireNonNull(
                getActivity()).onBackPressed());

        FloatingActionButton floatingActionButton =
                view.findViewById(R.id.add_expenditure);



        return view;
    }
}