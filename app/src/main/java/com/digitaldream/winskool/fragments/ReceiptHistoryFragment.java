package com.digitaldream.winskool.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.digitaldream.winskool.R;
import com.digitaldream.winskool.utils.UtilsFun;

import org.achartengine.GraphicalView;

import java.util.Objects;


public class ReceiptHistoryFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;


    private GraphicalView mGraphicalView;

    private LinearLayout mLayout;


    public ReceiptHistoryFragment() {
        // Required empty public constructor
    }

    public static ReceiptHistoryFragment newInstance(String param1, String param2) {
        ReceiptHistoryFragment fragment = new ReceiptHistoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_receipt_history,
                container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        mLayout = view.findViewById(R.id.chart);
        setHasOptionsMenu(true);
        toolbar.setTitle("Receipt");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(sView -> requireActivity().onBackPressed());

        return view;
    }



    @Override
    public void onResume() {
        super.onResume();

        if (mGraphicalView == null) {

            mGraphicalView = UtilsFun.drawGraph(new Integer[]{1000,20000,0,0,0,0,0,0,0,0,0,0},
                    Objects.requireNonNull(getContext()));
            mLayout.addView(mGraphicalView);
        } else {
            mGraphicalView.repaint();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}