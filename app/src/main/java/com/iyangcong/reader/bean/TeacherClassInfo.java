package com.iyangcong.reader.bean;

import java.io.Serializable;

public class TeacherClassInfo implements Serializable {
    public String grade;
    public int class_group_id;
    public int class_groups_dynamic_num;
    public int class_id;
    public String class_grade;
    public String class_name;
    public int avg_reading_words;
    public int no_active_student_count;
    public int student_count;
    public int class_dynamic_num;


    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public int getClass_group_id() {
        return class_group_id;
    }

    public void setClass_group_id(int class_group_id) {
        this.class_group_id = class_group_id;
    }

    public int getClass_groups_dynamic_num() {
        return class_groups_dynamic_num;
    }

    public void setClass_groups_dynamic_num(int class_groups_dynamic_num) {
        this.class_groups_dynamic_num = class_groups_dynamic_num;
    }

    public int getClass_id() {
        return class_id;
    }

    public void setClass_id(int class_id) {
        this.class_id = class_id;
    }

    public String getClass_grade() {
        return class_grade;
    }

    public void setClass_grade(String class_grade) {
        this.class_grade = class_grade;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public int getAvg_reading_words() {
        return avg_reading_words;
    }

    public void setAvg_reading_words(int avg_reading_words) {
        this.avg_reading_words = avg_reading_words;
    }

    public int getNo_active_student_count() {
        return no_active_student_count;
    }

    public void setNo_active_student_count(int no_active_student_count) {
        this.no_active_student_count = no_active_student_count;
    }

    public int getStudent_count() {
        return student_count;
    }

    public void setStudent_count(int student_count) {
        this.student_count = student_count;
    }

    public int getClass_dynamic_num() {
        return class_dynamic_num;
    }

    public void setClass_dynamic_num(int class_dynamic_num) {
        this.class_dynamic_num = class_dynamic_num;
    }
}
