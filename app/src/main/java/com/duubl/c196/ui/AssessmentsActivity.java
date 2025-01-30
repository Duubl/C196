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
import com.duubl.c196.entities.AssessmentType;
import com.duubl.c196.entities.Course;
import com.duubl.c196.entities.Instructor;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AssessmentsActivity extends AppCompatActivity {

    private Button newAssessmentButton;
    private LinearLayout assessmentLayout;
    private Repository repository;
    private List<Assessment> assessments;

    // HashMap to store the expanded states of the cards. Helpful for changing orientation of the phone.
    private HashMap<Integer, Boolean> expandedStates = new HashMap<>();

    /**
     * Creates the activity. Adds the toolbar and populates the activity with the assessment cards.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessments);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Layout for the list of instructors.
        assessmentLayout = findViewById(R.id.assessments_list_layout);
        if (assessmentLayout == null) {
            Log.e("InstructorsActivity", "instructorLayout is null!");
            return;
        }

        // New instructor button
        newAssessmentButton = findViewById(R.id.new_assessment_button);
        newAssessmentButton.setOnClickListener(item -> {
            openInputDialog();
        });

        // Loads the instructors into the activity on creation
        repository = new Repository(getApplication());
        try {
            assessments = repository.getAllAssessments();
        } catch (InterruptedException | ExecutionException e) {
            Log.e("AssessmentsActivity", "there are no assessments!");
            throw new RuntimeException(e);
        }
        try {
            populateAssessmentCards();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Opens the input dialog for adding a new assessment.
     * Takes input from the user for the name, start and end date and type of assessment.
     */

    private void openInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Assessment");

        LinearLayout inputLayout = new LinearLayout(this);
        inputLayout.setOrientation(LinearLayout.VERTICAL);
        inputLayout.setPadding(16, 16, 16, 16);

        // Get the instructor name
        final EditText nameInput = new EditText(this);
        nameInput.setHint("Assessment Name");
        inputLayout.addView(nameInput);

        final Button startDateButton = new Button(this);
        startDateButton.setText("Assessment Start Date");
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
        endDateButton.setText("Assessment End Date");
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

        final Button assessmentTypeButton = new Button(this);
        assessmentTypeButton.setText("Assessment Type");
        final AssessmentType[] type = new AssessmentType[1];
        inputLayout.addView(assessmentTypeButton);
        assessmentTypeButton.setOnClickListener(v -> {
            AssessmentType[] assessmentTypes = AssessmentType.values();
            String[] options = new String[assessmentTypes.length];
            for (int i = 0; i < assessmentTypes.length; i++) {
                options[i] = assessmentTypes[i].name();
            }

            new AlertDialog.Builder(this)
                    .setTitle("Select Assessment Type")
                    .setItems(options, (dialog, which) -> {
                        assessmentTypeButton.setText(options[which]);
                        type[0] = assessmentTypes[which];
                    })
                    .create()
                    .show();
        });
        builder.setView(inputLayout);

        // Creates the button on submit if all fields contain information.
        // TODO: Add error checking and proper formatting checking
        builder.setPositiveButton("Add", (dialog, which) -> {
            String assessmentName = nameInput.getText().toString().trim();
            if (assessmentName.isEmpty() || localStartDate[0] == null || localEndDate[0] == null) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                createNewAssessment(assessmentName, localStartDate[0], localEndDate[0], type[0]);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            Log.d("AssessmentsActivity", "Sent data to create new assessment " + assessmentName);
            try {
                populateAssessmentCards();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Opens the input dialog for modifying an assessment
     * Takes input from the user for the name, start and end dates and the assessment type.
     * @param assessment the assessment being modified
     */

    private void openInputDialog(Assessment assessment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modify Assessment");

        LinearLayout inputLayout = new LinearLayout(this);
        inputLayout.setOrientation(LinearLayout.VERTICAL);
        inputLayout.setPadding(16, 16, 16, 16);

        // Get the instructor name
        final EditText nameInput = new EditText(this);
        nameInput.setHint("Assessment Name");
        nameInput.setText(assessment.getName());
        inputLayout.addView(nameInput);

        final Button startDateButton = new Button(this);
        startDateButton.setText(assessment.getStartDate().toString());
        inputLayout.addView(startDateButton);

        final LocalDate[] localStartDate = new LocalDate[1];
        localStartDate[0] = assessment.getStartDate();
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
        endDateButton.setText(assessment.getEndDate().toString());
        inputLayout.addView(endDateButton);

        final LocalDate[] localEndDate = new LocalDate[1];
        localEndDate[0] = assessment.getEndDate();
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

        final Button assessmentTypeButton = new Button(this);
        assessmentTypeButton.setText(assessment.getType().name());
        final AssessmentType[] type = new AssessmentType[1];
        type[0] = assessment.getType();
        inputLayout.addView(assessmentTypeButton);
        assessmentTypeButton.setOnClickListener(v -> {
            AssessmentType[] assessmentTypes = AssessmentType.values();
            String[] options = new String[assessmentTypes.length];
            for (int i = 0; i < assessmentTypes.length; i++) {
                options[i] = assessmentTypes[i].name();
            }

            new AlertDialog.Builder(this)
                    .setTitle("Select Assessment Type")
                    .setItems(options, (dialog, which) -> {
                        assessmentTypeButton.setText(options[which]);
                        type[0] = assessmentTypes[which];
                    })
                    .create()
                    .show();
        });
        builder.setView(inputLayout);

        // Creates the button on submit if all fields contain information.
        // TODO: Add error checking and proper formatting checking
        builder.setPositiveButton("Update Assessment", (dialog, which) -> {
            String assessmentName = nameInput.getText().toString().trim();
            if (assessmentName.isEmpty() || localStartDate[0] == null || localEndDate[0] == null) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                modifyAssessment(assessment, assessmentName, localStartDate[0], localEndDate[0], type[0]);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            try {
                populateAssessmentCards();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        builder.setNeutralButton("Delete Assessment", (dialog, which) -> {
            AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(this);
            deleteBuilder.setTitle("You Sure, Bud?");
            deleteBuilder.setNegativeButton("Cancel", (z, x) -> {
                dialog.cancel();
            });
            deleteBuilder.setPositiveButton("Confirm", (d, w) -> {
                try {
                    deleteAssessment(assessment);
                    populateAssessmentCards();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            deleteBuilder.show();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Creates a new assessment
     * @param name the name of the assessment
     * @param startDate the start date of the assessment
     * @param endDate the end date of the assessment
     */

    private void createNewAssessment(String name, LocalDate startDate, LocalDate endDate, AssessmentType type) throws InterruptedException, ExecutionException {
        repository = new Repository(getApplication());
        Assessment assessment = new Assessment(0, name, startDate, endDate, type);

        long generatedID = repository.insert(assessment);

        if (generatedID <= 0 ) { return; }

        assessment.setAssessmentID((int) generatedID);
        assessments.add(assessment);
    }

    /**
     * Modifies an assessment
     * @param assessment the assessment being modified
     * @param name the new name for the assessment
     * @param startDate the new start date for the assessment
     * @param endDate the new end date for the assessment
     * @param type the new type for the assessment
     * @throws InterruptedException
     * @throws ExecutionException
     */

    private void modifyAssessment(Assessment assessment, String name, LocalDate startDate, LocalDate endDate, AssessmentType type) throws InterruptedException, ExecutionException {
        repository = new Repository(getApplication());
        Assessment newAssessment = new Assessment(assessment.getAssessmentID(), name, startDate, endDate, type);
        newAssessment.setCourseID(assessment.getCourseID());

        repository.update(newAssessment);
        assessments.remove(assessment);
        assessments.add(newAssessment);
    }

    /**
     * Deletes a given assessment
     * @param assessment the assessment to be removed
     * @throws ExecutionException
     * @throws InterruptedException
     */

    private void deleteAssessment(Assessment assessment) throws ExecutionException, InterruptedException {
        repository = new Repository(getApplication());
        repository.delete(assessment);
        assessments.remove(assessment);
    }

    /**
     * Creates the button for the assessment
     * @param assessment the assessment to have the button created for
     */

    private void createAssessmentButton(Assessment assessment) throws InterruptedException {
        LinearLayout parentLayout = findViewById(R.id.assessments_list_layout);
        parentLayout.setPadding(parentLayout.getPaddingLeft(),
                parentLayout.getPaddingTop(),
                parentLayout.getPaddingRight(),
                (assessments.size()*36));

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
        Button assessmentButton = new Button(this);
        assessmentButton.setText(assessment.getName());
        assessmentButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        assessmentButton.setBackgroundColor(ContextCompat.getColor(this, R.color.tertiary));
        assessmentButton.setTextColor(ContextCompat.getColor(this, R.color.primary_variant));

        // Create expandable section layout
        LinearLayout expandableLayout = new LinearLayout(this);
        expandableLayout.setOrientation(LinearLayout.VERTICAL);
        expandableLayout.setVisibility(expandedStates.getOrDefault(assessment.getAssessmentID(), false) ? View.VISIBLE : View.GONE);
        expandableLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        expandableLayout.setPadding(16, 16, 16, 16);

        // Add details to the expandable section
        TextView startTextView = new TextView(this);
        startTextView.setText("Start Date: " + assessment.getStartDate());
        expandableLayout.addView(startTextView);

        TextView endTextView = new TextView(this);
        endTextView.setText("End Date: " + assessment.getEndDate());
        expandableLayout.addView(endTextView);

        TextView typeTextView = new TextView(this);
        typeTextView.setText("\nAssessment Type:\n" + assessment.getType().name());
        expandableLayout.addView(typeTextView);

        TextView assignedCoursesView = new TextView(this);
        assignedCoursesView.setText("\nAssigned course: ");
        expandableLayout.addView(assignedCoursesView);

        // Button click listener to toggle expandable layout
        assessmentButton.setOnClickListener(v -> {
            boolean isExpanded = expandableLayout.getVisibility() == View.VISIBLE;
            expandableLayout.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            expandedStates.put(assessment.getAssessmentID(), !isExpanded);

            parentLayout.post(() -> {
                parentLayout.requestLayout();
                parentLayout.invalidate();
            });
        });

        // Long press listener to open edit menu
        assessmentButton.setOnLongClickListener(v -> {
            openInputDialog(assessment);
            return true;
        });

        List<Course> assignedCourses = repository.getAllAssessmentCourses(assessment);
        if (assignedCourses != null) {
            if (!assignedCourses.isEmpty()) {
                for (Course course : assignedCourses) {
                    Button c = new Button(this);
                    c.setText(course.getCourseName());
                    expandableLayout.addView(c);
                    c.setOnClickListener(v -> {
                        startActivity(new Intent(getApplicationContext(), CoursesActivity.class));
                    });
                }
            } else {
                TextView noCourses = new TextView(this);
                noCourses.setText("Not assigned to any courses!");
                expandableLayout.addView(noCourses);
            }
        }

        // Add button and expandable layout to the card layout
        cardLayout.addView(assessmentButton);
        cardLayout.addView(expandableLayout);

        // Add card layout to the card view
        cardView.addView(cardLayout);

        // Add the card view to the parent layout
        parentLayout.addView(cardView);
    }

    /**
     * Populates the instructor cards in the activity.
     */

    private void populateAssessmentCards() throws InterruptedException {
        if (assessmentLayout != null) {
            assessmentLayout.removeAllViews();
        }

        for (Assessment assessment : assessments) {
            createAssessmentButton(assessment);
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
     * Saves the expanded state of the assessment cards.
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
            populateAssessmentCards();
        } catch (InterruptedException e) {
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
