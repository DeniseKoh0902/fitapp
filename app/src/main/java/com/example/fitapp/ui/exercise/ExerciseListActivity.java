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

        user = getIntent().getParcelableExtra("user");

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
                Intent intent = new Intent(getApplicationContext(), Analytics.class);
                intent.putExtra("user", user);
                intent.putExtra("isAdmin", isAdmin);
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
        titleTextView = findViewById(R.id.TVExerciseTitle);
        subtitleTextView = findViewById(R.id.TVExerciseSubTitle);
        recyclerView = findViewById(R.id.RVTodayActivity);
        ImageButton buttonBack = findViewById(R.id.ButtonBack);

        buttonBack.setOnClickListener(view -> finish());

        category = getIntent().getStringExtra("category");

        updateTitleAndSubtitle();

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ExerciseAdapter(user, this, exerciseList, category);
        recyclerView.setAdapter(adapter);

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

        exercisesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                exerciseList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Exercise exercise = snapshot.getValue(Exercise.class);
                    if (exercise != null && exercise.getExerciseCategory().equals(category)) {
                        exerciseList.add(exercise);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ExerciseListActivity", "Error fetching data: " + databaseError.getMessage());
            }
        });
    }

    public void onAddExercise(Exercise exercise) {
        addExerciseToUserList(exercise);
    }

    private void addExerciseToUserList(Exercise exercise) {
        if (user == null) {
            Log.e("ExerciseListActivity", "Current user is null.");
            return;
        }

        DatabaseReference userExerciseListRef = FirebaseDatabase.getInstance().getReference("User_Exercise_List")
                .child(user.getUserId());

        userExerciseListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild(exercise.getExerciseId())) {
                        Toast.makeText(ExerciseListActivity.this, "Exercise already in your list.", Toast.LENGTH_SHORT).show();
                    } else {
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
                } else {
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
