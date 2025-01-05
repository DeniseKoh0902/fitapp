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
    private String mode = "activity_today";
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        user = getIntent().getParcelableExtra("user");

        if (user == null) {
            Toast.makeText(this, "Error: User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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
        recyclerView = findViewById(R.id.RVTodayActivity);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new TodayActivityAdapter(this, userExercises, mode, user, isAdmin);
        recyclerView.setAdapter(adapter);

        ImageButton buttonBack = findViewById(R.id.ButtonBack);
        buttonBack.setOnClickListener(view -> finish());

        fetchUserExercisesFromFirebase();

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
                userExercises.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String exerciseId = snapshot.getKey();
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
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(TodayActivity.this, "Failed to load exercise details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleMode() {
        mode = mode.equals("activity_today") ? "activity_today_edit" : "activity_today";
        adapter.updateMode(mode);

        Button buttonEditConfirm = findViewById(R.id.BtnEditConfirm);
        if (mode.equals("activity_today")) {
            buttonEditConfirm.setText("Edit");
        } else {
            buttonEditConfirm.setText("Confirm");
        }
    }

    public void onDeleteExercise(Exercise exercise) {
        userExercises.remove(exercise);
        adapter.notifyDataSetChanged();

        if (user != null && user.getUserId() != null) {
            DatabaseReference userExerciseListRef = FirebaseDatabase.getInstance().getReference("User_Exercise_List")
                    .child(user.getUserId()).child(exercise.getExerciseId());
            userExerciseListRef.removeValue()
                    .addOnSuccessListener(aVoid -> Toast.makeText(TodayActivity.this, "Exercise deleted.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(TodayActivity.this, "Failed to delete exercise.", Toast.LENGTH_SHORT).show());
        }
    }
}
