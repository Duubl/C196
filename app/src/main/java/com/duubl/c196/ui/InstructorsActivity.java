package com.duubl.c196.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

        instructor_layout = findViewById(R.id.instructor_list_layout);
        instructor_layout.setOrientation(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                ? LinearLayout.HORIZONTAL
                : LinearLayout.VERTICAL);

        new_instructor_button = findViewById(R.id.new_instructor_button);
        new_instructor_button.setOnClickListener(item -> {
            openInputDialog();
        });

        repository = new Repository(getApplication());
        try {
            instructors = repository.getAllInstructors();
        } catch (InterruptedException e) {
            Log.e("InstructorsActivity", "there are no instructors!");
            throw new RuntimeException(e);
        }
        for (Instructor instructor : instructors) {
            createInstructorButton(instructor.getInstructor_name());
        }
    }

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
            createInstructorButton(instructorName);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void createNewInstructor(String name, String phone, String email) throws InterruptedException {
        repository = new Repository(getApplication());
        Instructor instructor = new Instructor(0, 0, name, phone, email);
        repository.insert(instructor);
    }

    private void createInstructorButton(String instructorName) {
        instructor_layout = findViewById(R.id.instructor_list_layout);
        Button instructorButton = new Button(this);
        instructorButton.setText(instructorName);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        if (instructor_layout.getOrientation() == LinearLayout.HORIZONTAL) {
            params = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
            );
        }

        instructorButton.setLayoutParams(params);

        instructorButton.setOnClickListener(v -> {
            // TODO: Change toast. Temporary to show button click working.
            Toast.makeText(this, "Clicked: " + instructorName, Toast.LENGTH_SHORT).show();
        });

        instructor_layout.addView(instructorButton);
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
