package com.digitaldream.ddl.Models;

import android.widget.LinearLayout;

public class ClassTermResulsModel {
private String schoolSession;
private boolean firstTerm;
private boolean secondTerm;
private boolean thirdTerm;
private LinearLayout linearLayout;


    public ClassTermResulsModel(String schoolSession, boolean firstTerm, boolean secondTerm, boolean thirdTerm) {
        this.schoolSession = schoolSession;
        this.firstTerm = firstTerm;
        this.secondTerm = secondTerm;
        this.thirdTerm = thirdTerm;
    }

    public String getSchoolSession() {
        return schoolSession;
    }

    public void setSchoolSession(String schoolSession) {
        this.schoolSession = schoolSession;
    }

    public boolean getFirstTerm() {
        return firstTerm;
    }

    public void setFirstTerm(boolean firstTerm) {
        this.firstTerm = firstTerm;
    }

    public boolean getSecondTerm() {
        return secondTerm;
    }

    public void setSecondTerm(boolean secondTerm) {
        this.secondTerm = secondTerm;
    }

    public boolean getThirdTerm() {
        return thirdTerm;
    }

    public void setThirdTerm(boolean thirdTerm) {
        this.thirdTerm = thirdTerm;
    }
}
