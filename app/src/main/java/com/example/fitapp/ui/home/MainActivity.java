package com.example.fitapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
<<<<<<< HEAD
import android.widget.Button;
=======
>>>>>>> 172b022a49342446412c20a1f92646cf78a14948
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitapp.R;
<<<<<<< HEAD
import com.example.fitapp.javahelperfile.exercise.ExerciseDataUploader;
import com.example.fitapp.javahelperfile.profile.User;
import com.example.fitapp.ui.admin.AdminFeatures;
import com.example.fitapp.ui.admin.AdminProfile;
import com.example.fitapp.ui.analytics.Analytics;
import com.example.fitapp.ui.exercise.ExerciseListActivity;
import com.example.fitapp.ui.exercise.TodayActivity;
=======
import com.example.fitapp.javahelperfile.profile.User;
import com.example.fitapp.ui.analytics.Analytics;
>>>>>>> 172b022a49342446412c20a1f92646cf78a14948
import com.example.fitapp.ui.profile.Profile;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

<<<<<<< HEAD
    boolean isAdmin;

=======
>>>>>>> 172b022a49342446412c20a1f92646cf78a14948
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

<<<<<<< HEAD
        // IMPORTANT !! Only call this ONCE to upload exercise for global list
//        ExerciseDataUploader uploader = new ExerciseDataUploader();
//        uploader.uploadExercises();

=======
>>>>>>> 172b022a49342446412c20a1f92646cf78a14948
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        // Retrieve User object
        User user = getIntent().getParcelableExtra("user");

        if (user != null) {
            // Use the User object data
            TextView welcomeText = findViewById(R.id.welcomeText);
            welcomeText.setText("Welcome, " + user.getUsername());

        }

<<<<<<< HEAD
        // Check if the user is an admin
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        if (isAdmin) {
            // Change BottomNavigationMenu background color to purple
            bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.purple)); // Replace with your purple color resource
        }

=======
>>>>>>> 172b022a49342446412c20a1f92646cf78a14948
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
                return true;
            } else if (item.getItemId() == R.id.bottom_analytics) {
<<<<<<< HEAD
                Intent intent;
                if (isAdmin) {
                    // Navigate to AdminFeatures for admin users
                    intent = new Intent(MainActivity.this, AdminFeatures.class);
                } else {
                    // Navigate to Analytics for regular users
                    intent = new Intent(MainActivity.this, Analytics.class);
                }
                intent.putExtra("isAdmin", isAdmin); // Pass the isAdmin flag
=======
                Intent intent = new Intent(MainActivity.this, Analytics.class);
>>>>>>> 172b022a49342446412c20a1f92646cf78a14948
                intent.putExtra("user", user); // Attach the Parcelable User object
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (item.getItemId() == R.id.bottom_profile) {
<<<<<<< HEAD
                Intent intent;
                if (isAdmin) {
                    // Navigate to AdminProfile for admin users
                    intent = new Intent(MainActivity.this, AdminProfile.class);
                } else {
                    // Navigate to Profile for regular users
                    intent = new Intent(MainActivity.this, Profile.class);
                }
                intent.putExtra("isAdmin", isAdmin); // Pass the isAdmin flag
=======
                Intent intent = new Intent(MainActivity.this, Profile.class);
>>>>>>> 172b022a49342446412c20a1f92646cf78a14948
                intent.putExtra("user", user); // Attach the Parcelable User object
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });


<<<<<<< HEAD
        // Button references
        Button btnAerobic = findViewById(R.id.btnAerobic);
        Button btnFlexibility = findViewById(R.id.btnFlexibility);
        Button btnBalance = findViewById(R.id.btnBalance);
        Button btnHIIT = findViewById(R.id.btnHIIT);
        Button btnStrength = findViewById(R.id.btnStrength);
        Button btnToday = findViewById(R.id.btnTodayActivity);

        // Set click listeners
        btnAerobic.setOnClickListener(v -> openExerciseListActivity(user, "Aerobic"));
        btnFlexibility.setOnClickListener(v -> openExerciseListActivity(user, "Flexibility"));
        btnBalance.setOnClickListener(v -> openExerciseListActivity(user, "Balance"));
        btnHIIT.setOnClickListener(v -> openExerciseListActivity(user, "HIIT"));
        btnStrength.setOnClickListener(v -> openExerciseListActivity(user, "Strength"));

        btnToday.setOnClickListener(v -> openTodayActivityPage(user));

    }

    // Helper method to open ExerciseListActivity and pass User and category
    private void openExerciseListActivity(User user, String category) {
        Intent intent = new Intent(MainActivity.this, ExerciseListActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("isAdmin", isAdmin);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    // Helper method to open TodayActivity and pass User
    private void openTodayActivityPage(User user) {
        Intent intent = new Intent(MainActivity.this, TodayActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("isAdmin", isAdmin);
        intent.putExtra("mode", "activity_today");
        startActivity(intent);
=======
>>>>>>> 172b022a49342446412c20a1f92646cf78a14948
    }
}