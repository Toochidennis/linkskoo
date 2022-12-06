package com.digitaldream.ddl.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digitaldream.ddl.Adapters.TabAdapter;
import com.digitaldream.ddl.R;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminResultFragment extends Fragment {
    private TabAdapter tabAdapter;
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private Toolbar toolbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_admin_result, container, false);
        setHasOptionsMenu(true);
        tabLayout = view.findViewById(R.id.tablayout);
        mViewPager = view.findViewById(R.id.view_pager);
        tabAdapter = new TabAdapter(Objects.requireNonNull(getActivity()).getSupportFragmentManager());
        tabAdapter.addFragment(new StudentFragment(), "Students");
        tabAdapter.addFragment(new ClassResultFragment(), "Class");

        mViewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)(getActivity())).setSupportActionBar(toolbar);
        ActionBar actionBar =  ((AppCompatActivity)(getActivity())).getSupportActionBar();
        actionBar.setTitle("View Result");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
}
