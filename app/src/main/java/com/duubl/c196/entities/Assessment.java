package com.duubl.c196.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "assessments")
public class Assessment {
    @PrimaryKey(autoGenerate = true)
    private int assessment_id;
    // Foreign key
    private int course_id;
    private String assessment_name;
    private int assessment_type;

    public int getAssessment_id() {
        return assessment_id;
    }

    public int getCourse_id() {
        return course_id;
    }

    public String getAssessment_name() {
        return assessment_name;
    }

    public int getAssessment_type() {
        return assessment_type;
    }

    public void setAssessment_id(int assessment_id) {
        this.assessment_id = assessment_id;
    }

    public void setCourse_id(int course_id) {
        this.course_id = course_id;
    }

    public void setAssessment_name(String assessment_name) {
        this.assessment_name = assessment_name;
    }

    public void setAssessment_type(int assessment_type) {
        this.assessment_type = assessment_type;
    }
}
