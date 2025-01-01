package com.example.fitapp.ui.admin;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fitapp.R;
import com.example.fitapp.javahelperfile.exercise.Exercise;
import com.example.fitapp.javahelperfile.exercise.ExerciseAdapter;
import com.example.fitapp.javahelperfile.profile.User;
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

public class ChooseCategoryToEdit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category_to_edit);

        // Retrieve User object
        User user = getIntent().getParcelableExtra("user");
        // Retrieve the isAdmin flag
        boolean isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        // Bottom menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("isAdmin", isAdmin);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
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

        // Upper Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarChooseCategoryToEdit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_ios_24);
        if (upArrow != null) {
            DrawableCompat.setTint(upArrow, ContextCompat.getColor(this, R.color.black)); // Set your desired color
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        Button btnAerobic = findViewById(R.id.BtnAerobic);
        Button btnFlexibility = findViewById(R.id.BtnFlexibility);
        Button btnBalance = findViewById(R.id.BtnBalance);
        Button btnHIIT = findViewById(R.id.BtnHIIT);
        Button btnStrength = findViewById(R.id.BtnStrength);

        btnAerobic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the ChooseExerciseToEdit Activity
                Intent intent = new Intent(ChooseCategoryToEdit.this, ChooseExerciseToEdit.class);
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("user", user); // Attach the Parcelable User object
                intent.putExtra("category", "Aerobic"); // Pass the category
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // Optional transition
            }
        });

        btnFlexibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseCategoryToEdit.this, ChooseExerciseToEdit.class);
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("user", user);
                intent.putExtra("category", "Flexibility"); // Pass the category
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btnBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseCategoryToEdit.this, ChooseExerciseToEdit.class);
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("user", user);
                intent.putExtra("category", "Balance"); // Pass the category
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btnHIIT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseCategoryToEdit.this, ChooseExerciseToEdit.class);
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("user", user);
                intent.putExtra("category", "HIIT"); // Pass the category
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btnStrength.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseCategoryToEdit.this, ChooseExerciseToEdit.class);
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("user", user);
                intent.putExtra("category", "Strength"); // Pass the category
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


    }

    // for toolbar use to return to previous page
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}