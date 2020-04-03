package com.iyangcong.reader.bean;

import java.io.Serializable;
import java.util.Comparator;

public class Semester implements Serializable,Comparator<Semester>{
    public int semesterId;
    public String semesterName;

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        semesterId = semesterId;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }



    @Override
    public int compare(Semester s1, Semester s2) {
        return s1.getSemesterId()-s2.getSemesterId();
    }

    @Override
    public String toString() {
        return "Semester{" +
                "semesterId=" + semesterId +
                ", semesterName='" + semesterName + '\'' +
                '}';
    }
}
