package com.digitaldream.ddl.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.ddl.Activities.AttendanceDetails;
import com.digitaldream.ddl.Activities.ClassAttendance;
import com.digitaldream.ddl.Activities.Login;
import com.digitaldream.ddl.Adapters.AttendanceAdapter;
import com.digitaldream.ddl.DatabaseHelper;
import com.digitaldream.ddl.Models.GeneralSettingModel;
import com.digitaldream.ddl.Models.StudentTable;
import com.digitaldream.ddl.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;


public class ClassAttendanceFragment extends Fragment implements AttendanceAdapter.OnDayClickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";

    private FloatingActionButton mAttendanceBtn, mTakeAttendance, mFilterAttendance,
            mAttendanceBtnEmpty, mTakeAttendanceEmpty, mFilterAttendanceEmpty;
    private Animation mFabOpen, mFabClose, mRotateForward, mRotateBackward;
    private SwipeRefreshLayout mRefresh, mRefreshEmpty;

    private RelativeLayout mEmptyLayout;
    private RecyclerView mRecyclerView;
    private TextView errorMessage;
    private ImageView errorImage;
    private List<StudentTable> mStudentTableList;
    private AttendanceAdapter mAttendanceAdapter;
    private DatePickerDialog mDatePickerDialog;
    private List<StudentTable> mStudentTable;

    private String mStudentClassId;
    private String mStudentLevelId;
    private String mStudentClass;
    private String from;
    private String year, term, db;

    private boolean isOpen = false;


    public ClassAttendanceFragment() {
        // Required empty public constructor
    }


    public static ClassAttendanceFragment newInstance(String param1,
                                                      String param2,
                                                      String param3,
                                                      String param4) {
        ClassAttendanceFragment fragment = new ClassAttendanceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStudentClassId = getArguments().getString(ARG_PARAM1);
            mStudentLevelId = getArguments().getString(ARG_PARAM2);
            mStudentClass = getArguments().getString(ARG_PARAM3);
            from = getArguments().getString(ARG_PARAM4);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_class_attendance,
                container, false);


       Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle(
                "Attendance Details");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
        setHasOptionsMenu(true);

        if (from.equals("staff")){
            toolbar.setVisibility(View.VISIBLE);
        }else{
            toolbar.setVisibility(View.GONE);
        }


        SharedPreferences sharedPreferences = Objects.requireNonNull(getContext())
                .getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db", "");
        term = sharedPreferences.getString("term", "");
        year = sharedPreferences.getString("school_year", "");
        Log.i("term", year);


        mStudentTableList = new ArrayList<>();
        mStudentTable = new ArrayList<>();

        mRecyclerView = view.findViewById(R.id.attendance_recycler);
        mEmptyLayout = view.findViewById(R.id.empty_state);
        errorImage = view.findViewById(R.id.image);
        errorMessage = view.findViewById(R.id.error_message);


        mAttendanceAdapter = new AttendanceAdapter(getContext(),
                mStudentTableList, this);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAttendanceAdapter);

        fabButtonAction(view);

        getPreviousAttendance();


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        mStudentTableList.clear();
        getAttendance();
    }

    public void fabButtonAction(View sView) {


        //Un empty state
        mAttendanceBtn = sView.findViewById(R.id.attendance);
        mTakeAttendance = sView.findViewById(R.id.take_attendance);
        mFilterAttendance = sView.findViewById(R.id.filter_attendance);


        mFabOpen = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        mFabClose = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);

        mRotateForward = AnimationUtils.loadAnimation(getContext(),
                R.anim.rotate_forward);
        mRotateBackward = AnimationUtils.loadAnimation(getContext(),
                R.anim.rotate_backward);


        mAttendanceBtn.setOnClickListener(v -> onFabAnimation(mAttendanceBtn,
                mTakeAttendance, mFilterAttendance));

        mTakeAttendance.setOnClickListener(v -> {
            Intent newIntent = new Intent(getContext(),
                    ClassAttendance.class);
            newIntent.putExtra("levelId", mStudentLevelId);
            newIntent.putExtra("classId", mStudentClassId);
            newIntent.putExtra("from", "class");
            if (!mStudentTable.isEmpty()) {
                newIntent.putExtra("responseId",
                        mStudentTable.get(0).getStudentId());
                Log.i("responseId", mStudentTable.get(0).getStudentId());
            }

            startActivity(newIntent);
        });

        mFilterAttendance.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            mDatePickerDialog = new DatePickerDialog(getContext(), (sDatePicker, sYear, sMonth, sDayOfMonth) -> {

                int mont = sMonth + 1;
                String currentDate = sYear + "-" + mont + "-" + sDayOfMonth;
                currentDate = currentDate.concat(" 00:00:00");

                Intent newIntent = new Intent(getContext(),
                        AttendanceDetails.class);
                newIntent.putExtra("from", "class");
                newIntent.putExtra("class_name", mStudentClass);
                newIntent.putExtra("courseId", "0");
                newIntent.putExtra("classId", mStudentClassId);
                newIntent.putExtra("date", currentDate);
                newIntent.putExtra("db", db);
                startActivity(newIntent);


                //mAttendanceAdapter.getFilter().filter(currentDate);

            }, year, month, day);
            mDatePickerDialog.show();


        });


        //empty state
        mAttendanceBtnEmpty = sView.findViewById(R.id.attendance_empty);
        mTakeAttendanceEmpty = sView.findViewById(R.id.take_attendance_empty);
        mFilterAttendanceEmpty =
                sView.findViewById(R.id.filter_attendance_empty);


        mAttendanceBtnEmpty.setOnClickListener(v -> onFabAnimation(mAttendanceBtnEmpty,
                mTakeAttendanceEmpty, mFilterAttendanceEmpty));

        mTakeAttendanceEmpty.setOnClickListener(v -> {
            Intent newIntent = new Intent(getContext(),
                    ClassAttendance.class);
            newIntent.putExtra("levelId", mStudentLevelId);
            newIntent.putExtra("classId", mStudentClassId);
            newIntent.putExtra("from", "class");
            startActivity(newIntent);
        });


    }

    public void onFabAnimation(FloatingActionButton sAttend,
                               FloatingActionButton sTake,
                               FloatingActionButton sFilter) {
        if (isOpen) {
            sAttend.startAnimation(mRotateBackward);
            sTake.startAnimation(mFabClose);
            sFilter.startAnimation(mFabClose);
            sTake.setClickable(false);
            sFilter.setClickable(false);
            isOpen = false;
        } else {
            sAttend.startAnimation(mRotateForward);
            sTake.startAnimation(mFabOpen);
            sFilter.startAnimation(mFabOpen);
            sTake.setClickable(true);
            sFilter.setClickable(true);

            sTake.setVisibility(View.VISIBLE);
            sFilter.setVisibility(View.VISIBLE);

            isOpen = true;
        }
    }

    public void getAttendance() {

        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(getContext())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCancelable(false);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        String url = Login.urlBase + "/getAttendanceList.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, response -> {
            Log.i("response", response);
            dialog1.dismiss();
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String date = jsonObject.getString("date");
                    String count = jsonObject.getString("count");

                    String dateConverted = dateConverter(date);
                    Log.i("date", dateConverted);

                    StudentTable studentTable = new StudentTable();
                    studentTable.setDate(dateConverted);
                    studentTable.setStudentCount(count);
                    studentTable.setCourseCount(date);
                    mStudentTableList.add(studentTable);

                }
                mAttendanceAdapter.notifyDataSetChanged();

                if (!mStudentTableList.isEmpty()) {
                    mEmptyLayout.setVisibility(View.GONE);
                } else {
                    mEmptyLayout.setVisibility(View.VISIBLE);

                }

            } catch (JSONException | ParseException sE) {
                sE.printStackTrace();
            }

        }, error -> {
            dialog1.dismiss();
            Toast.makeText(getContext(), "Something went wrong!",
                    Toast.LENGTH_SHORT).show();
            mRecyclerView.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
            errorImage.setImageResource(R.drawable.no_internet);
            errorMessage.setText("Seems like you're not connected to the internet!");

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("year", year);
                stringMap.put("class", mStudentClassId);
                stringMap.put("course", "0");
                stringMap.put("term", term);
                stringMap.put("_db", db);
                return stringMap;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
        requestQueue.add(stringRequest);
    }


    public static String dateConverter(String date) throws ParseException {

        String format = date.replace(" ", "T").concat(".000Z");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd" +
                "'T'HH" + ":mm:ss.SSS'Z'", Locale.US);

        Date oldDate = simpleDateFormat.parse(format);

        assert oldDate != null;
        return DateFormat.getDateInstance(DateFormat.FULL).format(oldDate);

    }


    public void getPreviousAttendance() {
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(getContext())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCancelable(false);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();

        String url = Login.urlBase + "/getAttendance.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.i("Response", response);
                    dialog1.dismiss();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String responseId = jsonObject.getString("id");

                            StudentTable studentTable = new StudentTable();
                            studentTable.setStudentId(responseId);
                            mStudentTable.add(studentTable);
                        }

                    } catch (JSONException sE) {
                        sE.printStackTrace();
                    }

                }, error -> {
            error.printStackTrace();
            dialog1.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("class", mStudentClassId);
                stringMap.put("date", getDate());
                stringMap.put("course", "0");
                stringMap.put("_db", db);
                return stringMap;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
        requestQueue.add(stringRequest);

    }


    public String getDate() {
        String date;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        date = year + "-" + month + "-" + dayOfMonth;

        return date.concat(" 00:00:00");
    }


    @Override
    public void onDayClick(int position) {

        Intent newIntent = new Intent(getContext(),
                AttendanceDetails.class);
        newIntent.putExtra("from", "class");
        newIntent.putExtra("class_name", mStudentClass);
        newIntent.putExtra("courseId", "0");
        newIntent.putExtra("classId", mStudentClassId);
        newIntent.putExtra("date", mStudentTableList.get(position).getCourseCount());
        newIntent.putExtra("db", db);
        startActivity(newIntent);

    }


}