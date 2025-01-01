package com.example.fitapp.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fitapp.ui.analytics.Analytics;
import com.example.fitapp.ui.home.MainActivity;
import com.example.fitapp.R;
import com.example.fitapp.javahelperfile.profile.User;
import com.example.fitapp.ui.generalmodule.WelcomingPage;
import com.example.fitapp.ui.ratingsystem.RateUs;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends AppCompatActivity {

    private ImageView editprofile;
    private Button logout,rateus;
    private DatabaseReference userRef;
    private ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);
        // Retrieve the User object
        User user = getIntent().getParcelableExtra("user");

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

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (item.getItemId() == R.id.bottom_analytics) {
                Intent intent = new Intent(getApplicationContext(), Analytics.class);
                intent.putExtra("user", user); // Attach the Parcelable User object
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (item.getItemId() == R.id.bottom_profile) {
                return true;
            }
            return false;
        });

        logout = findViewById(R.id.logoutbutton);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, WelcomingPage.class);
                startActivity(intent);
            }
        });

        editprofile = findViewById(R.id.editprofileicon);
        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, EditProfile.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        //modify here miaomiao if got rateus class already
        rateus = findViewById(R.id.ratebutton);
        rateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, RateUs.class);
                intent.putExtra("user", user);
                startActivity(intent);
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