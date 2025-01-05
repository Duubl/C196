package com.duubl.c196.entities;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.time.LocalDate;
import java.util.ArrayList;

@Entity(tableName = "courses")
public class Course {

    @PrimaryKey(autoGenerate = true)
    private int course_id;
    // Foreign key
    private int term_id;
    private String course_name;
    private LocalDate start_date;
    private LocalDate end_date;
    private Status status;

    public Course(int course_id, int term_id, String course_name, LocalDate start_date, LocalDate end_date, Status status) {
       this.course_id = course_id;
       this.term_id = term_id;
       this.course_name = course_name;
       this.start_date = start_date;
       this.end_date = end_date;
       this.status = status;
    }

    /**
     * Gets the course name
     * @return the course name
     */

    public String getCourseName() {
        return this.course_name;
    }

    /**
     * Gets the course ID
     * @return the course ID
     */

    public int getCourse_id() {
        return this.course_id;
    }

    public int getTerm_id() {
        return this.term_id;
    }

    public String getCourse_name() {
        return this.course_name;
    }

    public LocalDate getStart_date() {
        return start_date;
    }

    public LocalDate getEnd_date() {
        return end_date;
    }

    public Status getStatus() {
        return status;
    }
}
