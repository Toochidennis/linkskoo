package com.digitaldream.ddl.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.ddl.DatabaseHelper;
import com.digitaldream.ddl.Models.LevelTable;
import com.digitaldream.ddl.Activities.Login;
import com.digitaldream.ddl.Models.ClassNameTable;
import com.digitaldream.ddl.R;
import com.digitaldream.ddl.StudentContactAdapter;
import com.digitaldream.ddl.Activities.StudentProfile;
import com.digitaldream.ddl.Models.StudentTable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsStaff extends Fragment implements StudentContactAdapter.OnStudentContactClickListener {
    private Toolbar toolbar;
    private Spinner level,classes;
    private List<String> spinnerLevelList,spinnerClassList;
    private RecyclerView recyclerView;
    private StudentContactAdapter studentContactAdapter;
    private List<StudentTable> studentContactList;
    private String studentLevelId, studentClass,db,staffId;
    private Dao<StudentTable,Long> studentDao;
    private DatabaseHelper databaseHelper;
    private List<ClassNameTable> classnames;
    private List<LevelTable> levelNames;
    private Dao<ClassNameTable,Long> classDao;
    private Dao<LevelTable,Long> levelDao;
    private LinearLayout empty_state;
    private FloatingActionButton addStudent,addStudent1;
    private Menu myMenu;
    private List<StudentTable> student;
    private SwipeRefreshLayout refreshStudents;
    private ArrayAdapter levelAdapter;


    public ContactsStaff() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_contacts_staff, container, false);
        setHasOptionsMenu(true);
        databaseHelper = new DatabaseHelper(getContext());
        try {
            studentDao = DaoManager.createDao(databaseHelper.getConnectionSource(),StudentTable.class);
            classDao = DaoManager.createDao(databaseHelper.getConnectionSource(),ClassNameTable.class);
            levelDao = DaoManager.createDao(databaseHelper.getConnectionSource(),LevelTable.class);
            classnames = classDao.queryForAll();
            levelNames = levelDao.queryForAll();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Contacts");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        level = view.findViewById(R.id.spinner_level_staff_student_contacts);
        classes = view.findViewById(R.id.spinner_class_staff_student_contact);
        empty_state = view.findViewById(R.id.staff_studentContact_empty_state);
        refreshStudents = view.findViewById(R.id.student_contacts_refresh);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db","");
        staffId = sharedPreferences.getString("user_id","");

        spinnerLevelList = new ArrayList<>();
        spinnerClassList = new ArrayList<>();

        for(int a=0; a<levelNames.size();a++){
            String level = levelNames.get(a).getLevelName().toUpperCase();
            spinnerLevelList.add(level);
        }

        recyclerView = view.findViewById(R.id.staff_student_contact_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        levelAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item, spinnerLevelList);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        level.setAdapter(levelAdapter);
        level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerClassList.clear();
                String levelSelected = adapterView.getItemAtPosition(i).toString();
                studentLevelId = levelNames.get(i).getLevelId();
                getStudentByLevel(studentLevelId);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter adapterClass = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item, spinnerClassList);
        adapterClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classes.setAdapter(adapterClass);


        studentContactList = new ArrayList<>();


        refreshStudents.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshStudentList();
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        myMenu = menu;
        if(studentContactList.size()==StudentContactAdapter.guardianPhones.size()){
            if(studentContactList.isEmpty()){
                menu.clear();
            }else {
                inflater.inflate(R.menu.selection_menu, menu);
                menu.findItem(R.id.select_all).setVisible(false);
            }

        }
        else if(StudentContactAdapter.guardianPhones.size()>0) {
            inflater.inflate(R.menu.selection_menu, menu);
        }
        else {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Contacts");
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String emails = "";
        String phoneNumbers ="";
        switch (item.getItemId()){
            case R.id.email_selection:
                if(!StudentContactAdapter.guardianEmails.isEmpty()) {
                    Intent emailIntent = new Intent(Intent.ACTION_VIEW);

                    for (int i = 0; i < StudentContactAdapter.guardianEmails.size(); i++) {
                        if (StudentContactAdapter.guardianEmails.get(i).isEmpty()) {
                            continue;
                        }
                        emails = emails + "" + StudentContactAdapter.guardianEmails.get(i) + ",";
                    }
                    try {
                        emails = emails.substring(0, emails.length() - 1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Uri data = Uri.parse("mailto:?subject=" + "subject text" + "&body=" + "body text " + "&to=" + emails);
                    emailIntent.setData(data);
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                }else{
                    Toast.makeText(getContext(), "There are no email address available", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.sms_selection:
                if(!StudentContactAdapter.guardianPhones.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    for (int i = 0; i < StudentContactAdapter.guardianPhones.size(); i++) {
                        if (StudentContactAdapter.guardianPhones.get(i).isEmpty()) {
                            continue;
                        }
                        phoneNumbers = phoneNumbers + "" + StudentContactAdapter.guardianPhones.get(i) + ",";
                    }
                    try {
                        phoneNumbers = phoneNumbers.substring(0, phoneNumbers.length() - 1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    intent.setData(Uri.parse("smsto:" + phoneNumbers));
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(),"There are no phone numbers available",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.select_all:
                StudentContactAdapter.flagValue = true;

               myMenu.findItem(R.id.select_all).setVisible(false);
                studentContactAdapter = new StudentContactAdapter(getContext(), studentContactList, ContactsStaff.this);
                recyclerView.setAdapter(studentContactAdapter);
                studentContactAdapter.notifyDataSetChanged();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStudentContactClick(int position) {
        Intent intent = new Intent(getContext(), StudentProfile.class);
        StudentTable st = new StudentTable();
        st.setStudentSurname(studentContactList.get(position).getStudentSurname());
        st.setStudentFirstname(studentContactList.get(position).getStudentFirstname());
        st.setStudentMiddlename(studentContactList.get(position).getStudentMiddlename());
        st.setStudentLevel(studentContactList.get(position).getStudentLevel());
        st.setGuardianName(studentContactList.get(position).getGuardianName());
        st.setGuardianPhoneNo(studentContactList.get(position).getGuardianPhoneNo());
        st.setGuardianEmail(studentContactList.get(position).getGuardianEmail());
        st.setGuardianAddress(studentContactList.get(position).getGuardianAddress());
        st.setStudentGender(studentContactList.get(position).getStudentGender());
        st.setStudentReg_no(studentContactList.get(position).getStudentReg_no());
        st.setStudentLevel(studentContactList.get(position).getStudentLevel());
        st.setStudentClass(studentContactList.get(position).getStudentClass());
        st.setState_of_origin(studentContactList.get(position).getState_of_origin());
        st.setDate_of_birth(studentContactList.get(position).getDate_of_birth());
        st.setStudentId(studentContactList.get(position).getStudentId());
        intent.putExtra("studentObject",st);
        startActivity(intent);
    }

    public void getStudentByLevel(String a) {

        QueryBuilder<StudentTable,Long> queryBuilder = studentDao.queryBuilder();
        try {
            queryBuilder.where().eq(StudentTable.STUDENTLEVEL,a);
            PreparedQuery<StudentTable> preparedQuery = queryBuilder.prepare();
            studentContactList = studentDao.query(preparedQuery);


            if(!studentContactList.isEmpty()) {

                setSpinnerClass(a);
            }else{
                empty_state.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                spinnerClassList.clear();
                spinnerClassList.add("");
                ArrayAdapter adapterClass = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinnerClassList);
                adapterClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                classes.setAdapter(adapterClass);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setSpinnerClass(String a) throws SQLException {
        QueryBuilder<ClassNameTable,Long> queryBuilder = classDao.queryBuilder();
        queryBuilder.where().eq("level",a);
        classnames = queryBuilder.query();
        Collections.reverse(classnames);
        if(!classnames.isEmpty()) {
            for (int i =0;i<classnames.size();i++){
                String classname = classnames.get(i).getClassName().toUpperCase();
                spinnerClassList.add(classname);
            }

            ArrayAdapter adapterClass = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinnerClassList);
            adapterClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            classes.setAdapter(adapterClass);
            classes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    QueryBuilder<StudentTable,Long> queryBuilder = studentDao.queryBuilder();
                    studentClass="";
                    if(classnames.size()>0) {
                        studentClass = classnames.get(i).getClassId();
                    }

                    try {
                        queryBuilder.where().eq("studentLevel",studentLevelId).and().eq("studentClass",studentClass);
                        studentContactList = queryBuilder.query();

                        if(!studentContactList.isEmpty()) {
                            empty_state.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            studentContactAdapter = new StudentContactAdapter(getContext(), studentContactList,ContactsStaff.this);
                            recyclerView.setAdapter(studentContactAdapter);
                            studentContactAdapter.notifyDataSetChanged();

                        }else{
                            recyclerView.setVisibility(View.GONE);
                            empty_state.setVisibility(View.VISIBLE);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }else {

            spinnerClassList.clear();
            spinnerClassList.add("");
            ArrayAdapter adapterClass = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinnerClassList);
            adapterClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            classes.setAdapter(adapterClass);


        }

    }

    private void refreshStudentList(){

        String login_url = Login.urlBase+"/allStaffStudent.php?staff_id="+staffId+"&_db="+db;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, login_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response",response);
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("teacherStudents")) {

                        JSONObject object = jsonObject.getJSONObject("teacherStudents");
                        JSONArray jsonArray = object.getJSONArray("rows");
                        if(jsonArray.length()>0) {

                            TableUtils.clearTable(databaseHelper.getConnectionSource(), StudentTable.class);
                            TableUtils.clearTable(databaseHelper.getConnectionSource(), LevelTable.class);
                            TableUtils.clearTable(databaseHelper.getConnectionSource(), ClassNameTable.class);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONArray jsonArray1 = jsonArray.getJSONArray(i);
                                String id = jsonArray1.getString(0);
                                String studentSurname = jsonArray1.getString(2);
                                String studentFirstname = jsonArray1.getString(3);
                                String studentMiddlename = jsonArray1.getString(4);
                                String studentGender = jsonArray1.getString(5);
                                String studentReg_no = jsonArray1.getString(12);
                                String studentClass = jsonArray1.getString(26);
                                String studentLevel = jsonArray1.getString(27);
                                String studentDOB = jsonArray1.getString(6);
                                String guardianName = jsonArray1.getString(14);
                                String guardianAddress = jsonArray1.getString(15);
                                String guardianEmail = jsonArray1.getString(16);
                                String guardianPhoneNo = jsonArray1.getString(17);
                                String lga = jsonArray1.getString(18);
                                String state_of_origin = jsonArray1.getString(19);
                                String nationality = jsonArray1.getString(20);
                                String date_admitted = jsonArray1.getString(22);
                                String class_name = jsonArray1.getString(28);
                                QueryBuilder<StudentTable, Long> queryBuilder = studentDao.queryBuilder();
                                queryBuilder.where().eq("studentId", id);
                                student = queryBuilder.query();
                                if (student.isEmpty()) {
                                    StudentTable st = new StudentTable();
                                    st.setStudentId(id);
                                    st.setStudentSurname(studentSurname);
                                    st.setStudentFirstname(studentFirstname);
                                    st.setStudentMiddlename(studentMiddlename);
                                    st.setStudentGender(studentGender);
                                    st.setStudentReg_no(studentReg_no);
                                    st.setStudentClass(studentClass);
                                    st.setStudentLevel(studentLevel);
                                    st.setGuardianName(guardianName);
                                    st.setGuardianAddress(guardianAddress);
                                    st.setGuardianEmail(guardianEmail);
                                    st.setGuardianPhoneNo(guardianPhoneNo);
                                    st.setLga(lga);
                                    st.setState_of_origin(state_of_origin);
                                    st.setNationality(nationality);
                                    st.setDate_admitted(date_admitted);
                                    st.setDate_of_birth(studentDOB);
                                    studentDao.create(st);
                                }
                            }
                            try {
                                QueryBuilder<StudentTable,Long> queryBuilder = studentDao.queryBuilder();
                                queryBuilder.where().eq("studentLevel",studentLevelId).and().eq("studentClass",studentClass);
                                studentContactList = queryBuilder.query();
                                recyclerView.setVisibility(View.VISIBLE);
                                empty_state.setVisibility(View.GONE);
                                if(!studentContactList.isEmpty()) {
                                    studentContactAdapter = new StudentContactAdapter(getContext(), studentContactList,ContactsStaff.this);
                                    recyclerView.setAdapter(studentContactAdapter);
                                    studentContactAdapter.notifyDataSetChanged();

                                }else{

                                    empty_state.setVisibility(View.VISIBLE);
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                            JSONObject jsonObject3 = jsonObject.getJSONObject("className");
                            JSONArray jsonArray4 = jsonObject3.getJSONArray("rows");
                            for (int i = 0; i < jsonArray4.length(); i++) {
                                JSONArray jsonArray5 = jsonArray4.getJSONArray(i);
                                String classId = jsonArray5.getString(0);
                                String className = jsonArray5.getString(1);
                                String level = jsonArray5.getString(2);
                                QueryBuilder<ClassNameTable, Long> queryBuilder = classDao.queryBuilder();
                                queryBuilder.where().eq("classId", classId);
                                classnames = queryBuilder.query();
                                if (classnames.isEmpty()) {
                                    ClassNameTable cn = new ClassNameTable();
                                    cn.setClassId(classId);
                                    cn.setClassName(className);
                                    cn.setLevel(level);
                                    classDao.create(cn);
                                }

                            }

                            JSONObject jsonObject6 = jsonObject.getJSONObject("levelName");
                            JSONArray jsonArray7 = jsonObject6.getJSONArray("rows");
                            for (int i = 0; i < jsonArray7.length(); i++) {
                                JSONArray jsonArray8 = jsonArray7.getJSONArray(i);
                                String levelId = jsonArray8.getString(0);
                                String levelName = jsonArray8.getString(1);
                                String schoolType = jsonArray8.getString(2);
                                QueryBuilder<LevelTable, Long> queryBuilder = levelDao.queryBuilder();
                                queryBuilder.where().eq("levelId", levelId);
                                levelNames = queryBuilder.query();
                                if (levelNames.isEmpty()) {
                                    LevelTable lt = new LevelTable();
                                    lt.setLevelId(levelId);
                                    lt.setLevelName(levelName);
                                    lt.setSchoolType(schoolType);
                                    levelDao.create(lt);
                                }

                            }

                        }

                        Toast.makeText(getContext(),"Student childList refreshed",Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
                refreshStudents.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                refreshStudents.setRefreshing(false);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
}
