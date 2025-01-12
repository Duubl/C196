package com.duubl.c196.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.duubl.c196.R;
import com.duubl.c196.database.Repository;
import com.duubl.c196.entities.Instructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InstructorsActivity extends AppCompatActivity {

    private Button newInstructorButton;
    private LinearLayout instructorLayout;
    private Repository repository;
    private List<Instructor> instructors;

    // HashMap to store the expanded states of the cards. Helpful for changing orientation of the phone.
    private HashMap<Integer, Boolean> expandedStates = new HashMap<>();

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
        instructorLayout = findViewById(R.id.instructor_list_layout);
        if (instructorLayout == null) {
            Log.e("InstructorsActivity", "instructorLayout is null!");
            return;
        }

        // New instructor button
        newInstructorButton = findViewById(R.id.new_instructor_button);
        newInstructorButton.setOnClickListener(item -> {
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
        populateInstructorCards();

        instructorLayout.post(()-> {
            adjustLayoutOrientation(getResources().getConfiguration().orientation);
        });
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
            Log.d("InstructorsActivity", "Sent data to create new instructor " + instructorName);
            populateInstructorCards();
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
        Log.d("InstructorsActivity", "Created a new instructor: " + name);
        repository = new Repository(getApplication());
        Instructor instructor = new Instructor(0, 0, name, phone, email);
        repository.insert(instructor);
        instructors.add(instructor);
    }

    /**
     * Creates the button for the instructor.
     * @param instructor the instructor to have the button created for
     */

    private void createInstructorButton(Instructor instructor) {
        Log.d("InstructorsActivity", "instructor " + instructor.getInstructor_name() + " button created!");
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
        instructorButton.setText(instructor.getInstructor_name());
        instructorButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        instructorButton.setBackgroundColor(ContextCompat.getColor(this, R.color.tertiary));
        instructorButton.setTextColor(ContextCompat.getColor(this, R.color.primary_variant));

        // Create expandable section layout
        LinearLayout expandableLayout = new LinearLayout(this);
        expandableLayout.setOrientation(LinearLayout.VERTICAL);
        expandableLayout.setVisibility(expandedStates.getOrDefault(instructor.getInstructor_id(), false) ? View.VISIBLE : View.GONE);
        expandableLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        expandableLayout.setPadding(16, 16, 16, 16);

        // Add details to the expandable section
        TextView emailTextView = new TextView(this);
        emailTextView.setText("Email: " + instructor.getInstructor_email());
        expandableLayout.addView(emailTextView);

        TextView phoneTextView = new TextView(this);
        phoneTextView.setText("Phone: " + instructor.getInstructor_phone());
        expandableLayout.addView(phoneTextView);

        // TODO: Add assigned courses to instructors
        // Should show a list of courses
        TextView assignedCoursesView = new TextView(this);
        assignedCoursesView.setText("\nAssigned courses: ");
        expandableLayout.addView(assignedCoursesView);

        // Button click listener to toggle expandable layout
        instructorButton.setOnClickListener(v -> {
            boolean isExpanded = expandableLayout.getVisibility() == View.VISIBLE;
            expandableLayout.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            expandedStates.put(instructor.getInstructor_id(), !isExpanded);
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

    private void populateInstructorCards() {
        if (instructorLayout != null) {
            instructorLayout.removeAllViews();
        }

        for (Instructor instructor : instructors) {
            createInstructorButton(instructor);
        }
    }

    /**
     * Adjusts the layout orientation when switching from portrait to landscape & vice-versa.
     * @param orientation the orientation
     */

    private void adjustLayoutOrientation(int orientation) {
        Log.d("InstructorsActivity", "Adjusting layout orientation: " + orientation);

        LinearLayout instructorLayout = findViewById(R.id.instructor_list_layout);
        if (instructorLayout == null) {
            Log.e("InstructorsActivity", "instructorLayout is null!");
            return;
        }

        ScrollView scrollView = findViewById(R.id.scroll_view);

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Change the orientation to horizontal in landscape mode
            instructorLayout.setOrientation(LinearLayout.HORIZONTAL);

            HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);
            horizontalScrollView.setId(R.id.scroll_view);
            horizontalScrollView.addView(instructorLayout);

            // Get the parent constraint layout and replace the current ScrollView
            ConstraintLayout parent = findViewById(R.id.constraint_layout);
            if (parent != null) {
                parent.removeView(scrollView);
                parent.addView(horizontalScrollView);
            }

        } else {
            // Change the orientation to vertical in portrait mode
            instructorLayout.setOrientation(LinearLayout.VERTICAL);

            // Ensure ScrollView is used for portrait mode
            if (!(scrollView instanceof ScrollView)) {
                ScrollView verticalScrollView = new ScrollView(this);
                verticalScrollView.setId(R.id.scroll_view);
                verticalScrollView.addView(instructorLayout);

                // Get the parent constraint layout and replace the current ScrollView
                ConstraintLayout parent = findViewById(R.id.constraint_layout);
                if (parent != null) {
                    parent.removeView(scrollView);
                    parent.addView(verticalScrollView);
                }
            }
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("expandedStates", expandedStates);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            expandedStates = (HashMap<Integer, Boolean>) savedInstanceState.getSerializable("expandedStates");
        }
        populateInstructorCards();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        adjustLayoutOrientation(newConfig.orientation);
    }
}
