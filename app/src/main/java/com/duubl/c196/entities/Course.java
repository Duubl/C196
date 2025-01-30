package com.duubl.c196.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.time.LocalDate;
import java.util.Objects;

@Entity(tableName = "courses")
public class Course {

    @PrimaryKey(autoGenerate = true)
    private int courseID;
    // Foreign key
    private int termID;
    private String courseName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status;

    public Course(int courseID, int termID, String courseName, LocalDate startDate, LocalDate endDate, Status status) {
        this.courseID = courseID;
        this.termID = termID;
        this.courseName = courseName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public int getTermID() {
        return termID;
    }

    public void setTermID(int termID) {
        this.termID = termID;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Tests for equivalence using the course ID.
     * @param obj the object being tested
     * @return true when both IDs are the same.
     */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Course course = (Course) obj;
        return Objects.equals(courseID, course.courseID);
    }

    /**
     * @return the objects hash code, which is set to the ID.
     */

    @Override
    public int hashCode() {
        return Objects.hash(courseID);
    }
}
