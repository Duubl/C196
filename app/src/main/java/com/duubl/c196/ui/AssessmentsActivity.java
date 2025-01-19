package com.duubl.c196.ui;

import android.app.DatePickerDialog;
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
import com.duubl.c196.entities.Instructor;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AssessmentsActivity extends AppCompatActivity {

    private Button newAssessmentButton;
    private LinearLayout assessmentLayout;
    private Repository repository;
    private List<Assessment> assessments;

    // HashMap to store the expanded states of the cards. Helpful for changing orientation of the phone.
    private HashMap<Integer, Boolean> expandedStates = new HashMap<>();

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
        } catch (InterruptedException e) {
            Log.e("AssessmentsActivity", "there are no assessments!");
            throw new RuntimeException(e);
        }
        populateAssessmentCards();
    }

    /**
     * Opens the input dialog for adding a new instructor.
     * Takes input from the user for the name, phone number and address for the instructor.
     */

    private void openInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Assessment");

        LinearLayout inputLayout = new LinearLayout(this);
        inputLayout.setOrientation(LinearLayout.VERTICAL);
        inputLayout.setPadding(16, 16, 16, 16);

        // Get the instructor name
        final EditText nameInput = new EditText(this);
        nameInput.setHint("Enter the name for the assessment");
        inputLayout.addView(nameInput);

        // Get the start date of the assessment
        final TextView startDateText = new TextView(this);
        startDateText.setText("Assessment start date");
        inputLayout.addView(startDateText);

        final Button startDateButton = new Button(this);
        startDateButton.setText("Select the start date of the assessment");
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

        // Get the end date of the assessment
        final TextView endDateText = new TextView(this);
        endDateText.setText("Assessment end date");
        inputLayout.addView(endDateText);

        final Button endDateButton = new Button(this);
        endDateButton.setText("Select the date of the assessment");
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
        assessmentTypeButton.setText("Select the assessment type");
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

        // TODO: Add ability to add instructor to a course

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
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Log.d("AssessmentsActivity", "Sent data to create new assessment " + assessmentName);
            populateAssessmentCards();
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

    private void createNewAssessment(String name, LocalDate startDate, LocalDate endDate, AssessmentType type) throws InterruptedException {
        repository = new Repository(getApplication());
        Assessment assessment = new Assessment(0, name, startDate, endDate, type);
        repository.insert(assessment);
        assessments.add(assessment);
    }

    /**
     * Creates the button for the assessment
     * @param assessment the assessment to have the button created for
     */

    private void createAssessmentButton(Assessment assessment) {
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
        Button instructorButton = new Button(this);
        instructorButton.setText(assessment.getName());
        instructorButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        instructorButton.setBackgroundColor(ContextCompat.getColor(this, R.color.tertiary));
        instructorButton.setTextColor(ContextCompat.getColor(this, R.color.primary_variant));

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

        // TODO: Add course assessment is assigned to
        TextView assignedCoursesView = new TextView(this);
        assignedCoursesView.setText("\nAssigned course: ");
        expandableLayout.addView(assignedCoursesView);

        // Button click listener to toggle expandable layout
        instructorButton.setOnClickListener(v -> {
            boolean isExpanded = expandableLayout.getVisibility() == View.VISIBLE;
            expandableLayout.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            expandedStates.put(assessment.getAssessmentID(), !isExpanded);

            parentLayout.post(() -> {
                parentLayout.requestLayout();
                parentLayout.invalidate();
            });
        });

        // Add button and expandable layout to the card layout
        cardLayout.addView(instructorButton);
        cardLayout.addView(expandableLayout);

        // Add card layout to the card view
        cardView.addView(cardLayout);

        // Add the card view to the parent layout
        parentLayout.addView(cardView);
    }

    /**
     * Populates the instructor cards in the activity.
     */

    private void populateAssessmentCards() {
        if (assessmentLayout != null) {
            assessmentLayout.removeAllViews();
        }

        for (Assessment assessment : assessments) {
            createAssessmentButton(assessment);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
