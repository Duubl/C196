package com.duubl.c196.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.duubl.c196.R;
import com.duubl.c196.database.Repository;
import com.duubl.c196.entities.Assessment;
import com.duubl.c196.entities.Instructor;
import com.duubl.c196.entities.Status;
import com.duubl.c196.entities.Course;
import com.duubl.c196.entities.Term;

import java.sql.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CoursesActivity extends AppCompatActivity {

    private Button newCourseButton;
    private LinearLayout courseLayout;
    private Repository repository;
    private List<Course> courses;

    // HashMap to store the expanded states of the cards. Helpful for changing orientation of the phone.
    private HashMap<Integer, Boolean> expandedStates = new HashMap<>();

    /**
     * Creates the activity. Adds the toolbar and populates the activity with the course cards.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Layout for the list of courses.
        courseLayout = findViewById(R.id.courses_list_layout);
        if (courseLayout == null) {
            Log.e("CoursesActivity", "courseLayout is null!");
            return;
        }

        // New course button
        newCourseButton = findViewById(R.id.new_course_button);
        newCourseButton.setOnClickListener(item -> {
            openInputDialog();
        });

        // Loads the courses into the activity on creation
        repository = new Repository(getApplication());
        try {
            courses = repository.getAllCourses();
        } catch (InterruptedException | ExecutionException e) {
            Log.e("CoursesActivity", "there are no courses!");
            throw new RuntimeException(e);
        }
        try {
            populateCourseCards();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Opens the input dialog for adding a new course.
     * Takes input from the user for the name, start & end dates, status and all course instructor information.
     */

    private void openInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Course");

        ArrayList<Instructor> assignedInstructors = new ArrayList<>();
        ArrayList<Assessment> assignedAssessments = new ArrayList<>();

        LinearLayout inputLayout = new LinearLayout(this);
        inputLayout.setOrientation(LinearLayout.VERTICAL);
        inputLayout.setPadding(16, 16, 16, 16);

        // Get the course name
        final EditText nameInput = new EditText(this);
        nameInput.setHint("Course Name");
        inputLayout.addView(nameInput);
        builder.setView(inputLayout);

        final Button startDateButton = new Button(this);
        startDateButton.setText("Course Start Date");
        inputLayout.addView(startDateButton);

        final LocalDate[] localStartDate = new LocalDate[1];
        startDateButton.setOnClickListener(v -> {
            LocalDate today = LocalDate.now();
            int year = today.getYear();
            int month = today.getMonthValue() - 1;
            int day = today.getDayOfMonth();
            new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                localStartDate[0] = LocalDate.of(selectedYear, selectedMonth + 1, selectedDayOfMonth);
                startDateButton.setText(localStartDate[0].toString());
            }, year, month, day).show();
        });

        final Button endDateButton = new Button(this);
        endDateButton.setText("Course End Date");
        inputLayout.addView(endDateButton);

        final LocalDate[] localEndDate = new LocalDate[1];
        endDateButton.setOnClickListener(v -> {
            LocalDate today = LocalDate.now();
            int year = today.getYear();
            int month = today.getMonthValue() - 1;
            int day = today.getDayOfMonth();
            new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                localEndDate[0] = LocalDate.of(selectedYear, selectedMonth + 1, selectedDayOfMonth);
                endDateButton.setText(localEndDate[0].toString());
            }, year, month, day).show();
        });

        final Button courseStatusButton = new Button(this);
        courseStatusButton.setText("Course Status");
        final Status[] status = new Status[1];
        inputLayout.addView(courseStatusButton);
        courseStatusButton.setOnClickListener(v -> {
            Status[] statuses = Status.values();
            String[] courseOptions = new String[statuses.length];
            for (int i = 0; i < statuses.length; i++) {
                courseOptions[i] = statuses[i].name();
            }

            new AlertDialog.Builder(this)
                    .setTitle("Select Course Status")
                    .setItems(courseOptions, (dialog, which) -> {
                        courseStatusButton.setText(courseOptions[which]);
                        status[0] = statuses[which];
                    })
                    .create()
                    .show();
        });

        final Button newCourseInstructorButton = new Button(this);
        newCourseInstructorButton.setText("Assign Instructor");
        inputLayout.addView(newCourseInstructorButton);
        newCourseInstructorButton.setOnClickListener(v -> {
            List<Instructor> instructors;
            try {
                instructors = repository.getAllInstructors();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            String[] instructorOptions = new String[instructors.size()];
            for (int i = 0; i < instructors.size(); i++) {
                instructorOptions[i] = instructors.get(i).getInstructorName();
            }

            // TODO: Prevent duplicate instructors from being assigned to the same course

            new AlertDialog.Builder(this)
                    .setTitle("Assign Instructor")
                    .setItems(instructorOptions, (dialog, which) -> {
                        newCourseInstructorButton.setText(instructorOptions[which]);
                        assignedInstructors.add(instructors.get(which));
                    })
                    .create()
                    .show();
        });

        final Button newCourseAssessmentButton = new Button(this);
        newCourseAssessmentButton.setText("Assign Assessment");
        inputLayout.addView(newCourseAssessmentButton);
        newCourseAssessmentButton.setOnClickListener(v -> {
            List<Assessment> assessments;
            try {
                assessments = repository.getAllAssessments();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            String[] assessmentOptions = new String[assessments.size()];
            for (int i = 0; i < assessments.size(); i++) {
                assessmentOptions[i] = assessments.get(i).getName();
            }

            new AlertDialog.Builder(this)
                    .setTitle("Assign Assessments")
                    .setItems(assessmentOptions, (dialog, which) -> {
                        newCourseAssessmentButton.setText(assessmentOptions[which]);
                        assignedAssessments.add(assessments.get(which));
                    })
                    .create()
                    .show();
        });

        final EditText noteInput = new EditText(this);
        noteInput.setHint("Note (Optional)");
        inputLayout.addView(noteInput);

        // Creates the button on submit if all fields contain information.
        // TODO: Add error checking and proper formatting checking
        builder.setPositiveButton("Add", (dialog, which) -> {
            String courseName = nameInput.getText().toString().trim();
            String note = noteInput.getText().toString().trim();
            if (courseName.isEmpty() || localStartDate[0] == null || localEndDate[0] == null || status[0] == null || assignedInstructors.isEmpty() || assignedAssessments.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                createNewCourse(courseName, localStartDate[0], localEndDate[0], status[0], assignedInstructors, assignedAssessments, note);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            try {
                populateCourseCards();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Opens the input dialog for modifying a course.
     * Takes input from the user for the name, start & end dates, status and all course instructor information.
     * @param course the course to be modified.
     */

    private void openInputDialog(Course course) throws ExecutionException, InterruptedException {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modify Course");

        List<Instructor> assignedInstructors = repository.getAllCourseInstructors(course);
        List<Assessment> assignedAssessments = repository.getAllCourseAssessments(course);

        LinearLayout inputLayout = new LinearLayout(this);
        inputLayout.setOrientation(LinearLayout.VERTICAL);
        inputLayout.setPadding(16, 16, 16, 16);

        // Get the course name
        final EditText nameInput = new EditText(this);
        nameInput.setHint("Course Name");
        nameInput.setText(course.getCourseName());
        inputLayout.addView(nameInput);

        final Button startDateButton = new Button(this);
        startDateButton.setText(course.getStartDate().toString());
        inputLayout.addView(startDateButton);

        final LocalDate[] localStartDate = new LocalDate[1];
        localStartDate[0] = course.getStartDate();
        startDateButton.setOnClickListener(v -> {
            LocalDate today = LocalDate.now();
            int year = today.getYear();
            int month = today.getMonthValue() - 1;
            int day = today.getDayOfMonth();
            new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                localStartDate[0] = LocalDate.of(selectedYear, selectedMonth + 1, selectedDayOfMonth);
                startDateButton.setText(localStartDate[0].toString());
            }, year, month, day).show();
        });

        final Button endDateButton = new Button(this);
        endDateButton.setText(course.getEndDate().toString());
        inputLayout.addView(endDateButton);

        final LocalDate[] localEndDate = new LocalDate[1];
        localEndDate[0] = course.getEndDate();
        endDateButton.setOnClickListener(v -> {
            LocalDate today = LocalDate.now();
            int year = today.getYear();
            int month = today.getMonthValue() - 1;
            int day = today.getDayOfMonth();
            new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                localEndDate[0] = LocalDate.of(selectedYear, selectedMonth + 1, selectedDayOfMonth);
                endDateButton.setText(localEndDate[0].toString());
            }, year, month, day).show();
        });

        final Button courseStatusButton = new Button(this);
        courseStatusButton.setText(course.getStatus().toString());
        final Status[] status = new Status[1];
        status[0] = course.getStatus();
        inputLayout.addView(courseStatusButton);
        courseStatusButton.setOnClickListener(v -> {
            Status[] statuses = Status.values();
            String[] courseOptions = new String[statuses.length];
            for (int i = 0; i < statuses.length; i++) {
                courseOptions[i] = statuses[i].name();
            }

            new AlertDialog.Builder(this)
                    .setTitle("Select Course Status")
                    .setItems(courseOptions, (dialog, which) -> {
                        courseStatusButton.setText(courseOptions[which]);
                        status[0] = statuses[which];
                    })
                    .create()
                    .show();
        });

        // Assigning multiple instructors to one course.
        final Button newCourseInstructorButton = new Button(this);

        StringBuilder newCourseInstructorButtonText = new StringBuilder("Assigned Instructors:\n");
        for (Instructor assignedInstructor: assignedInstructors) {
            newCourseInstructorButtonText.append(assignedInstructor.getInstructorName()).append(",\n");
        }
        newCourseInstructorButtonText.setLength(newCourseInstructorButtonText.length() - 2);
        newCourseInstructorButton.setText(newCourseInstructorButtonText.toString());

        inputLayout.addView(newCourseInstructorButton);

        newCourseInstructorButton.setOnClickListener(v -> {
            List<Instructor> instructors;
            try {
                instructors = repository.getAllInstructors();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            String[] instructorOptions = new String[instructors.size()];
            for (int i = 0; i < instructors.size(); i++) {
                instructorOptions[i] = instructors.get(i).getInstructorName();
            }

            // Pre-selects the already assigned instructors.
            boolean[] selectedItems = new boolean[instructorOptions.length];

            for (int i = 0; i < instructors.size(); i++) {
                if (assignedInstructors.contains(instructors.get(i))) {
                    selectedItems[i] = true;
                }
            }

            new AlertDialog.Builder(this)
                    .setTitle("Assign Instructors")
                    .setMultiChoiceItems(instructorOptions, selectedItems, (dialog, which, isChecked) -> {
                        selectedItems[which] = isChecked;
                    })
                    .setPositiveButton("Save", (dialog, which) -> {
                        assignedInstructors.clear();
                        for (int i = 0; i < selectedItems.length; i++) {
                            if (selectedItems[i]) {
                                if (!assignedInstructors.contains(instructors.get(i))) {
                                    assignedInstructors.add(instructors.get(i));
                                }
                            }
                        }

                        if (!assignedInstructors.isEmpty()) {
                            StringBuilder buttonText = new StringBuilder("Assigned Instructors:\n");
                            for (Instructor assignedInstructor : assignedInstructors) {
                                buttonText.append(assignedInstructor.getInstructorName()).append(",\n");
                            }
                            buttonText.setLength(buttonText.length() - 2);
                            newCourseInstructorButton.setText(buttonText.toString());
                        } else {
                            newCourseInstructorButton.setText("Assign Instructors");
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        });

        // Assigning multiple assessments to one course
        final Button newCourseAssessmentButton = new Button(this);

        StringBuilder newCourseAssessmentButtonText = new StringBuilder("Assigned Assessments:\n");
        for (Assessment assignedAssessment : assignedAssessments) {
            newCourseAssessmentButtonText.append(assignedAssessment.getName()).append(",\n");
        }
        newCourseAssessmentButtonText.setLength(newCourseAssessmentButtonText.length() - 2);
        newCourseAssessmentButton.setText(newCourseAssessmentButtonText.toString());

        inputLayout.addView(newCourseAssessmentButton);

        newCourseAssessmentButton.setOnClickListener(v -> {
            List<Assessment> assessments;
            try {
                assessments = repository.getAllAssessments();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            String[] assessmentOptions = new String[assessments.size()];
            for (int i = 0; i < assessments.size(); i++) {
                assessmentOptions[i] = assessments.get(i).getName();
            }

            // Pre-selects the already assigned assessments.
            boolean[] selectedItems = new boolean[assessmentOptions.length];

            for (int i = 0; i < assessments.size(); i++) {
                if (assignedAssessments.contains(assessments.get(i))) {
                    selectedItems[i] = true;
                }
            }

            new AlertDialog.Builder(this)
                    .setTitle("Assign Assessments")
                    .setMultiChoiceItems(assessmentOptions, selectedItems, (dialog, which, isChecked) -> {
                        selectedItems[which] = isChecked;
                    })
                    .setPositiveButton("Save", (dialog, which) -> {
                        assignedAssessments.clear();
                        for (int i = 0; i < selectedItems.length; i++) {
                            if (selectedItems[i]) {
                                if (!assignedAssessments.contains(assessments.get(i))) {
                                    assignedAssessments.add(assessments.get(i));
                                }
                            }
                        }

                        if (!assignedAssessments.isEmpty()) {
                            StringBuilder buttonText = new StringBuilder("Assigned Assessments:\n");
                            for (Assessment assignedAssessment : assignedAssessments) {
                                buttonText.append(assignedAssessment.getName()).append(",\n");
                            }
                            buttonText.setLength(buttonText.length() - 2);
                            newCourseAssessmentButton.setText(buttonText.toString());
                        } else {
                            newCourseAssessmentButton.setText("Assign Assessments");
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        });

        final EditText noteInput = new EditText(this);
        noteInput.setHint("Note (Optional)");
        noteInput.setText(course.getNote());
        inputLayout.addView(noteInput);

        builder.setView(inputLayout);

        // Creates the button on submit if all fields contain information.
        // TODO: Add error checking and proper formatting checking
        builder.setPositiveButton("Update Course", (dialog, which) -> {
            String courseName = nameInput.getText().toString().trim();
            String note = noteInput.getText().toString().trim();
            if (courseName.isEmpty() || localStartDate[0] == null || localEndDate[0] == null || status[0] == null || assignedInstructors.isEmpty() || assignedAssessments.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                modifyCourse(course, courseName, localStartDate[0], localEndDate[0], status[0], assignedAssessments, assignedInstructors, note);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            try {
                populateCourseCards();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Creates a new course
     * @param name the name of the course
     * @param startDate the start date of the course
     * @param endDate the end date of the course
     * @param status the status of the course
     * @param instructors the instructors to be added to the course
     * @param assessments the assessments to be added to the course
     * @throws InterruptedException
     */

    private void createNewCourse(String name, LocalDate startDate, LocalDate endDate, Status status, List<Instructor> instructors, List<Assessment> assessments, String note) throws InterruptedException, ExecutionException {
        repository = new Repository(getApplication());

        Course course = new Course(0, 0, name, startDate, endDate, status);
        course.setNote(note);

        long generatedID = repository.insert(course);

        if (generatedID <= 0) { return; }

        course.setCourseID((int) generatedID);

        for (Instructor instructor : instructors) {
            instructor.setCourseID(course.getCourseID());
            repository.update(instructor);
        }
        for (Assessment assessment : assessments) {
            assessment.setCourseID(course.getCourseID());
            repository.update(assessment);
        }
        courses.add(course);
    }

    /**
     * Modifies a course
     * @param course the course being modified
     * @param name the new name for the course
     * @param startDate the new start date for the course
     * @param endDate the new end date for the course
     * @param instructors the list of instructors assigned to the course
     * @param assessments the list of assessments assigned to the course
     * @throws ExecutionException
     * @throws InterruptedException
     */

    private void modifyCourse(Course course, String name, LocalDate startDate, LocalDate endDate, Status status, List<Assessment> assessments, List<Instructor> instructors, String note) throws ExecutionException, InterruptedException {
        repository = new Repository(getApplication());

        List<Instructor> allInstructors = repository.getAllInstructors();
        List<Instructor> allCourseInstructors = repository.getAllCourseInstructors(course);

        List<Assessment> allAssessments = repository.getAllAssessments();
        List<Assessment> allCourseAssessments = repository.getAllCourseAssessments(course);

        Course newCourse = new Course(course.getCourseID(), course.getTermID(), name, startDate, endDate, status);
        newCourse.setCourseID(course.getCourseID());
        newCourse.setNote(note);

        // Checks if the assessments list is empty. If it is, assign all previously assigned assessments a course ID of 0.
        if (assessments.isEmpty()) {
            for (Assessment assessment : allCourseAssessments) {
                assessment.setCourseID(0);
                repository.update(assessment);
            }
        } else {
            // Else if the assigned assessments list doesn't have an assessment but the total assessments list does, set the assessment course ID to 0.
            for (Assessment assessment : allCourseAssessments) {
                if (!assessments.contains(assessment) && allCourseAssessments.contains(assessment)) {
                    assessment.setCourseID(0);
                }
                repository.update(assessment);
            }
            // For all the assigned assessments, set the assessments course ID to the course ID it is assigned to.
            for (Assessment assessment : allAssessments) {
                if (assessments.contains(assessment)) {
                    assessment.setCourseID(course.getCourseID());
                }
                repository.update(assessment);
            }
        }

        // Checks if the instructor list is empty. If it is, assign all previously assigned instructors a course ID of 0.
        if (instructors.isEmpty()) {
            for (Instructor instructor : allCourseInstructors) {
                instructor.setCourseID(0);
                repository.update(instructor);
            }
        } else {
            // Else if the assigned instructors list doesn't have an instructor but the total instructor list does, set the instructor course ID to 0.
            for (Instructor instructor : allCourseInstructors) {
                if (!instructors.contains(instructor) && allCourseInstructors.contains(instructor)) {
                    instructor.setCourseID(0);
                }
                repository.update(instructor);
            }
            // For all the assigned instructors, set the instructors course ID to the course ID it is assigned to.
            for (Instructor instructor : allInstructors) {
                if (instructors.contains(instructor)) {
                    instructor.setCourseID(course.getCourseID());
                }
                repository.update(instructor);
            }
        }

        repository.update(newCourse);
        courses.remove(course);
        courses.add(newCourse);
    }

    /**
     * Creates a button for the course
     * @param course the course to have the button created for
     */

    private void createCourseButton(Course course) throws InterruptedException, ExecutionException {
        LinearLayout parentLayout = findViewById(R.id.courses_list_layout);
        parentLayout.setPadding(parentLayout.getPaddingLeft(),
                parentLayout.getPaddingTop(),
                parentLayout.getPaddingRight(),
                (courses.size()*36));

        CardView cardView = new CardView(this);
        cardView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        cardView.setCardElevation(8);
        cardView.setRadius(16);
        cardView.setPadding(16, 16, 16, 16);
        cardView.setUseCompatPadding(true);

        // Create parent LinearLayout inside the CardView
        LinearLayout cardLayout = new LinearLayout(this);
        cardLayout.setOrientation(LinearLayout.VERTICAL);
        cardLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Create button
        Button courseButton = new Button(this);
        courseButton.setText(course.getCourseName());
        courseButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        courseButton.setBackgroundColor(ContextCompat.getColor(this, R.color.tertiary));
        courseButton.setTextColor(ContextCompat.getColor(this, R.color.primary_variant));

        // Create expandable section layout
        LinearLayout expandableLayout = new LinearLayout(this);
        expandableLayout.setOrientation(LinearLayout.VERTICAL);
        expandableLayout.setVisibility(expandedStates.getOrDefault(course.getCourseID(), false) ? View.VISIBLE : View.GONE);
        expandableLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        expandableLayout.setPadding(16, 16, 16, 16);

        // Add details to the expandable section
        TextView startTextView = new TextView(this);
        startTextView.setText("Start Date: " + course.getStartDate());
        expandableLayout.addView(startTextView);

        TextView endTextView = new TextView(this);
        endTextView.setText("End Date: " + course.getEndDate());
        expandableLayout.addView(endTextView);

        TextView assignedInstructorView = new TextView(this);
        assignedInstructorView.setText("\nAssigned Instructors: ");
        expandableLayout.addView(assignedInstructorView);

        List<Instructor> assignedInstructors = repository.getAllCourseInstructors(course);
        if (!assignedInstructors.isEmpty()) {
            for (Instructor instructor : assignedInstructors) {
                Button i = new Button(this);
                i.setText(instructor.getInstructorName());
                expandableLayout.addView(i);
                i.setOnClickListener(v -> {
                    startActivity(new Intent(getApplicationContext(), InstructorsActivity.class));
                });
            }
        }

        TextView assignedAssessmentsView = new TextView(this);
        assignedAssessmentsView.setText("\nAssigned Assessments: ");
        expandableLayout.addView(assignedAssessmentsView);

        List<Assessment> assignedAssessments = repository.getAllCourseAssessments(course);
        if (!assignedAssessments.isEmpty()) {
            for (Assessment assessment : assignedAssessments) {
                Button i = new Button(this);
                i.setText(assessment.getName());
                expandableLayout.addView(i);
                i.setOnClickListener(v -> {
                    startActivity(new Intent(getApplicationContext(), AssessmentsActivity.class));
                });
            }
        }

        if (course.getNote() != null) {
            TextView notes = new TextView(this);
            notes.setText("\nNotes:\n" + course.getNote());
            expandableLayout.addView(notes);
        }

        // Button click listener to toggle expandable layout
        courseButton.setOnClickListener(v -> {
            boolean isExpanded = expandableLayout.getVisibility() == View.VISIBLE;
            expandableLayout.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            expandedStates.put(course.getCourseID(), !isExpanded);

            parentLayout.post(() -> {
                parentLayout.requestLayout();
                parentLayout.invalidate();
            });
        });

        courseButton.setOnLongClickListener(v -> {
            try {
                openInputDialog(course);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            return true;
        });

        // Add button and expandable layout to the card layout
        cardLayout.addView(courseButton);
        cardLayout.addView(expandableLayout);

        // Add card layout to the card view
        cardView.addView(cardLayout);

        // Add the card view to the parent layout
        parentLayout.addView(cardView);
    }

    /**
     * Popuates the course cards in the activity
     */

    private void populateCourseCards() throws InterruptedException, ExecutionException {
        if (courseLayout != null) {
            courseLayout.removeAllViews();
        }

        for (Course course : courses) {
            createCourseButton(course);
        }
    }

    /**
     * Handles menu item selections.
     * @param item The menu item that was selected.
     * @return true to consume the event. If the selected button is not the home button, calls the superclass method.
     */

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Saves the expanded state of the course cards.
     * @param outState Bundle in which to place your saved state.
     */

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("expandedStates", expandedStates);
    }

    /**
     * Restores the saved instance state.
     * @param savedInstanceState the data most recently supplied in {@link #onSaveInstanceState}.
     */

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            expandedStates = (HashMap<Integer, Boolean>) savedInstanceState.getSerializable("expandedStates");
        }
        try {
            populateCourseCards();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Called the superlcass method when changing configuration.
     * @param newConfig The new device configuration.
     */

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
