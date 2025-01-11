package com.duubl.c196.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.duubl.c196.R;
import com.duubl.c196.entities.Term;

import java.util.ArrayList;
import java.util.Calendar;

public class TermsActivity extends AppCompatActivity {

    private Button new_term_button;
    private LinearLayout terms_layout;
    private ArrayList<Term> terms = new ArrayList<Term>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        new_term_button = findViewById(R.id.new_term_button);
        new_term_button.setOnClickListener(item -> {
            openInputDialog();
        });
    }

    /**
     * Opens the input dialog for the new term information.
     * Accepts input for a name, start, end dates and a list of courses.
     */

    // TODO: Add the ability to add courses to the term

    private void openInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Term");

        LinearLayout inputLayout = new LinearLayout(this);
        inputLayout.setOrientation(LinearLayout.VERTICAL);
        inputLayout.setPadding(16, 16, 16, 16);

        // Get term name
        final EditText termInput = new EditText(this);
        termInput.setHint("Enter term name");
        inputLayout.addView(termInput);

        // Get start date
        final TextView startDateText = new TextView(this);
        startDateText.setText("Start Date:");
        inputLayout.addView(startDateText);

        final Button startDateButton = new Button(this);
        startDateButton.setText("Select Start Date");
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

        // Get end date
        final TextView endDateText = new TextView(this);
        endDateText.setText("End Date:");
        inputLayout.addView(endDateText);

        final Button endDateButton = new Button(this);
        endDateButton.setText("Select End Date");
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

        builder.setView(inputLayout);

        // Creates the button on submit if all fields contain information.
        // TODO: Add error checking and proper formatting checking
        builder.setPositiveButton("Add", (dialog, which) -> {
            String termName = termInput.getText().toString().trim();
            if (termName.isEmpty() || startDate[0] == null || endDate[0] == null) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            createTermButton(termName);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void createTermButton(String termName) {
        // TODO: Create term buttons for each term already stored in the database
        terms_layout = findViewById(R.id.terms_list_layout);
        Button termButton = new Button(this);
        termButton.setText(termName);
        termButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        termButton.setOnClickListener(v -> {
            // TODO: Change toast. Temporary to show button click working.
            Toast.makeText(this, "Clicked: " + termName, Toast.LENGTH_SHORT).show();
        });

        terms_layout.addView(termButton);
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
