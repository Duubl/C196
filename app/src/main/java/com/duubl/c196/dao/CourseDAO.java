package com.duubl.c196.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


import com.duubl.c196.entities.Assessment;
import com.duubl.c196.entities.Course;
import com.duubl.c196.entities.Instructor;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface CourseDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Course course);

    @Update
    void update(Course course);

    @Delete
    void delete(Course course);

    @Query("SELECT * FROM courses ORDER BY courseID ASC")
    List<Course> getAllCourses();

    @Query("SELECT * FROM assessments WHERE assessmentID=:course_id ORDER BY assessmentID ASC")
    List<Assessment> getCourseAssessments(int course_id);

    @Query("SELECT * FROM instructors WHERE instructorID=:course_id ORDER BY instructorID ASC")
    List<Instructor> getCourseInstructors(int course_id);
}
