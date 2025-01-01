package com.example.fitapp.ui.admin;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.example.fitapp.javahelperfile.profile.User;
import com.example.fitapp.ui.analytics.Analytics;
import com.example.fitapp.ui.home.MainActivity;
import com.example.fitapp.ui.profile.Profile;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminFeatures extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_features);

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
                    intent = new Intent(AdminFeatures.this, AdminFeatures.class);
                } else {
                    // Navigate to Analytics for regular users
                    intent = new Intent(AdminFeatures.this, Analytics.class);
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
                    intent = new Intent(AdminFeatures.this, AdminProfile.class);
                } else {
                    // Navigate to Profile for regular users
                    intent = new Intent(AdminFeatures.this, Profile.class);
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
        Toolbar toolbar = findViewById(R.id.toolbarAdminFeatures);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_ios_24);
        if (upArrow != null) {
            DrawableCompat.setTint(upArrow, ContextCompat.getColor(this, R.color.black)); // Set your desired color
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        Button viewUserList = findViewById(R.id.btnCheckUserList);
        viewUserList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the NextActivity
                Intent intent = new Intent(AdminFeatures.this, UserList.class);
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("user", user); // Attach the Parcelable User object
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // Optional transition
            }
        });

        Button viewEditExercise = findViewById(R.id.btnEditExercise);
        viewEditExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the NextActivity
                Intent intent = new Intent(AdminFeatures.this, ChooseCategoryToEdit.class);
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("user", user); // Attach the Parcelable User object
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // Optional transition
            }
        });

        Button viewFeedback = findViewById(R.id.btnViewFeedback);
        viewFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the NextActivity
                Intent intent = new Intent(AdminFeatures.this, ViewFeedback.class);
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("user", user); // Attach the Parcelable User object
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // Optional transition
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