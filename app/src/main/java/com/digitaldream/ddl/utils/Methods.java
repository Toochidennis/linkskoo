package com.digitaldream.ddl.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;

public class Methods {
    static int counter = 0;


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

/*    public static CountDownTimer startCountDown(ProgressBar sProgressBar,
                                                int sI,
                                      TextView sTextView) {

        return new CountDownTimer(5 * 1000, 1) {
            @Override
            public void onTick(long sL) {

                counter += 1;
                if (counter < sI + 1) {
                    sProgressBar.setProgress(counter);
                    sTextView.setText(counter + "%");
                    sProgressBar.setMax(100);
                }

            }

            @Override
            public void onFinish() {

            }
        };

    }*/


    public static void animateObject(ProgressBar sProgressBar, TextView sTextView,
                              int sI) {
        ObjectAnimator.ofInt(sProgressBar, "progress", sI)
                .setDuration(1000)
                .start();
        ValueAnimator animator = ValueAnimator.ofInt(0, sI);
        animator.setDuration(1000);
        animator.addUpdateListener(animation -> sTextView.setText(animator.getAnimatedValue() + "%"));
        animator.start();

    }
}
