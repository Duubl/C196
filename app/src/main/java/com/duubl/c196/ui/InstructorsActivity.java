package com.duubl.c196.ui;

import android.os.Bundle;
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

public class InstructorsActivity extends AppCompatActivity {

    private Button new_instructor_button;
    private LinearLayout instructor_layout;

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

        new_instructor_button = findViewById(R.id.new_instructor_button);
        new_instructor_button.setOnClickListener(item -> {
            openInputDialog();
        });
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

            createInstructorButton(instructorName);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void createInstructorButton(String instructorName) {
        // TODO: Create instructor buttons for each term already stored in the database
        instructor_layout = findViewById(R.id.instructor_list_layout);
        Button instructorButton = new Button(this);
        instructorButton.setText(instructorName);
        instructorButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // TODO: Populate with information stored in arraylist containing terms
    }
}
