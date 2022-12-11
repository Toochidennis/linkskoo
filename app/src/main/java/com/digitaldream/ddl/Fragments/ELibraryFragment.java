package com.digitaldream.ddl.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.digitaldream.ddl.R;

import java.util.ArrayList;
import java.util.List;

public class ELibraryFragment extends Fragment {

    private ImageSlider mImageSlider;
    private List<SlideModel> mSlideModelList;
    private Toolbar mToolbar;
    private ActionBar mActionBar;


    public ELibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_e_library, container,
                false);

        mToolbar = view.findViewById(R.id.toolbar);
        mImageSlider = view.findViewById(R.id.imageSlider);

        ((AppCompatActivity) (getActivity())).setSupportActionBar(mToolbar);
        mActionBar =
                ((AppCompatActivity) (getActivity())).getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.arrow_left);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("E-library");
        setHasOptionsMenu(true);
        mToolbar.setNavigationOnClickListener(v->  getActivity().onBackPressed());

        mSlideModelList = new ArrayList<>();

        mSlideModelList.add(new SlideModel(R.drawable.ic_kids_lessons, ScaleTypes.FIT));
        mSlideModelList.add(new SlideModel(R.drawable.ic_tutorials_slider,
                ScaleTypes.FIT));
        mSlideModelList.add(new SlideModel(R.drawable.ic_library_slider ,
                ScaleTypes.FIT));

        mImageSlider.setImageList(mSlideModelList);

        //the manipulated man
        //the anatomy of female
        //the west and the rest of us
        //after God is dibia


        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


}