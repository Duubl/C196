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
import com.duubl.c196.entities.Course;
import com.duubl.c196.entities.Instructor;
import com.duubl.c196.entities.Status;
import com.duubl.c196.entities.Term;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TermsActivity extends AppCompatActivity {

    private Button newTermButton;
    private LinearLayout termLayout;
    private Repository repository;
    private List<Term> terms;

    // HashMap to store the expanded states of the cards. Helpful for changing orientation of the phone.
    private HashMap<Integer, Boolean> expandedStates = new HashMap<>();

    /**
     * Creates the activity. Adds the toolbar and populates the activity with the term cards.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Layout for the list of courses.
        termLayout = findViewById(R.id.term_list_layout);
        if (termLayout == null) {
            Log.e("TermsActivity", "termLayout is null!");
            return;
        }

        newTermButton = findViewById(R.id.new_term_button);
        newTermButton.setOnClickListener(item -> {
            openInputDialog();
        });

        // Loads the courses into the activity on creation
        repository = new Repository(getApplication());
        try {
            terms = repository.getAllTerms();
        } catch (InterruptedException | ExecutionException e) {
            Log.e("TermsActivity", "there are no terms!");
            throw new RuntimeException(e);
        }
        try {
            populateTermCards();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Opens the input dialog for the new term information.
     * Accepts input for a name, start, end dates and a list of courses.
     */

    private void openInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Term");

        ArrayList<Course> assignedCourses = new ArrayList<>();

        LinearLayout inputLayout = new LinearLayout(this);
        inputLayout.setOrientation(LinearLayout.VERTICAL);
        inputLayout.setPadding(16, 16, 16, 16);

        final EditText termInput = new EditText(this);
        termInput.setHint("Enter term name");
        inputLayout.addView(termInput);

        final Button startDateButton = new Button(this);
        startDateButton.setText("Select Start Date");
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
        endDateButton.setText("Select End Date");
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

        final Button newTermCourseButton = new Button(this);
        newTermCourseButton.setText("Assign Courses");
        inputLayout.addView(newTermCourseButton);
        newTermCourseButton.setOnClickListener(v -> {
            List<Course> courses;
            try {
                courses = repository.getAllCourses();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            String[] courseOptions = new String[courses.size()];
            for (int i = 0; i < courses.size(); i++) {
                courseOptions[i] = courses.get(i).getCourseName();
            }

            boolean[] selectedItems = new boolean[courses.size()];

            new AlertDialog.Builder(this)
                    .setTitle("Assign Courses")
                    .setMultiChoiceItems(courseOptions, selectedItems, (dialog, which, isChecked) -> {
                        selectedItems[which] = isChecked;
                    })
                    .setPositiveButton("Save", (dialog, which) -> {
                        for (int i = 0; i < selectedItems.length; i++) {
                            if (selectedItems[i]) {
                                if (!assignedCourses.contains(courses.get(i))) {
                                    assignedCourses.add(courses.get(i));
                                    Log.d("TermsActivity", "Assigned course: " + courses.get(i).getCourseName());
                                }
                            }
                        }

                        if (!assignedCourses.isEmpty()) {
                            StringBuilder buttonText = new StringBuilder("Assigned Courses:\n");
                            for (Course assignedCourse : assignedCourses) {
                                buttonText.append(assignedCourse.getCourseName()).append(",\n");
                            }
                            buttonText.setLength(buttonText.length() - 2);
                            newTermCourseButton.setText(buttonText.toString());
                        } else {
                            newTermCourseButton.setText("Assign Courses");
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        });

        builder.setView(inputLayout);

        // Creates the button on submit if all fields contain information.
        // TODO: Add error checking and proper formatting checking
        builder.setPositiveButton("Add", (dialog, which) -> {
            String termName = termInput.getText().toString().trim();
            if (termName.isEmpty() || localStartDate[0] == null || localEndDate[0] == null) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                createNewTerm(termName, localStartDate[0], localEndDate[0], assignedCourses);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            try {
                populateTermCards();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Opens the input dialog for the new term information.
     * Accepts input for a name, start, end dates and a list of courses.
     */

    private void openInputDialog(Term term) throws ExecutionException, InterruptedException {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modify Term");

        List<Course> assignedCourses = repository.getAllTermCourses(term);

        LinearLayout inputLayout = new LinearLayout(this);
        inputLayout.setOrientation(LinearLayout.VERTICAL);
        inputLayout.setPadding(16, 16, 16, 16);

        final EditText termInput = new EditText(this);
        termInput.setHint("Term Name");
        termInput.setText(term.getTermName());
        inputLayout.addView(termInput);

        final Button startDateButton = new Button(this);
        startDateButton.setText(term.getStartDate().toString());
        inputLayout.addView(startDateButton);

        final LocalDate[] localStartDate = new LocalDate[1];
        localStartDate[0] = term.getStartDate();
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
        endDateButton.setText(term.getEndDate().toString());
        inputLayout.addView(endDateButton);

        final LocalDate[] localEndDate = new LocalDate[1];
        localEndDate[0] = term.getEndDate();
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

        // Creates the new term course button
        final Button newTermCourseButton = new Button(this);

        // Changes the name depending on which courses are assigned to the term
        StringBuilder newTermCourseButtonText = new StringBuilder("Assigned Courses:\n");
        for (Course assignedCourse : assignedCourses) {
            newTermCourseButtonText.append(assignedCourse.getCourseName()).append(",\n");
        }
        newTermCourseButtonText.setLength(newTermCourseButtonText.length() - 2);
        newTermCourseButton.setText(newTermCourseButtonText.toString());

        inputLayout.addView(newTermCourseButton);

        newTermCourseButton.setOnClickListener(v -> {
            List<Course> courses;
            try {
                courses = repository.getAllCourses();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            String[] courseOptions = new String[courses.size()];
            for (int i = 0; i < courses.size(); i++) {
                courseOptions[i] = courses.get(i).getCourseName();
            }


            // Pre-selects the already assigned courses.
            boolean[] selectedItems = new boolean[courseOptions.length];

            for (int i = 0; i < courses.size(); i++) {
                if (assignedCourses.contains(courses.get(i))) {
                    selectedItems[i] = true;
                }
            }

            new AlertDialog.Builder(this)
                    .setTitle("Assign Courses")
                    .setMultiChoiceItems(courseOptions, selectedItems, (dialog, which, isChecked) -> {
                        selectedItems[which] = isChecked;
                    })
                    .setPositiveButton("Save", (dialog, which) -> {
                        assignedCourses.clear();
                        for (int i = 0; i < selectedItems.length; i++) {
                            if (selectedItems[i]) {
                                if (!assignedCourses.contains(courses.get(i))) {
                                    assignedCourses.add(courses.get(i));
                                    Log.d("TermsActivity", "Assigned course: " + courses.get(i).getCourseName());
                                }
                            }
                        }

                        if (!assignedCourses.isEmpty()) {
                            StringBuilder buttonText = new StringBuilder("Assigned Courses:\n");
                            for (Course assignedCourse : assignedCourses) {
                                buttonText.append(assignedCourse.getCourseName()).append(",\n");
                            }
                            buttonText.setLength(buttonText.length() - 2);
                            newTermCourseButton.setText(buttonText.toString());
                        } else {
                            newTermCourseButton.setText("Assign Courses");
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        });

        builder.setView(inputLayout);

        // Creates the button on submit if all fields contain information.
        // TODO: Add error checking and proper formatting checking
        builder.setPositiveButton("Update Term", (dialog, which) -> {
            String termName = termInput.getText().toString().trim();
            if (termName.isEmpty() || localStartDate[0] == null || localEndDate[0] == null) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                modifyTerm(term, termName, localStartDate[0], localEndDate[0], assignedCourses);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            try {
                populateTermCards();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Creates a new term.
     * @param name the name of the term.
     * @param startDate the start date of the term.
     * @param endDate the end date of the term.
     * @param courses the courses to be added to the term.
     * @throws InterruptedException
     */

    private void createNewTerm(String name, LocalDate startDate, LocalDate endDate, List<Course> courses) throws InterruptedException, ExecutionException {
        repository = new Repository(getApplication());

        Term term = new Term(0, name, startDate, endDate);

        long generatedID = repository.insert(term);

        if (generatedID <= 0) { return; }

        term.setTermID((int) generatedID);

        for (Course course : courses) {
            course.setTermID(term.getTermID());
            repository.update(course);
        }
        terms.add(term);
    }

    /**
     * Modifies a term
     * @param term the term being modified
     * @param name the new name for the term
     * @param startDate the new start date for the term
     * @param endDate the new end date for the term
     * @param courses the list of courses currently assigned to the term
     * @throws ExecutionException
     * @throws InterruptedException
     */

    private void modifyTerm(Term term, String name, LocalDate startDate, LocalDate endDate, List<Course> courses) throws ExecutionException, InterruptedException {
        repository = new Repository(getApplication());

        List<Course> allCourses = repository.getAllCourses();
        List<Course> allTermCourses = repository.getAllTermCourses(term);

        Term newTerm = new Term(term.getTermID(), name, startDate, endDate);
        newTerm.setTermID(term.getTermID());

        for (Course course : allTermCourses) {
            if (!courses.contains(course) && allTermCourses.contains(course)) {
                course.setTermID(0);
            }
            repository.update(course);
        }

        for (Course course : allCourses) {
            if (courses.contains(course)) {
                course.setTermID(term.getTermID());
            }
            repository.update(course);
        }

        repository.update(newTerm);
        terms.remove(term);
        terms.add(newTerm);
    }

    /**
     * Creates a new term button.
     * @param term the term which the button is being created for.
     * @throws InterruptedException
     * @throws ExecutionException
     */

    private void createTermButton(Term term) throws InterruptedException, ExecutionException {
        LinearLayout parentLayout = findViewById(R.id.term_list_layout);
        parentLayout.setPadding(parentLayout.getPaddingLeft(),
                parentLayout.getPaddingTop(),
                parentLayout.getPaddingRight(),
                (terms.size()*36));

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
        Button termButton = new Button(this);
        termButton.setText(term.getTermName());
        termButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        termButton.setBackgroundColor(ContextCompat.getColor(this, R.color.tertiary));
        termButton.setTextColor(ContextCompat.getColor(this, R.color.primary_variant));

        // Create expandable section layout
        LinearLayout expandableLayout = new LinearLayout(this);
        expandableLayout.setOrientation(LinearLayout.VERTICAL);
        expandableLayout.setVisibility(expandedStates.getOrDefault(term.getTermID(), false) ? View.VISIBLE : View.GONE);
        expandableLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        expandableLayout.setPadding(16, 16, 16, 16);

        // Add details to the expandable section
        TextView startTextView = new TextView(this);
        startTextView.setText("Start Date: " + term.getStartDate());
        expandableLayout.addView(startTextView);

        TextView endTextView = new TextView(this);
        endTextView.setText("End Date: " + term.getEndDate());
        expandableLayout.addView(endTextView);

        TextView assignedInstructorView = new TextView(this);
        assignedInstructorView.setText("\nAssigned Courses: ");
        expandableLayout.addView(assignedInstructorView);

        List<Course> assignedCourses = repository.getAllTermCourses(term);
        if (!assignedCourses.isEmpty()) {
            for (Course course : assignedCourses) {
                Button c = new Button(this);
                c.setText(course.getCourseName());
                expandableLayout.addView(c);
                c.setOnClickListener(v -> {
                    startActivity(new Intent(getApplicationContext(), CoursesActivity.class));
                });
            }
        }

        // Button click listener to toggle expandable layout
        termButton.setOnClickListener(v -> {
            boolean isExpanded = expandableLayout.getVisibility() == View.VISIBLE;
            expandableLayout.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            expandedStates.put(term.getTermID(), !isExpanded);

            parentLayout.post(() -> {
                parentLayout.requestLayout();
                parentLayout.invalidate();
            });
        });

        termButton.setOnLongClickListener(v -> {
            try {
                openInputDialog(term);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            return true;
        });

        // Add button and expandable layout to the card layout
        cardLayout.addView(termButton);
        cardLayout.addView(expandableLayout);

        // Add card layout to the card view
        cardView.addView(cardLayout);

        // Add the card view to the parent layout
        parentLayout.addView(cardView);
    }

    /**
     * Popuates the term cards in the activity.
     */

    private void populateTermCards() throws InterruptedException, ExecutionException {
        if (termLayout != null) {
            termLayout.removeAllViews();
        }

        for (Term term : terms) {
            createTermButton(term);
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
            populateTermCards();
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
