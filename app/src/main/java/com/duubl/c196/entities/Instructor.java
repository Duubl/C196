package com.duubl.c196.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "instructors")
public class Instructor {
    @PrimaryKey(autoGenerate = true)
    private int instructor_id;
    // Foreign key
    private int course_id;
    private String instructor_name;
    private String instructor_phone;
    private String instructor_email;

    public Instructor(int instructor_id, int course_id, String instructor_name, String instructor_phone, String instructor_email) {
        this.instructor_id = instructor_id;
        this.course_id = course_id;
        this.instructor_name = instructor_name;
        this.instructor_phone = instructor_phone;
        this.instructor_email = instructor_email;
    }

    public int getInstructor_id() {
        return instructor_id;
    }

    public int getCourse_id() {
        return course_id;
    }

    public String getInstructor_name() {
        return instructor_name;
    }

    public String getInstructor_phone() {
        return instructor_phone;
    }

    public String getInstructor_email() {
        return instructor_email;
    }

    public void setInstructor_id(int instructor_id) {
        this.instructor_id = instructor_id;
    }

    public void setCourse_id(int course_id) {
        this.course_id = course_id;
    }

    public void setInstructor_name(String instructor_name) {
        this.instructor_name = instructor_name;
    }

    public void setInstructor_phone(String instructor_phone) {
        this.instructor_phone = instructor_phone;
    }

    public void setInstructor_email(String instructor_email) {
        this.instructor_email = instructor_email;
    }
}
