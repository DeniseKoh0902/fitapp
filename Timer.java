package com.example.fitapp.ui.home;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fitapp.R;
import com.example.fitapp.javahelperfile.exercise.Exercise;
import com.example.fitapp.javahelperfile.exercise.TodayActivityAdapter;
import com.example.fitapp.javahelperfile.profile.User;
import com.example.fitapp.ui.admin.AdminFeatures;
import com.example.fitapp.ui.admin.AdminProfile;
import com.example.fitapp.ui.analytics.Analytics;
import com.example.fitapp.ui.profile.Profile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Timer extends AppCompatActivity {

    boolean isAdmin;
    private User user;
    private String mode = "";
    private TextView activityName, activityDescription, activityTimer;
    private Button startButton, resetButton;
    private ImageView activityImage;
    private CountDownTimer countDownTimer;

    private boolean isTimerRunning = false;
    private long timeLeftInMillis;
    private String activityId;
    private String userId;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

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

        // Initialize UI elements
        activityImage = findViewById(R.id.activityImage);
        activityName = findViewById(R.id.activityName);
        activityDescription = findViewById(R.id.activityDescription);
        activityTimer = findViewById(R.id.activityTiming);
        startButton = findViewById(R.id.startButton);

        // Retrieve intent data
        Intent intent = getIntent();
        String name = intent.getStringExtra("EXERCISE_NAME");
        int duration = intent.getIntExtra("EXERCISE_DURATION", 0);
        String category = intent.getStringExtra("EXERCISE_CATEGORY");
        String picPath = intent.getStringExtra("EXERCISE_PIC_PATH");

        // Populate UI with data from the intent
        populateUI(name, duration, category, picPath);

        // Set timer duration (convert minutes to milliseconds)
        timeLeftInMillis = duration * 60 * 1000;

        // Set button listeners
        startButton.setOnClickListener(v -> {
            if (!isTimerRunning) {
                startTimer();
            }
        });
        // Initialize Firebase
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Exercises");


        // Fetch activity details
        fetchActivityDetailsFromFirebase(name);

    }

    // Clean up timer on destroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void fetchActivityDetailsFromFirebase(String exerciseName) {
        databaseReference.orderByChild("exerciseName").equalTo(exerciseName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot exerciseSnapshot : snapshot.getChildren()) {
                                // Map the data to the Exercise class
                                Exercise exercise = exerciseSnapshot.getValue(Exercise.class);
                                if (exercise != null) {
                                    activityName.setText(exercise.getExerciseName());
                                    activityDescription.setText(String.format("Category: %s", exercise.getExerciseCategory()));

                                    // Handle image loading
                                    if (exercise.getExercisePicPath() != null && !exercise.getExercisePicPath().isEmpty()) {
                                        Glide.with(Timer.this)
                                                .load(exercise.getExercisePicPath())
                                                .into(activityImage);
                                    } else {
                                        activityImage.setImageResource(R.drawable.jogging); // Default image
                                    }

                                    // Set timer duration
                                    timeLeftInMillis = exercise.getExerciseDuration() * 60 * 1000;
                                    updateTimer();
                                }
                            }
                        } else {
                            handleFirebaseFailure();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        handleFirebaseFailure();
                        Log.e("Firebase", error.getMessage());
                    }
                });
    }


    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                showSuccessMessage();
                saveActivityDataToFirebase();
            }
        }.start();

        isTimerRunning = true;
        startButton.setEnabled(false);
    }

    private void updateTimer() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        activityTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    private void saveActivityDataToFirebase() {
        DatabaseReference userActivitiesRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("CompletedActivities");

        HashMap<String, Object> activityData = new HashMap<>();
        activityData.put("activityId", activityId);
        activityData.put("completedAt", System.currentTimeMillis());

        userActivitiesRef.push().setValue(activityData)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Activity saved!"))
                .addOnFailureListener(e -> Log.e("Firebase", "Error saving activity: " + e.getMessage()));
    }

    private void showSuccessMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Congratulations!")
                .setMessage("You have completed the activity!")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void handleFirebaseFailure() {
        activityName.setText("Error loading activity");
        activityDescription.setText("Please try again later.");
        startButton.setEnabled(false);
    }

    private void populateUI(String name, int duration, String category, String picPath) {
        activityName.setText(name);
        activityDescription.setText(String.format(Locale.getDefault(), "%s - %d min", category, duration));

        if (picPath != null && !picPath.isEmpty()) {
            Glide.with(this).load(picPath).into(activityImage);
        } else {
            activityImage.setImageResource(R.drawable.jogging); // Default image
        }
    }

}

