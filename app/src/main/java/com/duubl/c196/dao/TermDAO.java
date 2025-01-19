package com.duubl.c196.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.duubl.c196.entities.Course;
import com.duubl.c196.entities.Term;

import java.util.List;

@Dao
public interface TermDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Term term);

    @Update
    void update(Term term);

    @Delete
    void delete(Term term);

    @Query("SELECT * FROM terms ORDER BY term_id ASC")
    List<Term> getAllTerms();

    @Query("SELECT * FROM courses WHERE termID=:course_term ORDER BY termID ASC")
    List<Course> getTermCourses(int course_term);
}
