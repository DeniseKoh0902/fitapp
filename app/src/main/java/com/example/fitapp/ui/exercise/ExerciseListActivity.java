package com.example.fitapp.ui.exercise;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitapp.R;
import com.example.fitapp.javahelperfile.exercise.Exercise;
import com.example.fitapp.javahelperfile.exercise.ExerciseAdapter;
import com.example.fitapp.javahelperfile.profile.User;
import com.example.fitapp.ui.admin.AdminFeatures;
import com.example.fitapp.ui.admin.AdminProfile;
import com.example.fitapp.ui.analytics.Analytics;
import com.example.fitapp.ui.home.MainActivity;
import com.example.fitapp.ui.profile.Profile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExerciseListActivity extends AppCompatActivity {
    private User user;
    private TextView titleTextView, subtitleTextView;
    private RecyclerView recyclerView;
    private ExerciseAdapter adapter;
    private List<Exercise> exerciseList = new ArrayList<>();
    private String category;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        // Retrieve the User object
        user = getIntent().getParcelableExtra("user");

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
        // Bind views
        titleTextView = findViewById(R.id.TVExerciseTitle);
        subtitleTextView = findViewById(R.id.TVExerciseSubTitle);
        recyclerView = findViewById(R.id.RVTodayActivity);
        ImageButton buttonBack = findViewById(R.id.ButtonBack);

        // Navigate back to the home page when the back button is pressed
        buttonBack.setOnClickListener(view -> finish());

        // Get category from Intent
        category = getIntent().getStringExtra("category");

        // Set title and subtitle based on category
        updateTitleAndSubtitle();

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ExerciseAdapter(user, this, exerciseList, category);
        recyclerView.setAdapter(adapter);

        // Fetch exercises from Firebase
        fetchExercisesFromFirebase();
    }

    private void updateTitleAndSubtitle() {
        switch (category) {
            case "Aerobic":
                titleTextView.setText("Aerobic Exercise");
                subtitleTextView.setText("Boost your heart health and stamina!");
                break;
            case "Flexibility":
                titleTextView.setText("Flexibility Exercise");
                subtitleTextView.setText("Stretch your limits for a more agile you!");
                break;
            case "Balance":
                titleTextView.setText("Balance Exercise");
                subtitleTextView.setText("Strengthen your core for a more balanced life!");
                break;
            case "HIIT":
                titleTextView.setText("High-Intensity Interval Training");
                subtitleTextView.setText("Push your limits and torch calories fast!");
                break;
            case "Strength":
                titleTextView.setText("Strength Training");
                subtitleTextView.setText("Lift, push, and conquer for power and confidence!");
                break;
            default:
                titleTextView.setText("Exercise");
                subtitleTextView.setText("");
                break;
        }
    }

    private void fetchExercisesFromFirebase() {
        DatabaseReference exercisesRef = FirebaseDatabase.getInstance().getReference("Exercises");

        // Real-time listener for exercises
        exercisesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                exerciseList.clear(); // Clear the list to prevent duplicates
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Exercise exercise = snapshot.getValue(Exercise.class);
                    if (exercise != null && exercise.getExerciseCategory().equals(category)) {
                        exerciseList.add(exercise);
                    }
                }
                adapter.notifyDataSetChanged(); // Notify adapter of data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ExerciseListActivity", "Error fetching data: " + databaseError.getMessage());
            }
        });
    }

    // This function is triggered by the adapter when the user clicks the "Add" button
    public void onAddExercise(Exercise exercise) {
        addExerciseToUserList(exercise);
    }

    private void addExerciseToUserList(Exercise exercise) {
        // Ensure the user is not null
        if (user == null) {
            Log.e("ExerciseListActivity", "Current user is null.");
            return;
        }

        // Get Firebase reference for User_Exercise_List
        DatabaseReference userExerciseListRef = FirebaseDatabase.getInstance().getReference("User_Exercise_List")
                .child(user.getUserId()); // Store under the userId

        // Check if exercise already exists for the user
        userExerciseListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Check if the exercise already exists in the list
                    if (dataSnapshot.hasChild(exercise.getExerciseId())) {
                        // Exercise already added
                        Toast.makeText(ExerciseListActivity.this, "Exercise already in your list.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Exercise not in list, add it with exercise_id as key
                        userExerciseListRef.child(exercise.getExerciseId()).setValue(true) // Store the exercise_id as a key
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("ExerciseListActivity", "Exercise added to User_Exercise_List.");
                                    Toast.makeText(ExerciseListActivity.this, "Exercise added to your list!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("ExerciseListActivity", "Failed to add exercise: " + e.getMessage());
                                    Toast.makeText(ExerciseListActivity.this, "Failed to add exercise: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                } else {
                    // If no exercise list exists, create a new one with the selected exercise_id
                    userExerciseListRef.child(exercise.getExerciseId()).setValue(true)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("ExerciseListActivity", "Exercise added to User_Exercise_List.");
                                Toast.makeText(ExerciseListActivity.this, "Exercise added to your list!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("ExerciseListActivity", "Failed to add exercise: " + e.getMessage());
                                Toast.makeText(ExerciseListActivity.this, "Failed to add exercise: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ExerciseListActivity", "Error checking for existing exercise: " + databaseError.getMessage());
            }
        });
    }


}
