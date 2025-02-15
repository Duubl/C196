package com.duubl.c196;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.duubl.c196.database.Repository;
import com.duubl.c196.entities.Assessment;
import com.duubl.c196.entities.AssessmentType;
import com.duubl.c196.entities.Course;
import com.duubl.c196.entities.Instructor;
import com.duubl.c196.entities.Status;
import com.duubl.c196.entities.Term;
import com.duubl.c196.ui.AssessmentsActivity;
import com.duubl.c196.ui.CoursesActivity;
import com.duubl.c196.ui.InstructorsActivity;
import com.duubl.c196.ui.TermsActivity;
import com.google.android.material.navigation.NavigationView;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private Repository repository;
    private LinearLayout termLayout;
    private List<Term> terms;
    private List<Course> courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // Layout for the list of courses.
        termLayout = findViewById(R.id.terms_container);
        if (termLayout == null) {
            Log.e("MainActivity", "termLayout is null!");
            return;
        }

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else if (itemId == R.id.nav_terms) {
                startActivity(new Intent(getApplicationContext(), TermsActivity.class));
            } else if (itemId == R.id.nav_courses) {
                startActivity(new Intent(getApplicationContext(), CoursesActivity.class));
            } else if (itemId == R.id.nav_instructors) {
                startActivity(new Intent(getApplicationContext(), InstructorsActivity.class));
            } else if (itemId == R.id.nav_assessments) {
                startActivity(new Intent(getApplicationContext(), AssessmentsActivity.class));
            } else {
                Log.e("NavigationDrawer", "No Intent created for the selected item");
            }
            drawerLayout.closeDrawers();
            return true;
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        repository = new Repository(getApplication());
        try {
            courses = repository.getAllCourses();
            terms = repository.getAllTerms();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        TextView progressText = findViewById(R.id.course_counter);
        int completedCount = 0;
        for (Course course : courses) {
            if (course.getStatus() == Status.COMPLETED) {
                completedCount++;
            }
        }
        int totalCourses = courses.size();

        if (completedCount == 1) {
            progressText.setText(completedCount + " Course Completed / " + totalCourses + " Courses Total");
        } else {
            progressText.setText(completedCount + " Courses Completed / " + totalCourses + " Courses Total");
        }
        try {
            populateTermCards();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new term button.
     * @param term the term the button is created for.
     * @throws InterruptedException
     * @throws ExecutionException
     */

    private void createTermButton(Term term) throws InterruptedException, ExecutionException {
        LinearLayout parentLayout = findViewById(R.id.terms_container);

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

        int completedCount = 0;
        List<Course> termCourses = repository.getAllTermCourses(term);
        for (Course course : termCourses) {
            if (course.getStatus() == Status.COMPLETED) {
                completedCount++;
            }
        }
        int totalCourses = termCourses.size();

        // Create button
        Button termButton = new Button(this);
        termButton.setText(term.getTermName() + "\n" + completedCount + " of " + totalCourses + " courses completed");
        termButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        termButton.setBackgroundColor(ContextCompat.getColor(this, R.color.tertiary));
        termButton.setTextColor(ContextCompat.getColor(this, R.color.primary_variant));

        termButton.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), TermsActivity.class));
        });

        // Add button and expandable layout to the card layout
        cardLayout.addView(termButton);

        // Add card layout to the card view
        cardView.addView(cardLayout);

        // Add the card view to the parent layout
        parentLayout.addView(cardView);
    }

    /**
     * Prepopulates term, course, assessment and instructor data
     */

    private void prepopulateData() throws ExecutionException, InterruptedException {
        repository = new Repository(getApplication());

        // Test term 1 with courses
        Term term1 = new Term(1,
                "Test Term 1",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 3, 2));
        Course course1 = new Course(1,
                1,
                "Test Course 1",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 2),
                Status.COMPLETED);
        Course course2 = new Course(2,
                1,
                "Test Course 2",
                LocalDate.of(2025, 2, 1),
                LocalDate.of(2025, 2, 2),
                Status.COMPLETED);
        Course course3 = new Course(3,
                1,
                "Test Course 3",
                LocalDate.of(2025, 3, 1),
                LocalDate.of(2025, 3, 2),
                Status.IN_PROGRESS);
        Assessment assessment1 = new Assessment(1,
                "Test Assessment 1",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 2),
                AssessmentType.OBJECTIVE);
        Assessment assessment2 = new Assessment(2,
                "Test Assessment 2",
                LocalDate.of(2025, 2, 1),
                LocalDate.of(2025, 2, 2),
                AssessmentType.PERFORMANCE);
        Assessment assessment3 = new Assessment(3,
                "Test Assessment 3",
                LocalDate.of(2025, 3, 1),
                LocalDate.of(2025, 3, 2),
                AssessmentType.PERFORMANCE);
        assessment1.setCourseID(1);
        assessment2.setCourseID(2);
        assessment3.setCourseID(3);
        Instructor instructor1 = new Instructor(1,
                1,
                "Xi Xinping",
                "111-111-1111",
                "Xi@gmail.com");
        Instructor instructor2 = new Instructor(2,
                2,
                "John Cena",
                "222-222-2222",
                "John@gmail.com");
        Instructor instructor3 = new Instructor(3,
                3,
                "Ronnie Coleman",
                "333-333-3333",
                "yeahbuddy@gmail.com");

        // Test term 2 with courses
        Term term2 = new Term(2,
                "Test Term 2",
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 6, 2));
        Course course4 = new Course(4,
                2,
                "Test Course 4",
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 2),
                Status.IN_PROGRESS);
        Course course5 = new Course(5,
                2,
                "Test Course 5",
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 5, 2),
                Status.PLAN_TO_TAKE);
        Course course6 = new Course(6,
                2,
                "Test Course 6",
                LocalDate.of(2025, 6, 1),
                LocalDate.of(2025, 6, 2),
                Status.PLAN_TO_TAKE);
        Assessment assessment4 = new Assessment(4,
                "Test Assessment 4",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 2),
                AssessmentType.OBJECTIVE);
        Assessment assessment5 = new Assessment(5,
                "Test Assessment 5",
                LocalDate.of(2025, 2, 1),
                LocalDate.of(2025, 2, 2),
                AssessmentType.PERFORMANCE);
        Assessment assessment6 = new Assessment(6,
                "Test Assessment 6",
                LocalDate.of(2025, 3, 1),
                LocalDate.of(2025, 3, 2),
                AssessmentType.PERFORMANCE);
        assessment4.setCourseID(4);
        assessment5.setCourseID(5);
        assessment6.setCourseID(6);
        Instructor instructor4 = new Instructor(4,
                4,
                "Jay Cutler",
                "555-555-5555",
                "20eggs@gmail.com");
        Instructor instructor5 = new Instructor(5,
                5,
                "Sam Sulek",
                "666-666-6666",
                "backne@gmail.com");
        Instructor instructor6 = new Instructor(6,
                6,
                "Tom Platz",
                "777-777-7777",
                "quadfather@gmail.com");

        repository.insert(term1);
        repository.insert(term2);
        repository.insert(course1);
        repository.insert(course2);
        repository.insert(course3);
        repository.insert(course4);
        repository.insert(course5);
        repository.insert(course6);
        repository.insert(assessment1);
        repository.insert(assessment2);
        repository.insert(assessment3);
        repository.insert(assessment4);
        repository.insert(assessment5);
        repository.insert(assessment6);
        repository.insert(instructor1);
        repository.insert(instructor2);
        repository.insert(instructor3);
        repository.insert(instructor4);
        repository.insert(instructor5);
        repository.insert(instructor6);

        populateTermCards();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.extras_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == R.id.prepopulate) {
            try {
                prepopulateData();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}