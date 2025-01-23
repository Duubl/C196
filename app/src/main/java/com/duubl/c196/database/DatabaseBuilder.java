package com.duubl.c196.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.duubl.c196.dao.AssessmentDAO;
import com.duubl.c196.dao.CourseDAO;
import com.duubl.c196.dao.InstructorDAO;
import com.duubl.c196.dao.TermDAO;
import com.duubl.c196.entities.Assessment;
import com.duubl.c196.entities.Course;
import com.duubl.c196.entities.Instructor;
import com.duubl.c196.entities.Term;
import com.duubl.c196.util.Converter;

// When making changes to the database, change the version number
@Database(entities = {Term.class, Course.class, Instructor.class, Assessment.class}, version = 7, exportSchema = false)
@TypeConverters({Converter.class})
public abstract class DatabaseBuilder extends RoomDatabase {
    public abstract TermDAO term_dao();
    public abstract CourseDAO course_dao();
    public abstract AssessmentDAO assessment_dao();
    public abstract InstructorDAO instructor_dao();
    private static volatile DatabaseBuilder INSTANCE;

    static DatabaseBuilder getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DatabaseBuilder.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DatabaseBuilder.class, "ScheduleDatabase.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
