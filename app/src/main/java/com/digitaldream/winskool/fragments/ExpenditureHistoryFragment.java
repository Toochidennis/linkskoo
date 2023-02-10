package com.digitaldream.winskool.fragments;

import static com.digitaldream.winskool.utils.NotificationUtilsKt.CHANNEL_ID_1;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.activities.Login;
import com.digitaldream.winskool.activities.PaymentActivity;
import com.digitaldream.winskool.utils.Methods;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.achartengine.GraphicalView;

import java.util.HashMap;
import java.util.Map;


public class ExpenditureHistoryFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private GraphicalView mGraphicalView;

    private LinearLayout mLayout;


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
        mLayout = view.findViewById(R.id.chart);

        toolbar.setTitle("Expenditure History");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(sView -> requireActivity().onBackPressed());

        FloatingActionButton floatingActionButton =
                view.findViewById(R.id.add_expenditure);

        floatingActionButton.setOnClickListener(sView -> startActivity(new Intent(getContext(),
                PaymentActivity.class).putExtra("from", "add_expenditure"))
        );


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mGraphicalView == null) {

            mGraphicalView = Methods.drawGraph(
                    new Integer[]{1000, 200, 550, 660, 0, 0, 0, 0, 880, 0, 0, 0},
                    requireContext());
            mLayout.addView(mGraphicalView);
        } else {
            mGraphicalView.repaint();
        }
        takeClassAttendance();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    private void takeClassAttendance() {

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(
                "loginDetail", Context.MODE_PRIVATE);
        String db = sharedPreferences.getString("db", "");

        String url = Login.urlBase + "/manageFees.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url,
                response -> Log.i("Response", response), Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("delete", "25");
                stringMap.put("_db", db);
                return stringMap;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);

    }


}