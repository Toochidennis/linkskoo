package com.digitaldream.ddl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.digitaldream.ddl.Activities.Login;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class ViewResultWebView extends AppCompatActivity {
    private WebView mWebview;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_result_web_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("View Result");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);
        Intent i = getIntent();
        String studentId = i.getStringExtra("studentId");
        String level = i.getStringExtra("level");
        String term = i.getStringExtra("term");
        String year = i.getStringExtra("year") ;
        String classId = i.getStringExtra("classId");
        Log.i("response",studentId +" "+ level+ " "+term+" "+year);

        mWebview = findViewById(R.id.webview_view_result);
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();


        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        String db = sharedPreferences.getString("db","");
        Log.i("student_class",""+classId);
        mWebview.loadUrl(Login.urlBase+"/result.php?id="+studentId+"&class="+classId+"&term="+term+"&year="+year+"&_db="+db);

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
