package com.duubl.c196.ui;

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
import com.duubl.c196.entities.Instructor;

import java.util.ArrayList;
import java.util.List;

public class InstructorsActivity extends AppCompatActivity {

    private Button new_instructor_button;
    private LinearLayout instructor_layout;
    private Repository repository;
    private List<Instructor> instructors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructors);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Layout for the list of instructors.
        instructor_layout = findViewById(R.id.instructor_list_layout);
        instructor_layout.setOrientation(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                ? LinearLayout.HORIZONTAL
                : LinearLayout.VERTICAL);

        // New instructor button
        new_instructor_button = findViewById(R.id.new_instructor_button);
        new_instructor_button.setOnClickListener(item -> {
            openInputDialog();
        });

        // Loads the instructors into the activity on creation
        repository = new Repository(getApplication());
        try {
            instructors = repository.getAllInstructors();
        } catch (InterruptedException e) {
            Log.e("InstructorsActivity", "there are no instructors!");
            throw new RuntimeException(e);
        }
        for (Instructor instructor : instructors) {
            createInstructorButton(instructor.getInstructor_name(), instructor.getInstructor_phone(), instructor.getInstructor_email());
        }
    }

    /**
     * Opens the input dialog for adding a new instructor.
     * Takes input from the user for the name, phone number and address for the instructor.
     */

    private void openInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Instructor");

        LinearLayout inputLayout = new LinearLayout(this);
        inputLayout.setOrientation(LinearLayout.VERTICAL);
        inputLayout.setPadding(16, 16, 16, 16);

        // Get the instructor name
        final EditText nameInput = new EditText(this);
        nameInput.setHint("Enter instructor name");
        inputLayout.addView(nameInput);

        // Get instructor phone number
        final EditText phoneInput = new EditText(this);
        phoneInput.setHint("Enter instructor's phone number");
        inputLayout.addView(phoneInput);

        // Get instructor email address
        final EditText emailInput = new EditText(this);
        emailInput.setHint("Enter instructor's email address");
        inputLayout.addView(emailInput);

        // TODO: Add ability to add instructor to a course

        builder.setView(inputLayout);

        // Creates the button on submit if all fields contain information.
        // TODO: Add error checking and proper formatting checking
        builder.setPositiveButton("Add", (dialog, which) -> {
            String instructorName = nameInput.getText().toString().trim();
            String instructorPhone = phoneInput.getText().toString().trim();
            String instructorEmail = emailInput.getText().toString().trim();
            if (instructorName.isEmpty() || instructorPhone.isEmpty() || instructorEmail.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                createNewInstructor(instructorName, instructorPhone, instructorEmail);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            createInstructorButton(instructorName, instructorPhone, instructorEmail);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Creates a new instructor.
     * @param name the name of the instructor
     * @param phone the phone number of the instructor
     * @param email the email of the instructor
     * @throws InterruptedException
     */

    private void createNewInstructor(String name, String phone, String email) throws InterruptedException {
        repository = new Repository(getApplication());
        Instructor instructor = new Instructor(0, 0, name, phone, email);
        repository.insert(instructor);
    }

    /**
     * Creates the button for the instructor.
     * @param name the name of the instructor to be displayed on the button
     * @param phone the phone number of the instructor
     * @param email the email of the instructor
     */

    private void createInstructorButton(String name, String phone, String email) {
        LinearLayout parentLayout = findViewById(R.id.instructor_list_layout);

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
        instructorButton.setText(name);
        instructorButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        instructorButton.setBackgroundColor(ContextCompat.getColor(this, R.color.tertiary));
        instructorButton.setTextColor(ContextCompat.getColor(this, R.color.primary_variant));

        // Create expandable section layout
        LinearLayout expandableLayout = new LinearLayout(this);
        expandableLayout.setOrientation(LinearLayout.VERTICAL);
        expandableLayout.setVisibility(View.GONE);
        expandableLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        expandableLayout.setPadding(16, 16, 16, 16);

        // Add details to the expandable section
        TextView emailTextView = new TextView(this);
        emailTextView.setText("Email: " + email);
        expandableLayout.addView(emailTextView);

        TextView phoneTextView = new TextView(this);
        phoneTextView.setText("Phone: " + phone);
        expandableLayout.addView(phoneTextView);

        // TODO: Add assigned courses to instructors
        // Should show a list of courses
        TextView assignedCoursesView = new TextView(this);
        assignedCoursesView.setText("\nAssigned courses: ");
        expandableLayout.addView(assignedCoursesView);

        // Button click listener to toggle expandable layout
        instructorButton.setOnClickListener(v -> {
            if (expandableLayout.getVisibility() == View.GONE) {
                expandableLayout.setVisibility(View.VISIBLE);
            } else {
                expandableLayout.setVisibility(View.GONE);
            }
        });

        // Add button and expandable layout to the card layout
        cardLayout.addView(instructorButton);
        cardLayout.addView(expandableLayout);

        // Add card layout to the card view
        cardView.addView(cardLayout);

        // Add the card view to the parent layout
        parentLayout.addView(cardView);
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
