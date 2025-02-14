package com.duubl.c196;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.duubl.c196.entities.Course;
import com.duubl.c196.entities.Status;
import com.duubl.c196.entities.Term;
import com.duubl.c196.ui.AssessmentsActivity;
import com.duubl.c196.ui.CoursesActivity;
import com.duubl.c196.ui.InstructorsActivity;
import com.duubl.c196.ui.TermsActivity;
import com.google.android.material.navigation.NavigationView;

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
     * Creates a notification channel. Required for API 26+.
     */

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            String channel_id = getString(R.string.channel_id);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channel_id, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}