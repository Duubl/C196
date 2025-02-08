package com.duubl.c196;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

        progressText.setText(completedCount + " Completed / " + totalCourses + " Total");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}