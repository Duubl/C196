package com.duubl.c196;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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

import com.duubl.c196.ui.AssessmentsActivity;
import com.duubl.c196.ui.CoursesActivity;
import com.duubl.c196.ui.InstructorsActivity;
import com.duubl.c196.ui.TermsActivity;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

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
            Intent i = null;
            if (itemId == R.id.nav_home) {
                i = new Intent(getApplicationContext(), MainActivity.class);
            } else if (itemId == R.id.nav_terms) {
                i = new Intent(getApplicationContext(), TermsActivity.class);
            } else if (itemId == R.id.nav_courses) {
                i = new Intent(getApplicationContext(), CoursesActivity.class);
            } else if (itemId == R.id.nav_instructors) {
                i = new Intent(getApplicationContext(), InstructorsActivity.class);
            } else if (itemId == R.id.nav_assessments) {
                i = new Intent(getApplicationContext(), AssessmentsActivity.class);
            }
            drawerLayout.closeDrawers();
            if (i != null) {
                startActivity(i);
            } else {
                Log.e("NavigationDrawer", "No Intent created for the selected item");
            }
            return true;
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}