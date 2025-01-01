package com.example.fitapp.ui.exercise;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitapp.R;
import com.example.fitapp.javahelperfile.exercise.Exercise;
import com.example.fitapp.javahelperfile.exercise.TodayActivityAdapter;
import com.example.fitapp.javahelperfile.profile.User;
import com.example.fitapp.ui.admin.AdminFeatures;
import com.example.fitapp.ui.admin.AdminProfile;
import com.example.fitapp.ui.analytics.Analytics;
import com.example.fitapp.ui.profile.Profile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TodayActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TodayActivityAdapter adapter;
    private List<Exercise> userExercises = new ArrayList<>();
    private User user;
    private String mode = "activity_today"; // Default mode is "activity_today"
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        // Retrieve user from Intent
        user = getIntent().getParcelableExtra("user");

        if (user == null) {
            Toast.makeText(this, "Error: User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        // Check if the user is an admin
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        if (isAdmin) {
            // Change BottomNavigationMenu background color to purple
            bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.purple)); // Replace with your purple color resource
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
                return true;
            } else if (item.getItemId() == R.id.bottom_analytics) {
                Intent intent;
                if (isAdmin) {
                    // Navigate to AdminFeatures for admin users
                    intent = new Intent(getApplicationContext(), AdminFeatures.class);
                } else {
                    // Navigate to Analytics for regular users
                    intent = new Intent(getApplicationContext(), Analytics.class);
                }
                intent.putExtra("isAdmin", isAdmin); // Pass the isAdmin flag
                intent.putExtra("user", user); // Attach the Parcelable User object
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (item.getItemId() == R.id.bottom_profile) {
                Intent intent;
                if (isAdmin) {
                    // Navigate to AdminProfile for admin users
                    intent = new Intent(getApplicationContext(), AdminProfile.class);
                } else {
                    // Navigate to Profile for regular users
                    intent = new Intent(getApplicationContext(), Profile.class);
                }
                intent.putExtra("isAdmin", isAdmin); // Pass the isAdmin flag
                intent.putExtra("user", user); // Attach the Parcelable User object
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });
        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.RVTodayActivity);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new TodayActivityAdapter(this, userExercises, mode);
        recyclerView.setAdapter(adapter);

        // Set up the back button
        ImageButton buttonBack = findViewById(R.id.ButtonBack);
        buttonBack.setOnClickListener(view -> finish());

        // Fetch user's added exercises from Firebase
        fetchUserExercisesFromFirebase();

        // Edit/Confirm button click listener
        Button buttonEditConfirm = findViewById(R.id.BtnEditConfirm);
        buttonEditConfirm.setOnClickListener(view -> toggleMode());
    }

    private void fetchUserExercisesFromFirebase() {
        if (user == null || user.getUserId() == null) {
            Toast.makeText(this, "Error: User is null.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userExerciseListRef = FirebaseDatabase.getInstance().getReference("User_Exercise_List")
                .child(user.getUserId());

        userExerciseListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userExercises.clear(); // Clear the list to avoid duplicates
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String exerciseId = snapshot.getKey(); // Get exercise ID
                    fetchExerciseDetails(exerciseId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(TodayActivity.this, "Failed to load exercises.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchExerciseDetails(String exerciseId) {
        DatabaseReference exercisesRef = FirebaseDatabase.getInstance().getReference("Exercises").child(exerciseId);

        exercisesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Exercise exercise = dataSnapshot.getValue(Exercise.class);
                if (exercise != null) {
                    userExercises.add(exercise);
                    adapter.notifyDataSetChanged(); // Notify adapter of data changes
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(TodayActivity.this, "Failed to load exercise details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleMode() {
        // Toggle between edit and view mode
        mode = mode.equals("activity_today") ? "activity_today_edit" : "activity_today";
        adapter.updateMode(mode); // Update the mode in the adapter

        // Change button text to reflect the current mode
        Button buttonEditConfirm = findViewById(R.id.BtnEditConfirm);
        if (mode.equals("activity_today")) {
            buttonEditConfirm.setText("Edit");
        } else {
            buttonEditConfirm.setText("Confirm");
        }
    }

    // Method to delete an exercise from the "Today Activity" list and database
    public void onDeleteExercise(Exercise exercise) {
        // Remove from the local list
        userExercises.remove(exercise);
        adapter.notifyDataSetChanged(); // Update the RecyclerView

        // Remove from Firebase
        if (user != null && user.getUserId() != null) {
            DatabaseReference userExerciseListRef = FirebaseDatabase.getInstance().getReference("User_Exercise_List")
                    .child(user.getUserId()).child(exercise.getExerciseId());
            userExerciseListRef.removeValue()
                    .addOnSuccessListener(aVoid -> Toast.makeText(TodayActivity.this, "Exercise deleted.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(TodayActivity.this, "Failed to delete exercise.", Toast.LENGTH_SHORT).show());
        }
    }
}
