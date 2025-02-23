package com.duubl.c196.ui;

import android.content.Intent;
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
import com.duubl.c196.entities.Assessment;
import com.duubl.c196.entities.Course;
import com.duubl.c196.entities.Instructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class InstructorsActivity extends AppCompatActivity {

    private Button newInstructorButton;
    private LinearLayout instructorLayout;
    private Repository repository;
    private List<Instructor> instructors;

    // HashMap to store the expanded states of the cards. Helpful for changing orientation of the phone.
    private HashMap<Integer, Boolean> expandedStates = new HashMap<>();

    /**
     * Creates the activity. Adds the toolbar and populates the activity with the instructor cards.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */

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
        } catch (InterruptedException | ExecutionException e) {
            Log.e("InstructorsActivity", "there are no instructors!");
            throw new RuntimeException(e);
        }
        try {
            populateInstructorCards();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
        nameInput.setHint("Instructor Name");
        inputLayout.addView(nameInput);

        // Get instructor phone number
        final EditText phoneInput = new EditText(this);
        phoneInput.setHint("Instructor's Phone Number");
        inputLayout.addView(phoneInput);

        // Get instructor email address
        final EditText emailInput = new EditText(this);
        emailInput.setHint("Instructor's Email Address");
        inputLayout.addView(emailInput);

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
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            try {
                populateInstructorCards();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Opens the input dialog for modifying an instructor
     * Takes input from the user for the name, phone number and address for the instructor.
     */

    private void openInputDialog(Instructor instructor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modify Instructor");

        LinearLayout inputLayout = new LinearLayout(this);
        inputLayout.setOrientation(LinearLayout.VERTICAL);
        inputLayout.setPadding(16, 16, 16, 16);

        // Get the instructor name
        final EditText nameInput = new EditText(this);
        nameInput.setHint("Instructor Name");
        nameInput.setText(instructor.getInstructorName());
        inputLayout.addView(nameInput);

        // Get instructor phone number
        final EditText phoneInput = new EditText(this);
        phoneInput.setHint("Instructor's Phone Number");
        phoneInput.setText(instructor.getInstructorPhone());
        inputLayout.addView(phoneInput);

        // Get instructor email address
        final EditText emailInput = new EditText(this);
        emailInput.setHint("Instructor's Email Address");
        emailInput.setText(instructor.getInstructorEmail());
        inputLayout.addView(emailInput);

        builder.setView(inputLayout);

        // Creates the button on submit if all fields contain information.
        // TODO: Add error checking and proper formatting checking
        builder.setPositiveButton("Update Instructor", (dialog, which) -> {
            String instructorName = nameInput.getText().toString().trim();
            String instructorPhone = phoneInput.getText().toString().trim();
            String instructorEmail = emailInput.getText().toString().trim();
            if (instructorName.isEmpty() || instructorPhone.isEmpty() || instructorEmail.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                modifyInstructor(instructor, instructorName, instructorPhone, instructorEmail);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            try {
                populateInstructorCards();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        builder.setNeutralButton("Delete Instructor", (dialog, which) -> {
            AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(this);
            deleteBuilder.setTitle("You Sure, Bud?");
            deleteBuilder.setNegativeButton("Cancel", (z, x) -> {
                dialog.cancel();
            });
            deleteBuilder.setPositiveButton("Confirm", (d, w) -> {
                try {
                    deleteInstructor(instructor);
                    populateInstructorCards();
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
     * Creates a new instructor.
     * @param name the name of the instructor
     * @param phone the phone number of the instructor
     * @param email the email of the instructor
     * @throws InterruptedException
     */

    private void createNewInstructor(String name, String phone, String email) throws InterruptedException, ExecutionException {
        repository = new Repository(getApplication());
        Instructor instructor = new Instructor(0, 0, name, phone, email);

        long generatedID = repository.insert(instructor);

        if (generatedID <= 0 ) { return; }

        instructor.setInstructorID((int) generatedID);
        repository.insert(instructor);
        instructors.add(instructor);
    }

    /**
     * Modifies an instructor
     * @param instructor the instructor to be modified
     * @param name the name of the instructor
     * @param phone the phone number of the instructor
     * @param email the email of the instructor
     * @throws ExecutionException
     * @throws InterruptedException
     */

    private void modifyInstructor(Instructor instructor, String name, String phone, String email) throws ExecutionException, InterruptedException {
        repository = new Repository(getApplication());
        Instructor newInstructor = new Instructor(instructor.getInstructorID(), instructor.getCourseID(), name, phone, email);
        newInstructor.setInstructorID(instructor.getInstructorID());

        repository.update(newInstructor);
        instructors.remove(instructor);
        instructors.add(newInstructor);
    }

    /**
     * Deletes a given instructor
     * @param instructor the instructor to be removed
     * @throws ExecutionException
     * @throws InterruptedException
     */

    private void deleteInstructor(Instructor instructor) throws ExecutionException, InterruptedException {
        repository = new Repository(getApplication());
        repository.delete(instructor);
        instructors.remove(instructor);
    }

    /**
     * Creates the button for the instructor.
     * @param instructor the instructor to have the button created for
     */

    private void createInstructorButton(Instructor instructor, int selectedInstructorID) throws InterruptedException {
        LinearLayout parentLayout = findViewById(R.id.instructor_list_layout);
        parentLayout.setPadding(parentLayout.getPaddingLeft(),
                parentLayout.getPaddingTop(),
                parentLayout.getPaddingRight(),
                (instructors.size()*36));

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
        instructorButton.setText(instructor.getInstructorName());
        instructorButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        instructorButton.setBackgroundColor(ContextCompat.getColor(this, R.color.tertiary));
        instructorButton.setTextColor(ContextCompat.getColor(this, R.color.primary_variant));

        // Create expandable section layout
        LinearLayout expandableLayout = new LinearLayout(this);
        expandableLayout.setOrientation(LinearLayout.VERTICAL);
        expandableLayout.setVisibility(instructor.getInstructorID() == selectedInstructorID ? View.VISIBLE : View.GONE);
        expandableLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        expandableLayout.setPadding(16, 16, 16, 16);

        // Add details to the expandable section
        TextView emailTextView = new TextView(this);
        emailTextView.setText("Email: " + instructor.getInstructorEmail());
        expandableLayout.addView(emailTextView);

        TextView phoneTextView = new TextView(this);
        phoneTextView.setText("Phone: " + instructor.getInstructorPhone());
        expandableLayout.addView(phoneTextView);

        // Should show a list of courses
        TextView assignedCoursesView = new TextView(this);
        assignedCoursesView.setText("\nAssigned courses: ");
        expandableLayout.addView(assignedCoursesView);

        // Button click listener to toggle expandable layout
        instructorButton.setOnClickListener(v -> {
            boolean isExpanded = expandableLayout.getVisibility() == View.VISIBLE;
            expandableLayout.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            expandedStates.put(instructor.getInstructorID(), !isExpanded);

            parentLayout.post(() -> {
                parentLayout.requestLayout();
                parentLayout.invalidate();
            });
        });

        instructorButton.setOnLongClickListener(v -> {
            openInputDialog(instructor);
            return true;
        });

        List<Course> assignedCourses = repository.getAllInstructorCourses(instructor);
        if (assignedCourses != null) {
            if (!assignedCourses.isEmpty()) {
                for (Course course : assignedCourses) {
                    Button c = new Button(this);
                    c.setText(course.getCourseName());
                    expandableLayout.addView(c);
                    c.setOnClickListener(v -> {
                        Intent intent = new Intent(InstructorsActivity.this, CoursesActivity.class);
                        intent.putExtra("courseID", course.getCourseID());
                        startActivity(intent);
                    });
                }
            } else {
                TextView noCourses = new TextView(this);
                noCourses.setText("Not assigned to any courses!");
                expandableLayout.addView(noCourses);
            }
        }

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

    private void populateInstructorCards() throws InterruptedException {
        if (instructorLayout != null) {
            instructorLayout.removeAllViews();
        }

        Intent intent = getIntent();
        int instructorID = intent.getIntExtra("instructorID", -1);

        for (Instructor instructor : instructors) {
            createInstructorButton(instructor, instructorID);
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
     * Saves the expanded state of the instructor cards.
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
            populateInstructorCards();
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
