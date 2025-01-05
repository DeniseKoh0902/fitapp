package com.example.fitapp.ui.home;
import android.app.Dialog;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fitapp.R;
import com.example.fitapp.javahelperfile.exercise.Exercise;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Timer extends AppCompatActivity {

    boolean isAdmin;
    private User user;
    private TextView activityName, activityDescription, activityTimer;
    private Button startButton;
    private ImageView activityImage;
    private CountDownTimer countDownTimer;

    private boolean isTimerRunning = false;
    private long timeLeftInMillis;
    private String activityId;
    private String userId;
    private String exerciseid;
    private DatabaseReference databaseReference;
    private String category;
    private int duration;

    private int resid;
    private float caloriesBurned;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        user = getIntent().getParcelableExtra("user");

        if (user == null) {
            Toast.makeText(this, "Error: User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ImageButton buttonBack = findViewById(R.id.ButtonBack);
        buttonBack.setOnClickListener(view -> finish());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        if (isAdmin) {
            bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.purple));
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
                return true;
            } else if (item.getItemId() == R.id.bottom_analytics) {
                Intent intent;
                if (isAdmin) {
                    intent = new Intent(getApplicationContext(), AdminFeatures.class);
                } else {
                    intent = new Intent(getApplicationContext(), Analytics.class);
                }
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("user", user);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (item.getItemId() == R.id.bottom_profile) {
                Intent intent;
                if (isAdmin) {
                    intent = new Intent(getApplicationContext(), AdminProfile.class);
                } else {
                    intent = new Intent(getApplicationContext(), Profile.class);
                }
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("user", user);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });

        activityImage = findViewById(R.id.activityImage);
        activityName = findViewById(R.id.activityName);
        activityDescription = findViewById(R.id.activityDescription);
        activityTimer = findViewById(R.id.activityTiming);
        startButton = findViewById(R.id.startButton);

        progressBar = findViewById(R.id.PBTimer);


        Intent intent = getIntent();
        String name = intent.getStringExtra("EXERCISE_NAME");
        duration = intent.getIntExtra("EXERCISE_DURATION", 0);
        category = intent.getStringExtra("EXERCISE_CATEGORY");
        exerciseid = intent.getStringExtra("EXERCISE_ID");
        String picPath = intent.getStringExtra("EXERCISE_PIC_PATH");
        resid= intent.getIntExtra("EXERCISE_IMAGE_RESID",0);

        populateUI(name, duration, category, picPath);

        timeLeftInMillis = duration * 60 * 1000;

        startButton.setOnClickListener(v -> {
            if (isTimerRunning) {
                stopTimer();
            } else {
                startTimer();
            }
        });

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Exercises");

        fetchActivityDetailsFromFirebase(name);

    }

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
                            Exercise exercise = exerciseSnapshot.getValue(Exercise.class);
                            if (exercise != null) {
                                activityName.setText(exercise.getExerciseName());
                                timeLeftInMillis = exercise.getExerciseDuration() * 60 * 1000;
                                updateTimer();


                                caloriesBurned = exercise.getCaloriesBurned();

                                Log.d("Firebase", "Calories burned: " + caloriesBurned);
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
        progressBar.setMax(100);
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimer();
                updateProgressBar();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                showSuccessMessage();
                saveActivityDataToFirebase();
                addPointsToUser(5);
                startButton.setText("Start");
                startButton.setEnabled(true);
                progressBar.setProgress(0);
            }
        }.start();

        isTimerRunning = true;
        startButton.setText("Stop");
        startButton.setEnabled(true);
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        isTimerRunning = false;
        startButton.setText("Start");
        startButton.setEnabled(true);
    }

    private void updateTimer() {
        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        activityTimer.setText(timeFormatted);
    }

    private void saveActivityDataToFirebase() {
        if (user == null) {
            Log.e("TimerActivity", "Current user is null.");
            return;
        }
        String completedDate = getCurrentISODateTime();

        DatabaseReference userActivitiesRef = FirebaseDatabase.getInstance()
                .getReference("Users_Exercise_History")
                .child(user.getUserId());

        HashMap<String, Object> activityData = new HashMap<>();
        activityData.put("exercise_id", activityId);
        activityData.put("exercise_date", completedDate);
        activityData.put("exercise_duration", duration);
        activityData.put("calories_burned", caloriesBurned);

        userActivitiesRef.push().setValue(activityData)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Activity saved!"))
                .addOnFailureListener(e -> Log.e("Firebase", "Error saving activity: " + e.getMessage()));
    }

    private String getCurrentISODateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return sdf.format(new Date());
    }

    private void showSuccessMessage() {
        showCongratsDialog();
    }

    private void handleFirebaseFailure() {
        activityName.setText("Error loading activity");
        activityDescription.setText("Please try again later.");
        startButton.setEnabled(false);
    }

    private void addPointsToUser(int pointsToAdd) {
        if (user == null) {
            Log.e("TimerActivity", "Current user is null.");
            return;
        }

        DatabaseReference userPointsRef = FirebaseDatabase.getInstance().getReference("User")
                .child(user.getUserId()).child("user_points");

        userPointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long currentPoints = 0;
                if (dataSnapshot.exists()) {
                    currentPoints = dataSnapshot.getValue(Long.class);
                }

                long updatedPoints = currentPoints + pointsToAdd;
                userPointsRef.setValue(updatedPoints)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("TimerActivity", "Points updated successfully to " + updatedPoints);
                            Toast.makeText(Timer.this, "You earned " + pointsToAdd + " points!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("TimerActivity", "Failed to update points: " + e.getMessage());
                            Toast.makeText(Timer.this, "Failed to update points: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TimerActivity", "Error reading points: " + databaseError.getMessage());
            }
        });
    }

    private void populateUI(String name, int duration, String category, String picPath) {
        activityName.setText(name);
        String description;
        switch (category) {
            case "Aerobic":
                activityName.setText("Aerobic Exercise");
                description = "Boost your heart health and stamina!";
                break;
            case "Flexibility":
                activityName.setText("Flexibility Exercise");
                description = "Stretch your limits for a more agile you!";
                break;
            case "Balance":
                activityName.setText("Balance Exercise");
                description = "Strengthen your core for a more balanced life!";
                break;
            case "HIIT":
                activityName.setText("High-Intensity Interval Training");
                description = "Push your limits and torch calories fast!";
                break;
            case "Strength":
                activityName.setText("Strength Training");
                description = "Lift, push, and conquer for power and confidence!";
                break;
            default:
                activityName.setText("Exercise");
                description = "";
                break;
        }

        activityDescription.setText(description);

        if (picPath != null && !picPath.isEmpty()) {
            Glide.with(getApplicationContext())
                    .load(picPath)
                    .placeholder(R.drawable.yoga)
                    .error(R.drawable.yoga)
                    .into(activityImage);
        } else {
            if (resid!= 0) {
                activityImage.setImageResource(resid);
            } else {
                activityImage.setImageResource(R.drawable.jogging);
            }
        }

    }

    private void updateProgressBar() {
        int elapsedMillis = (int) (duration * 60 * 1000 - timeLeftInMillis);
        int progress = (int) ((elapsedMillis / (float) (duration * 60 * 1000)) * 100);
        progressBar.setProgress(progress);
    }

    private void showCongratsDialog() {
        Dialog dialog = new Dialog(Timer.this);
        dialog.setContentView(R.layout.congratulations);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        dialog.setCancelable(false);

        TextView TVUsername = dialog.findViewById(R.id.TVName);
        Button BtnHome = dialog.findViewById(R.id.BtnHome);

        if (user != null) {
            TVUsername.setText(user.getUsername());
        } else {
            TVUsername.setText("Unknown User");
        }

        dialog.show();

        BtnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("user", user);

                startActivity(intent);


                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

}

