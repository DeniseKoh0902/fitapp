package com.example.fitapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fitapp.R;
import com.example.fitapp.javahelperfile.exercise.Exercise;
import com.example.fitapp.javahelperfile.exercise.ExerciseDataUploader;
import com.example.fitapp.javahelperfile.exercise.TodayActivityAdapter;
import com.example.fitapp.javahelperfile.profile.User;
import com.example.fitapp.ui.admin.AdminProfile;
import com.example.fitapp.ui.analytics.Analytics;
import com.example.fitapp.ui.exercise.ExerciseListActivity;
import com.example.fitapp.ui.exercise.TodayActivity;
import com.example.fitapp.ui.profile.Profile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    boolean isAdmin;
    private RecyclerView recyclerView;
    private TodayActivityAdapter adapter;
    private List<Exercise> userExercises = new ArrayList<>();
    private User user;
    private String mode = "activity_today";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // IMPORTANT !! Only call this ONCE to upload exercise for global list
//        ExerciseDataUploader uploader = new ExerciseDataUploader();
//        uploader.uploadExercises();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        user = getIntent().getParcelableExtra("user");

        ImageView userProfilePicture = findViewById(R.id.IVHomeProfile);
        TextView userLevel = findViewById(R.id.TVLevel);
        TextView userPoints = findViewById(R.id.TVPoints);

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("User")
                .child(user.getUserId());

        userRef.child("user_points").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Integer updatedPoints = snapshot.getValue(Integer.class);
                    if (updatedPoints != null) {
                        userPoints.setText(updatedPoints + "pt");

                        String newUserType = calculateUserType(updatedPoints);
                        userLevel.setText(newUserType);

                        userRef.child("user_types").setValue(newUserType)
                                .addOnSuccessListener(aVoid -> Log.d("FirebaseUpdate", "User type updated successfully"))
                                .addOnFailureListener(e -> Log.e("FirebaseUpdate", "Failed to update user type: " + e.getMessage()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MainActivity", "Failed to retrieve user points: " + error.getMessage());
            }
        });



        if (user != null) {
            TextView welcomeText = findViewById(R.id.welcomeText);
            welcomeText.setText("Welcome, " + user.getUsername());


            if (user.getProfilePicPath() != null && !user.getProfilePicPath().isEmpty()) {
                Glide.with(this)
                        .load(user.getProfilePicPath())
                        .placeholder(R.drawable.profilepic)
                        .into(userProfilePicture);
            }else {
                userProfilePicture.setImageResource(R.drawable.profilepic);
            }
        } else {
            Log.e("MainActivity", "User object is null!");
        }


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
                    intent = new Intent(MainActivity.this, AdminProfile.class);
                } else {
                    intent = new Intent(MainActivity.this, Profile.class);
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

        LinearLayout btnAerobic = findViewById(R.id.aerobicLayout);
        LinearLayout btnFlexibility = findViewById(R.id.flexibilityLayout);
        LinearLayout btnBalance = findViewById(R.id.balanceLayout);
        LinearLayout btnHIIT = findViewById(R.id.hittLayout);
        LinearLayout btnStrength = findViewById(R.id.strengthLayout);

        Button btnToday = findViewById(R.id.btnTodayActivity);

        btnAerobic.setOnClickListener(v -> openExerciseListActivity(user, "Aerobic"));
        btnFlexibility.setOnClickListener(v -> openExerciseListActivity(user, "Flexibility"));
        btnBalance.setOnClickListener(v -> openExerciseListActivity(user, "Balance"));
        btnHIIT.setOnClickListener(v -> openExerciseListActivity(user, "HIIT"));
        btnStrength.setOnClickListener(v -> openExerciseListActivity(user, "Strength"));

        btnToday.setOnClickListener(v -> openTodayActivityPage(user));

        recyclerView = findViewById(R.id.todayRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new TodayActivityAdapter(this, userExercises, mode, user, isAdmin);
        recyclerView.setAdapter(adapter);

        fetchUserExercisesFromFirebase();

    }

    private String calculateUserType(int points) {
        if (points < 25) {
            return "Bronze";
        } else if (points >= 26 && points <= 75) {
            return "Silver";
        } else if (points > 76 && points <= 200) {
            return "Gold";
        } else if (points > 200) {
            return "Diamond";
        }
        return "Bronze";
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
                Toast.makeText(MainActivity.this, "Failed to load exercises.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this, "Failed to load exercise details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openExerciseListActivity(User user, String category) {
        Intent intent = new Intent(MainActivity.this, ExerciseListActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("isAdmin", isAdmin);
        intent.putExtra("category", category);
        startActivity(intent);
    }


    private void openTodayActivityPage(User user) {
        Intent intent = new Intent(MainActivity.this, TodayActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("isAdmin", isAdmin);
        intent.putExtra("mode", "activity_today");
        startActivity(intent);
    }

}
