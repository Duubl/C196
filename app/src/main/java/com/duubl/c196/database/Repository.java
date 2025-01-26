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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
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
     * @throws ExecutionException
     */

    public List<Term> getAllTerms() throws InterruptedException, ExecutionException {
        Callable<List<Term>> task = () -> all_terms = term_dao.getAllTerms();
        FutureTask<List<Term>> future = new FutureTask<>(task);
        database_executor.execute(future);
        return future.get();
    }

    /**
     * Inserts a new term into the database
     * @param term the term to be inserted
     * @throws InterruptedException
     * @throws ExecutionException
     */

    public long insert(Term term) throws InterruptedException, ExecutionException {
        Callable<Long> task = () -> term_dao.insert(term);
        FutureTask<Long> future = new FutureTask<>(task);
        database_executor.execute(future);
        return future.get();
    }

    /**
     * Deletes the given term
     * @param term the term to be deleted
     * @throws InterruptedException
     * @throws ExecutionException
     */

    public void delete(Term term) throws InterruptedException, ExecutionException {
        Callable<Void> task = () -> {
            term_dao.delete(term);
            return null;
        };
        FutureTask<Void> future = new FutureTask<>(task);
        database_executor.execute(future);
        future.get();
    }

    /**
     * Deletes the term by id
     * @param term_id the id of the term to be deleted
     * @throws InterruptedException
     * @throws ExecutionException
     */

    public void delete(int term_id) throws InterruptedException, ExecutionException {
        Callable<Void> task = () -> {
            for (Term term : all_terms) {
                if (term.getTermID() == term_id) {
                    term_dao.delete(term);
                }
            }
            return null;
        };
        FutureTask<Void> future = new FutureTask<>(task);
        database_executor.execute(future);
        future.get();
    }

    /**
     * Updates the given term
     * @param term the term to be updated
     * @throws InterruptedException
     * @throws ExecutionException
     */

    public void update(Term term) throws InterruptedException, ExecutionException {
        Callable<Void> task = () -> {
            term_dao.update(term);
            return null;
        };
        FutureTask<Void> future = new FutureTask<>(task);
        database_executor.execute(future);
        future.get();
    }

    // Courses

    /**
     * Inserts a new course into the database
     * @param course the course to be inserted
     * @throws InterruptedException
     * @throws ExecutionException
     */

    public void insert(Course course) throws InterruptedException, ExecutionException {
        Callable<Void> task = () -> {
            course_dao.insert(course);
            return null;
        };
        FutureTask<Void> future = new FutureTask<>(task);
        database_executor.execute(future);
        future.get();
    }

    /**
     * Pulls all the courses from the database that belong to the given term
     * @return all_courses, a list of all the courses assigned to the given term
     * @param term the term to find the courses for
     * @throws InterruptedException
     * @throws ExecutionException
     */

    public List<Course> getAllTermCourses(Term term) throws InterruptedException, ExecutionException {
        Callable<List<Course>> task = () -> all_courses = term_dao.getTermCourses(term.getTermID());
        FutureTask<List<Course>> future = new FutureTask<>(task);
        database_executor.execute(future);
        return future.get();
    }

    /**
     * Gets the course the instructor is assigned to.
     * @param instructor the assessment to check.
     * @return all_courses, being an array of all courses the instructor is assigned to. Returns an empty arraylist if nothing is found.
     */

    public List<Course> getAllInstructorCourses(Instructor instructor) {
        Callable<List<Course>> task = () -> instructor_dao.getAllInstructorCourses(instructor.getInstructorID());
        FutureTask<List<Course>> future = new FutureTask<>(task);
        database_executor.execute(future);
        try {
            return future.get();
        } catch (Exception e) {
            Log.e("Repository", "Error fetching instructor courses: ", e);
            return new ArrayList<>();
        }
    }

    /**
     * Gets the course the assessment is assigned to.
     * @param assessment the assessment to check.
     * @return all_courses, being an array of all courses the assessment is assigned to. Returns an empty arraylist if nothing is found.
     * @throws InterruptedException
     */

    public List<Course> getAllAssessmentCourses(Assessment assessment) throws InterruptedException {
        Callable<List<Course>> task = () -> all_courses = assessment_dao.getAllAssessmentCourses(assessment.getAssessmentID());
        FutureTask<List<Course>> future = new FutureTask<>(task);
        database_executor.execute(future);
        try {
            return future.get();
        } catch (Exception e) {
            Log.e("Repository", "Error fetching assessment courses: ", e);
            return new ArrayList<>();
        }
    }

    /**
     * Pulls all the courses from the database
     * @return all_courses, a list of all the courses
     * @throws InterruptedException
     * @throws ExecutionException
     */

    public List<Course> getAllCourses() throws InterruptedException, ExecutionException {
        Callable<List<Course>> task = () -> all_courses = course_dao.getAllCourses();
        FutureTask<List<Course>> future = new FutureTask<>(task);
        database_executor.execute(future);
        return future.get();
    }

    /**
     * Deletes the given course
     * @param course the course to be deleted
     * @throws InterruptedException
     * @throws ExecutionException
     */

    public void delete(Course course) throws InterruptedException, ExecutionException {
        Callable<Void> task = () -> {
            course_dao.delete(course);
            return null;
        };
        FutureTask<Void> future = new FutureTask<>(task);
        database_executor.execute(future);
        future.get();
    }

    /**
     * Updates the given course
     * @param course the course to be updated
     * @throws InterruptedException
     * @throws ExecutionException
     */

    public void update(Course course) throws InterruptedException, ExecutionException {
        Callable<Void> task = () -> {
            course_dao.update(course);
            return null;
        };
        FutureTask<Void> future = new FutureTask<>(task);
        database_executor.execute(future);
        future.get();
    }

    // Instructors

    /**
     * Inserts a new instructor into the database
     * @param instructor the instructor to be inserted
     * @throws InterruptedException
     * @throws ExecutionException
     */

    public void insert(Instructor instructor) throws InterruptedException, ExecutionException {
        Callable<Void> task = () -> {
            instructor_dao.insert(instructor);
            return null;
        };
        FutureTask<Void> future = new FutureTask<>(task);
        database_executor.execute(future);
        future.get();
    }

    /**
     * Pulls all the course instructors from the database
     * @return all_instructors, a list of all the course instructors
     * @param course the course to find the instructors for
     * @throws InterruptedException
     * @throws ExecutionException
     */

    public List<Instructor> getAllCourseInstructors(Course course) throws InterruptedException, ExecutionException {
        Callable<List<Instructor>> task = () -> all_instructors = course_dao.getCourseInstructors(course.getCourseID());
        FutureTask<List<Instructor>> future = new FutureTask<>(task);
        database_executor.execute(future);
        return future.get();
    }

    /**
     * Pulls all the instructors from the database
     * @return all_instructors, a list of all the instructors
     * @throws InterruptedException
     * @throws ExecutionException
     */

    public List<Instructor> getAllInstructors() throws InterruptedException, ExecutionException {
        Callable<List<Instructor>> task = () -> all_instructors = instructor_dao.getAllInstructors();
        FutureTask<List<Instructor>> future = new FutureTask<>(task);
        database_executor.execute(future);
        return future.get();
    }

    /**
     * Deletes the given instructor
     * @param instructor the instructor to be deleted
     * @throws InterruptedException
     * @throws ExecutionException
     */

    public void delete(Instructor instructor) throws InterruptedException, ExecutionException {
        Callable<Void> task = () -> {
            instructor_dao.delete(instructor);
            return null;
        };
        FutureTask<Void> future = new FutureTask<>(task);
        database_executor.execute(future);
        future.get();
    }

    /**
     * Updates the given instructor
     * @param instructor the instructor to be updated
     * @throws InterruptedException
     * @throws ExecutionException
     */

    public void update(Instructor instructor) throws InterruptedException, ExecutionException {
        Callable<Void> task = () -> {
            instructor_dao.update(instructor);
            return null;
        };
        FutureTask<Void> future = new FutureTask<>(task);
        database_executor.execute(future);
        future.get();
    }

    // Assessments

    /**
     * Inserts a new assessment into the database
     * @param assessment the assessment to be inserted
     * @throws InterruptedException
     * @throws ExecutionException
     */

    public void insert(Assessment assessment) throws InterruptedException, ExecutionException {
        Callable<Void> task = () -> {
            assessment_dao.insert(assessment);
            return null;
        };
        FutureTask<Void> future = new FutureTask<>(task);
        database_executor.execute(future);
        future.get();
    }

    /**
     * Pulls all the assessments from the database
     * @return all_assessments, a list of all the assessments
     * @throws InterruptedException
     * @throws ExecutionException
     */

    public List<Assessment> getAllAssessments() throws InterruptedException, ExecutionException {
        Callable<List<Assessment>> task = () -> all_assessments = assessment_dao.getAllAssessments();
        FutureTask<List<Assessment>> future = new FutureTask<>(task);
        database_executor.execute(future);
        return future.get();
    }

    /**
     * Pulls all the course assessments from the database
     * @return all_assessments, a list of all the assessments assigned to the given course
     * @param course the course to find the assessments for
     * @throws InterruptedException
     * @throws ExecutionException
     */

    public List<Assessment> getAllCourseAssessments(Course course) throws InterruptedException, ExecutionException {
        Callable<List<Assessment>> task = () -> all_assessments = course_dao.getCourseAssessments(course.getCourseID());
        FutureTask<List<Assessment>> future = new FutureTask<>(task);
        database_executor.execute(future);
        return future.get();
    }

    /**
     * Deletes the given assessment
     * @param assessment the assessment to be deleted
     * @throws InterruptedException
     * @throws ExecutionException
     */

    public void delete(Assessment assessment) throws InterruptedException, ExecutionException {
        Callable<Void> task = () -> {
            assessment_dao.delete(assessment);
            return null;
        };
        FutureTask<Void> future = new FutureTask<>(task);
        database_executor.execute(future);
        future.get();
    }

    /**
     * Updates the given assessment
     * @param assessment the assessment to be updated
     * @throws InterruptedException
     * @throws ExecutionException
     */

    public void update(Assessment assessment) throws InterruptedException, ExecutionException {
        Callable<Void> task = () -> {
            assessment_dao.update(assessment);
            return null;
        };
        FutureTask<Void> future = new FutureTask<>(task);
        database_executor.execute(future);
        future.get();
    }
}
