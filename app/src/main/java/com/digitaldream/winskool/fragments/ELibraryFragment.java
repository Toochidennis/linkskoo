package com.digitaldream.winskool.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.activities.BooksActivity;
import com.digitaldream.winskool.activities.StaffUtils;
import com.google.android.gms.ads.AdView;

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
    private CardView mCbt, mVideos, mGames, mBooks;
    private AdView mAdView;


    public ELibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_e_library, container,
                false);

       /* MobileAds.initialize(Objects.requireNonNull(getContext()), sInitializationStatus -> {
            Log.d("AdMob", "Initialisation completed");

                   RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(List.of(
                        "33BE2250B43518CCDA7DE426D04EE231")).build();
        MobileAds.setRequestConfiguration(configuration);

        });*/

        mToolbar = view.findViewById(R.id.toolbar);
        mImageSlider = view.findViewById(R.id.imageSlider);
        mCbt = view.findViewById(R.id.cbt_btn);
        mVideos = view.findViewById(R.id.videos_btn);
        mGames = view.findViewById(R.id.games_btn);
        mBooks = view.findViewById(R.id.books_btn);
        mAdView = view.findViewById(R.id.adView);

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

        mImageSlider.setItemClickListener(sI -> {
            switch (sI) {
                case 0:
                case 1:
                case 2:
                    Toast.makeText(getContext(), "You clicked position " + sI,
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    try {
                        throw new Exception("(:");
                    } catch (Exception sE) {
                        sE.printStackTrace();
                    }
            }
        });


        mCbt.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), StaffUtils.class);
            intent.putExtra("from", "cbt");
            startActivity(intent);
        });

        mBooks.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), BooksActivity.class);
            startActivity(intent);
        });

        mVideos.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), StaffUtils.class);
            intent.putExtra("from", "videos");
            startActivity(intent);
        });

        mGames.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), StaffUtils.class);
            intent.putExtra("from", "games");
            startActivity(intent);
        });


      /*  AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        Log.d("AdMob",""+ mAdView.isShown());*/


        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }


}