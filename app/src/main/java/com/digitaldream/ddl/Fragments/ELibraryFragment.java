package com.digitaldream.ddl.Fragments;

import android.content.Intent;
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
import android.widget.RelativeLayout;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.digitaldream.ddl.Activities.StaffUtils;
import com.digitaldream.ddl.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//the manipulated man
//the anatomy of female
//the west and the rest of us
//after God is dibia

public class ELibraryFragment extends Fragment {

    private ImageSlider mImageSlider;
    private List<SlideModel> mSlideModelList;
    private Toolbar mToolbar;
    private ActionBar mActionBar;
    private RelativeLayout mCbt, mVideos, mGames, mBooks;


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
        mCbt = view.findViewById(R.id.cbt_btn);
        mVideos = view.findViewById(R.id.videos_btn);
        mGames = view.findViewById(R.id.games_btn);
        mBooks = view.findViewById(R.id.books_btn);

        ((AppCompatActivity) (Objects.requireNonNull(getActivity()))).setSupportActionBar(mToolbar);
        mActionBar =
                ((AppCompatActivity) (getActivity())).getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.arrow_left);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("E-library");
        setHasOptionsMenu(true);
        mToolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());

        mSlideModelList = new ArrayList<>();

        mSlideModelList.add(new SlideModel(R.drawable.ic_kids_lessons, ScaleTypes.FIT));
        mSlideModelList.add(new SlideModel(R.drawable.ic_tutorials_slider,
                ScaleTypes.FIT));
        mSlideModelList.add(new SlideModel(R.drawable.ic_library_slider,
                ScaleTypes.FIT));

        mImageSlider.setImageList(mSlideModelList);


        mCbt.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), StaffUtils.class);
            intent.putExtra("from", "cbt_exam");
            startActivity(intent);
        });


        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


}