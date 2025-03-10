package com.duubl.c196.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

@Entity(tableName = "assessments")
public class Assessment {
    @PrimaryKey(autoGenerate = true)
    private int assessmentID;
    // Foreign key
    private int courseID;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private AssessmentType type;

    public Assessment(int assessmentID, String name, LocalDate startDate, LocalDate endDate, AssessmentType type) {
        this.assessmentID = assessmentID;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
    }

    public int getAssessmentID() {
        return assessmentID;
    }

    public void setAssessmentID(int assessmentID) {
        this.assessmentID = assessmentID;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public AssessmentType getType() {
        return type;
    }

    public void setType(AssessmentType type) {
        this.type = type;
    }

    /**
     * Tests for equivalence using the assessment ID.
     * @param obj the object being tested
     * @return true when both IDs are the same.
     */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Assessment assessment = (Assessment) obj;
        return Objects.equals(assessmentID, assessment.assessmentID);
    }

    /**
     * @return the objects hash code, which is set to the ID.
     */

    @Override
    public int hashCode() {
        return Objects.hash(assessmentID);
    }
}
