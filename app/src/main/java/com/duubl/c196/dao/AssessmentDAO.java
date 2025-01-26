package com.duubl.c196.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.duubl.c196.entities.Assessment;
import com.duubl.c196.entities.Course;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface AssessmentDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Assessment assessment);

    @Update
    void update(Assessment assessment);

    @Delete
    void delete(Assessment assessment);

    @Query("SELECT * FROM assessments ORDER BY assessmentID ASC")
    List<Assessment> getAllAssessments();

    @Query("SELECT * FROM assessments WHERE courseID=:course_id ORDER BY assessmentID ASC")
    List<Assessment> getCourseAssessments(int course_id);

    @Query("SELECT * FROM courses WHERE courseID=:course_id ORDER BY courseID ASC")
    List<Course> getAllAssessmentCourses(int course_id);
}
