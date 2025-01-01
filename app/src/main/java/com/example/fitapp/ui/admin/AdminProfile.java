package com.example.fitapp.ui.admin;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.fitapp.R;
import com.example.fitapp.javahelperfile.profile.User;
import com.example.fitapp.ui.analytics.Analytics;
import com.example.fitapp.ui.analytics.HealthRecord;
import com.example.fitapp.ui.analytics.ViewHealthReport;
import com.example.fitapp.ui.generalmodule.WelcomingPage;
import com.example.fitapp.ui.home.MainActivity;
import com.example.fitapp.ui.profile.EditProfile;
import com.example.fitapp.ui.profile.Profile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminProfile extends AppCompatActivity {
    private DatabaseReference userRef;
    private ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        // Retrieve User object
        User user = getIntent().getParcelableExtra("user");
        // Retrieve the isAdmin flag
        boolean isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        if (user != null) {
            // Populate the fields with user information
            EditText username = findViewById(R.id.username);
            EditText userType = findViewById(R.id.usertype);
            EditText dob = findViewById(R.id.dob);
            EditText email = findViewById(R.id.emailadd);
            EditText phone = findViewById(R.id.phone);
            EditText points = findViewById(R.id.point);
            TextView name = findViewById(R.id.upperusername);

            username.setText("            "+user.getUsername());
            userType.setText("            "+user.getUserType()); // Use getUserType() from User class
            dob.setText("            "+user.getDob());
            email.setText("            "+user.getEmail());
            phone.setText("            "+user.getPhone());
            points.setText("            "+user.getPoints() + " points");
            name.setText(user.getUsername());

            // Make the EditTexts non-editable
            disableEditText(username, userType, dob, email, phone, points);
        }

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
                    intent = new Intent(AdminProfile.this, AdminFeatures.class);
                } else {
                    // Navigate to Analytics for regular users
                    intent = new Intent(AdminProfile.this, Analytics.class);
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
                    intent = new Intent(AdminProfile.this, AdminProfile.class);
                } else {
                    // Navigate to Profile for regular users
                    intent = new Intent(AdminProfile.this, Profile.class);
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

        Button logout = findViewById(R.id.logoutbutton);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminProfile.this, WelcomingPage.class);
                startActivity(intent);
            }
        });

        ImageView editprofile = findViewById(R.id.editprofileicon);
        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminProfile.this, EditProfile.class);
                intent.putExtra("user", user);
                intent.putExtra("isAdmin", isAdmin);
                startActivity(intent);
            }
        });
        Button viewAdminFeatures = findViewById(R.id.BtnAdminFeatures);
        viewAdminFeatures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the NextActivity
                Intent intent = new Intent(AdminProfile.this, AdminFeatures.class);
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("user", user); // Attach the Parcelable User object
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // Optional transition
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userRef = database.getReference("User").child(user.getUserId());

        profilePic = findViewById(R.id.profilepic);
        // Load the saved profile picture
        loadProfilePictureFromDatabase();
    }

    private void disableEditText(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false);
            editText.setEnabled(false);
        }
    }

    private void loadProfilePictureFromDatabase() {
        userRef.child("profile_pic_path").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().getValue() != null) {
                String imageUrl = task.getResult().getValue(String.class);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(this).load(imageUrl).into(profilePic);
                } else {
                    profilePic.setImageResource(R.drawable.profilepic);
                }
            } else {
                profilePic.setImageResource(R.drawable.profilepic);
            }
        });
    }
}