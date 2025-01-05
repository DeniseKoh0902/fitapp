package com.example.fitapp.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fitapp.R;
import com.example.fitapp.javahelperfile.profile.User;
import com.example.fitapp.ui.analytics.Analytics;

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

        User user = getIntent().getParcelableExtra("user");
        boolean isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        if (user != null) {
            EditText username = findViewById(R.id.username);
            EditText userType = findViewById(R.id.usertype);
            EditText dob = findViewById(R.id.dob);
            EditText email = findViewById(R.id.emailadd);
            EditText phone = findViewById(R.id.phone);
            EditText points = findViewById(R.id.point);
            TextView name = findViewById(R.id.upperusername);

            username.setText("            "+user.getUsername());
            userType.setText("            "+user.getUserType());
            dob.setText("            "+user.getDob());
            email.setText("            "+user.getEmail());
            phone.setText("            "+user.getPhone());
            points.setText("            "+user.getPoints() + " points");
            name.setText(user.getUsername());

            disableEditText(username, userType, dob, email, phone, points);
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);

        if (isAdmin) {
            bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.purple));
        }

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
                    intent = new Intent(AdminProfile.this, AdminProfile.class);
                } else {
                    intent = new Intent(AdminProfile.this, Profile.class);
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
                Intent intent = new Intent(AdminProfile.this, AdminFeatures.class);
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("user", user);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userRef = database.getReference("User").child(user.getUserId());

        profilePic = findViewById(R.id.profilepic);
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