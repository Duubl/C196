package com.duubl.c196.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.duubl.c196.entities.Course;
import com.duubl.c196.entities.Instructor;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface InstructorDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Instructor instructor);

    @Update
    void update(Instructor instructor);

    @Delete
    void delete(Instructor instructor);

    @Query("SELECT * FROM instructors ORDER BY instructorID ASC")
    List<Instructor> getAllInstructors();

    @Query("SELECT * FROM instructors WHERE courseID=:course_id ORDER BY instructorID ASC")
    List<Instructor> getCourseInstructors(int course_id);

    @Query("SELECT * FROM courses WHERE courseID=:course_id ORDER BY courseID ASC")
    List<Course> getAllInstructorCourses(int course_id);
}
