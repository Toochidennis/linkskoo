package com.digitaldream.winskool.fragments;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.digitaldream.winskool.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;


public class ReceiptsFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private final String[] mMonth = new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    private GraphicalView mGraphicalView;
    private XYMultipleSeriesRenderer mSeriesRenderer;
    private XYMultipleSeriesDataset mSeriesDataset;

    private LinearLayout mLayout;


    public ReceiptsFragment() {
        // Required empty public constructor
    }

    public static ReceiptsFragment newInstance(String param1, String param2) {
        ReceiptsFragment fragment = new ReceiptsFragment();
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
        View view = inflater.inflate(R.layout.fragment_receipt,
                container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        mLayout = view.findViewById(R.id.chart);
        setHasOptionsMenu(true);
        toolbar.setTitle("Receipt");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(sView -> requireActivity().onBackPressed());

        return view;
    }


    private void initChart() {
        int[] chartLength = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        double[] income = {100000, 80000, 200000, 50000, 20000, 10000, 80000, 0, 0, 0, 0, 0};

        XYSeries XYSeries = new XYSeries("Income");

        for (int i = 0; i < chartLength.length; i++) {
            XYSeries.add(chartLength[i], income[i]);
        }

        mSeriesDataset = new XYMultipleSeriesDataset();
        mSeriesDataset.addSeries(XYSeries);

        XYSeriesRenderer XYSeriesRenderer = new XYSeriesRenderer();
        XYSeriesRenderer.setColor(Color.WHITE);
        XYSeriesRenderer.setFillPoints(true);
        XYSeriesRenderer.setDisplayChartValues(true);
        XYSeriesRenderer.setChartValuesTextSize(20);


        mSeriesRenderer = new XYMultipleSeriesRenderer();
        mSeriesRenderer.setXLabels(0);
        mSeriesRenderer.setYLabels(0);
        //mSeriesRenderer.setMargins(new int[]{60,80,20,20});
        mSeriesRenderer.setXTitle("Year 2023");
        mSeriesRenderer.setXLabelsColor(Color.WHITE);
        mSeriesRenderer.setBackgroundColor(Color.parseColor("#130C6B"));
        mSeriesRenderer.setZoomEnabled(false, true);
        mSeriesRenderer.addSeriesRenderer(XYSeriesRenderer);
        mSeriesRenderer.setMarginsColor(Color.parseColor("#130C6B"));
        mSeriesRenderer.setPanEnabled(false, false);
        mSeriesRenderer.setShowGrid(true);
        mSeriesRenderer.setGridLineWidth(1);
        mSeriesRenderer.setLabelsTextSize(20);
        mSeriesRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        mSeriesRenderer.setYLabelsColor(0, Color.WHITE);
        mSeriesRenderer.setXLabelsColor(Color.WHITE);
        mSeriesRenderer.setApplyBackgroundColor(true);
        mSeriesRenderer.setBarSpacing(.8);

        for (int i = 0; i < chartLength.length; i++) {
            mSeriesRenderer.addXTextLabel(i + 1, mMonth[i]);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mGraphicalView == null) {
            initChart();
            mGraphicalView = ChartFactory.getBarChartView(getContext(), mSeriesDataset,
                    mSeriesRenderer, BarChart.Type.DEFAULT);
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