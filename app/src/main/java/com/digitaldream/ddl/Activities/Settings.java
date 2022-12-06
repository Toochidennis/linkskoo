package com.digitaldream.ddl.Activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.digitaldream.ddl.Adapters.SettingListAdapter;
import com.digitaldream.ddl.R;

public class Settings extends AppCompatActivity {
    private String[] settingsTitle={"General settings","Level","Courses","Grade","Assessment"};
    private ListView listView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);
        listView = findViewById(R.id.settings_list);

        SettingListAdapter adapter = new SettingListAdapter(this,settingsTitle);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent intent = new Intent(Settings.this, GeneralSettings.class);
                        startActivity(intent);
                        break;
                    case 1:
                        Intent intent0 = new Intent(Settings.this, LevelSettings.class);
                        startActivity(intent0);
                        break;
                    case 2:
                        Intent intent1 = new Intent(Settings.this, CourseSettings.class);
                        startActivity(intent1);
                        break;
                    case 3:
                        Intent intent2 = new Intent(Settings.this, GradeSettings.class);
                        startActivity(intent2);
                        break;
                    case 4:
                        Intent intent3 = new Intent(Settings.this, AssessmentSetting.class);
                        startActivity(intent3);
                        break;
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }
}
