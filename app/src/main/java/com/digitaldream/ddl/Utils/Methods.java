package com.digitaldream.ddl.Utils;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;

public class Methods {


    public static String capitaliseFirstLetter(String sS) {

        String[] strings = sS.toLowerCase().split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        for (String letter : strings) {
            try {
                String words =
                        letter.substring(0, 1).toUpperCase() + letter.substring(1).toLowerCase();
                stringBuilder.append(words).append(" ");
            } catch (Exception sE) {
                sE.printStackTrace();
            }

        }
        return stringBuilder.toString();
    }

    public static String abbreviate(String sS) {

        String[] strings = sS.toLowerCase().split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        for (String letter : strings) {
            try {
                String words =
                        letter.substring(0, 1).toUpperCase();
                stringBuilder.append(words);
            } catch (Exception sE) {
                sE.printStackTrace();
            }

        }
        return stringBuilder.toString();
    }

    public static int setColor() {
        Random random = new Random();

        return Color.argb(255, random.nextInt(256), random.nextInt(256),
                random.nextInt(256));
    }
/*
    public static void startCountDown(ProgressBar sProgressBar, int sI,
                                      TextView sTextView){

         CountDownTimer timer = new CountDownTimer(5 * 1000, 1) {
            @Override
            public void onTick(long sL) {

                sI = sI + 1;
                if (sI < 51) {
                    sProgressBar.setProgress(sI);
                    sTextView.setText(sI + "%");
                    sProgressBar.setMax(100);
                }

            }

            @Override
            public void onFinish() {

            }
        };

    }*/
}
