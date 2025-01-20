package com.duubl.c196.database;

import android.app.Application;
import android.util.Log;

import com.duubl.c196.dao.AssessmentDAO;
import com.duubl.c196.dao.CourseDAO;
import com.duubl.c196.dao.InstructorDAO;
import com.duubl.c196.dao.TermDAO;
import com.duubl.c196.entities.Assessment;
import com.duubl.c196.entities.Course;
import com.duubl.c196.entities.Instructor;
import com.duubl.c196.entities.Term;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Repository {
    private TermDAO term_dao;
    private CourseDAO course_dao;
    private InstructorDAO instructor_dao;
    private AssessmentDAO assessment_dao;

    private List<Term> all_terms;
    private List<Course> all_courses;
    private List<Instructor> all_instructors;
    private List<Assessment> all_assessments;

    private static int NUMBER_OF_THREADS = 4;
    static final ExecutorService database_executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public Repository(Application application) {
        DatabaseBuilder db = DatabaseBuilder.getDatabase(application);
        term_dao = db.term_dao();
        course_dao = db.course_dao();
        instructor_dao = db.instructor_dao();
        assessment_dao = db.assessment_dao();
    }

    // Terms

    /**
     * Pulls all the terms from the database
     * @return all_terms, a list of all the terms
     * @throws InterruptedException
     */

    public List<Term> getAllTerms() throws InterruptedException {
        database_executor.execute(()-> {
            all_terms = term_dao.getAllTerms();
        });

        Thread.sleep(1000);
        return all_terms;
    }

    /**
     * Inserts a new term into the database
     * @param term the term to be inserted
     * @throws InterruptedException
     */

    public void insert(Term term) throws InterruptedException {
        database_executor.execute(() -> {
            term_dao.insert(term);
        });
            Thread.sleep(1000);
    }

    /**
     * Deletes the given term
     * @param term the term to be deleted
     * @throws InterruptedException
     */

    public void delete(Term term) throws InterruptedException {
        database_executor.execute(() -> {
            term_dao.delete(term);
        });
            Thread.sleep(1000);
    }

    public void delete(int term_id) throws InterruptedException {
        for (Term term : all_terms) {
            if (term.getTerm_id() == term_id) {
                term_dao.delete(term);
            }
        }
    }

    /**
     * Updates the given term
     * @param term the term to be updated
     * @throws InterruptedException
     */

    public void update(Term term) throws InterruptedException {
        database_executor.execute(() -> {
            term_dao.update(term);
        });
            Thread.sleep(1000);
    }

    // Courses

    /**
     * Inserts a new course into the database
     * @param course the course to be inserted
     * @throws InterruptedException
     */

    public void insert(Course course) throws InterruptedException {
        database_executor.execute(() -> {
            course_dao.insert(course);
        });
        Thread.sleep(1000);
    }

    /**
     * Pulls all the courses from the database that belong to the given term
     * @return all_courses, a list of all the courses assigned to the given term
     * @param term the term to find the courses for
     * @throws InterruptedException
     */

    public List<Course> getAllTermCourses(Term term) throws InterruptedException {
        database_executor.execute(()-> {
            all_courses = term_dao.getTermCourses(term.getTerm_id());
        });

        Thread.sleep(1000);
        return all_courses;
    }

    public List<Course> getAllInstructorCourses(Instructor instructor) throws InterruptedException {
        database_executor.execute(() -> {
            all_courses = instructor_dao.getAllInstructorCourses(instructor.getCourseID());
        });

        Thread.sleep(1000);
        return all_courses;
    }

    public List<Course> getAllAssessmentCourses(Assessment assessment) throws InterruptedException {
        database_executor.execute(() -> {
            all_courses = assessment_dao.getAllAssessmentCourses(assessment.getAssessmentID());
            if (!all_courses.isEmpty()) {
                Log.d("Repository", "Got a course for assessment: " + assessment.getName());
            } else {
                Log.e("Repository", "Pulled no assessments for " + assessment.getName());
            }
        });
        return all_courses;
    }

    /**
     * Pulls all the courses from the database
     * @return all_courses, a list of all the courses
     * @throws InterruptedException
     */

    public List<Course> getAllCourses() throws InterruptedException {
        database_executor.execute(()-> {
            all_courses = course_dao.getAllCourses();
        });

        Thread.sleep(1000);
        return all_courses;
    }

    /**
     * Deletes the given course
     * @param course the course to be deleted
     * @throws InterruptedException
     */

    public void delete(Course course) throws InterruptedException {
        database_executor.execute(() -> {
            course_dao.delete(course);
        });
        Thread.sleep(1000);
    }

    /**
     * Updates the given course
     * @param course the course to be updated
     * @throws InterruptedException
     */

    public void update(Course course) throws InterruptedException {
        database_executor.execute(() -> {
            course_dao.update(course);
        });
        Thread.sleep(1000);
    }

    // Instructors

    /**
     * Inserts a new instructor into the database
     * @param instructor the instructor to be inserted
     * @throws InterruptedException
     */

    public void insert(Instructor instructor) throws InterruptedException {
        database_executor.execute(() -> {
            instructor_dao.insert(instructor);
        });
        Thread.sleep(1000);
    }

    /**
     * Pulls all the course instructors from the database
     * @return all_instructors, a list of all the course instructors
     * @param course the course to find the instructors for
     * @throws InterruptedException
     */

    public List<Instructor> getAllCourseInstructors(Course course) throws InterruptedException {
        database_executor.execute(()-> {
            all_instructors = course_dao.getCourseInstructors(course.getCourseID());
        });

        Thread.sleep(1000);
        return all_instructors;
    }

    /**
     * Pulls all the instructors from the database
     * @return all_instructors, a list of all the instructors
     * @throws InterruptedException
     */

    public List<Instructor> getAllInstructors() throws InterruptedException {
        database_executor.execute(()-> {
            all_instructors = instructor_dao.getAllInstructors();
        });

        Thread.sleep(1000);
        return all_instructors;
    }

    /**
     * Deletes the given instructor
     * @param instructor the instructor to be deleted
     * @throws InterruptedException
     */

    public void delete(Instructor instructor) throws InterruptedException {
        database_executor.execute(() -> {
            instructor_dao.delete(instructor);
        });
        Thread.sleep(1000);
    }

    /**
     * Updates the given instructor
     * @param instructor the instructor to be updated
     * @throws InterruptedException
     */

    public void update(Instructor instructor) throws InterruptedException {
        database_executor.execute(() -> {
            instructor_dao.update(instructor);
        });
        Thread.sleep(1000);
    }

    // Assessments

    /**
     * Inserts a new assessment into the database
     * @param assessment the assessment to be inserted
     * @throws InterruptedException
     */

    public void insert(Assessment assessment) throws InterruptedException {
        database_executor.execute(() -> {
            assessment_dao.insert(assessment);
        });
        Thread.sleep(1000);
    }

    /**
     * Pulls all the assessments from the database
     * @return all_assessments, a list of all the assessments
     * @throws InterruptedException
     */

    public List<Assessment> getAllAssessments() throws InterruptedException {
        database_executor.execute(()-> {
            all_assessments = assessment_dao.getAllAssessments();
        });

        Thread.sleep(1000);
        return all_assessments;
    }

    /**
     * Pulls all the course assessments from the database
     * @return all_assessments, a list of all the assessments assigned to the given course
     * @param course the course to find the assessments for
     * @throws InterruptedException
     */

    public List<Assessment> getAllCourseAssessments(Course course) throws InterruptedException {
        database_executor.execute(()-> {
            all_assessments = course_dao.getCourseAssessments(course.getCourseID());
        });

        Thread.sleep(1000);
        return all_assessments;
    }

    /**
     * Deletes the given assessment
     * @param assessment the assessment to be deleted
     * @throws InterruptedException
     */

    public void delete(Assessment assessment) throws InterruptedException {
        database_executor.execute(() -> {
            assessment_dao.delete(assessment);
        });
        Thread.sleep(1000);
    }

    /**
     * Updates the given assessment
     * @param assessment the assessment to be updated
     * @throws InterruptedException
     */

    public void update(Assessment assessment) throws InterruptedException {
        database_executor.execute(() -> {
            assessment_dao.update(assessment);
        });
        Thread.sleep(1000);
    }
}
