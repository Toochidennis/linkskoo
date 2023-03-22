package com.digitaldream.ddl.fragments;


import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digitaldream.ddl.adapters.TabAdapter;
import com.digitaldream.ddl.R;
import com.google.android.material.tabs.TabLayout;


public class AdminResultFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_admin_result, container, false);
        TabLayout tabLayout = view.findViewById(R.id.tablayout);
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        TabAdapter tabAdapter = new TabAdapter(requireActivity().getSupportFragmentManager());
        tabAdapter.addFragment(new StudentFragment(), "Students");
        tabAdapter.addFragment(new ClassResultFragment(), "Class");

        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        toolbar.setTitle("View Result");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }

}
