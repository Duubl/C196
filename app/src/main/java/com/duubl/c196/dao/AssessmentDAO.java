package com.duubl.c196.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.duubl.c196.entities.Assessment;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface AssessmentDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Assessment assessment);

    @Update
    void update(Assessment assessment);

    @Delete
    void delete(Assessment assessment);

    @Query("SELECT * FROM assessments ORDER BY assessment_id ASC")
    List<Assessment> getAllAssessments();

    @Query("SELECT * FROM assessments WHERE assessment_id=:course_id ORDER BY assessment_id ASC")
    List<Assessment> getCourseAssessments(int course_id);
}
