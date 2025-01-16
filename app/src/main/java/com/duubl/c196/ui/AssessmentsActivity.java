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

        final Calendar[] startDate = new Calendar[1];
        startDateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                startDate[0] = calendar;
                startDateButton.setText(String.format("%d/%d/%d", month + 1, dayOfMonth, year));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Get the end date of the assessment
        final TextView endDateText = new TextView(this);
        endDateText.setText("Assessment end date");
        inputLayout.addView(endDateText);

        final Button endDateButton = new Button(this);
        endDateButton.setText("Select the date of the assessment");
        inputLayout.addView(endDateButton);

        final Calendar[] endDate = new Calendar[1];
        endDateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                endDate[0] = calendar;
                endDateButton.setText(String.format("%d/%d/%d", month + 1, dayOfMonth, year));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // TODO: Add conversion from calendar[] to LocalDate

        // TODO: Add ability to select the type of assessment

        // TODO: Add ability to add instructor to a course

        builder.setView(inputLayout);

        // Creates the button on submit if all fields contain information.
        // TODO: Add error checking and proper formatting checking
        builder.setPositiveButton("Add", (dialog, which) -> {
            String assessmentName = nameInput.getText().toString().trim();
            if (assessmentName.isEmpty() || startDate[0] == null) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                createNewAssessment(assessmentName, startDate, endDate);
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

    private void createNewAssessment(String name, LocalDate startDate, LocalDate endDate) {
        // TODO: Create assessments. Also add type as an input
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
        instructorButton.setText(assessment.getAssessment_name());
        instructorButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        instructorButton.setBackgroundColor(ContextCompat.getColor(this, R.color.tertiary));
        instructorButton.setTextColor(ContextCompat.getColor(this, R.color.primary_variant));

        // Create expandable section layout
        LinearLayout expandableLayout = new LinearLayout(this);
        expandableLayout.setOrientation(LinearLayout.VERTICAL);
        expandableLayout.setVisibility(expandedStates.getOrDefault(assessment.getAssessment_id(), false) ? View.VISIBLE : View.GONE);
        expandableLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        expandableLayout.setPadding(16, 16, 16, 16);

        // Add details to the expandable section
        TextView startTextView = new TextView(this);
        startTextView.setText("Start Date: " + assessment.getStart_date());
        expandableLayout.addView(startTextView);

        TextView endTextView = new TextView(this);
        endTextView.setText("End Date: " + assessment.getEnd_date());
        expandableLayout.addView(endTextView);

        // TODO: Add course assessment is assigned to
        TextView assignedCoursesView = new TextView(this);
        assignedCoursesView.setText("\nAssigned course: ");
        expandableLayout.addView(assignedCoursesView);

        // Button click listener to toggle expandable layout
        instructorButton.setOnClickListener(v -> {
            boolean isExpanded = expandableLayout.getVisibility() == View.VISIBLE;
            expandableLayout.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            expandedStates.put(assessment.getAssessment_id(), !isExpanded);

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
