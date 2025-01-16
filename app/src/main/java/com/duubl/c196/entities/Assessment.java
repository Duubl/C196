package com.duubl.c196.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.ArrayList;

@Entity(tableName = "assessments")
public class Assessment {
    @PrimaryKey(autoGenerate = true)
    private int assessment_id;
    // Foreign key
    private int course_id;
    private String assessment_name;
    private LocalDate start_date;
    private LocalDate end_date;
    private AssessmentType assessment_type;

    public int getAssessment_id() {
        return assessment_id;
    }

    public int getCourse_id() {
        return course_id;
    }

    public String getAssessment_name() {
        return assessment_name;
    }

    public AssessmentType getAssessment_type() {
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

    public void setAssessment_type(AssessmentType assessment_type) {
        this.assessment_type = assessment_type;
    }

    public LocalDate getStart_date() {
        return start_date;
    }

    public void setStart_date(LocalDate start_date) {
        this.start_date = start_date;
    }

    public LocalDate getEnd_date() {
        return end_date;
    }

    public void setEnd_date(LocalDate end_date) {
        this.end_date = end_date;
    }
}
