package com.digitaldream.ddl.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.digitaldream.ddl.R;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class SubjectResultUtil extends AppCompatActivity {
    private WebView mWebview;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_subject_result);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("View Result");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);
        Intent i = getIntent();

        String courseId = i.getStringExtra("courseId");
        String classId = i.getStringExtra("class_id");
        String status = i.getStringExtra("status");



        mWebview = findViewById(R.id.webview_view_result_subject);
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();


        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        String db = sharedPreferences.getString("db","");
        if(status.equals("view")) {
            mWebview.loadUrl(Login.urlBase+"/adminViewResult.php?class=" + classId + "&course=" + courseId + "&_db=" + db+"&year="+ ClassResultDownload.schoolYear+"&term="+ClassResultDownload.term);
        }else if(status.equals("edit")) {
            mWebview.loadUrl(Login.urlBase+"/adminAddResult.php?class=" + classId + "&course=" + courseId + "&_db=" + db+"&year="+ClassResultDownload.schoolYear+"&term="+ClassResultDownload.term);
        }


        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.setWebViewClient(new WebViewClient());
        mWebview.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(newProgress==100){
                    dialog1.dismiss();
                    mWebview.setVisibility(View.VISIBLE);
                }
            }


            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
        if (mWebview.canGoBack()){
            mWebview.goBack();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
