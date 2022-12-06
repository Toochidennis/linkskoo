package com.digitaldream.ddl.Models;

import static com.digitaldream.ddl.BuildConfig.DEBUG;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import org.w3c.dom.Text;

public class MenuItemColor {
    public static void setMenuTextColor(final Context sContext,
                                        final Toolbar sToolbar,
                                        final int sMenuResId, final int sColor){
        sToolbar.post(new Runnable() {
            @Override
            public void run() {
                View view = sToolbar.findViewById(sMenuResId);
                if (view instanceof TextView){
                    if (DEBUG){
                        Log.i("Error", "view textview");
                    }
                    TextView textView = (TextView) (view);
                    textView.setTextColor(ContextCompat.getColor(sContext, sColor));

                }
            }
        });

    }
}
