package com.example.fitapp.ui.ratingsystem;

import android.content.Intent;
<<<<<<< HEAD
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.fitapp.javahelperfile.ratingsystem.Feedback;
import com.example.fitapp.ui.admin.AdminFeatures;
import com.example.fitapp.ui.admin.ViewFeedback;
=======
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

>>>>>>> 172b022a49342446412c20a1f92646cf78a14948
import com.example.fitapp.ui.analytics.Analytics;
import com.example.fitapp.ui.home.MainActivity;
import com.example.fitapp.R;
import com.example.fitapp.javahelperfile.profile.User;
import com.example.fitapp.ui.profile.Profile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
<<<<<<< HEAD
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
=======
>>>>>>> 172b022a49342446412c20a1f92646cf78a14948

public class RateUs extends AppCompatActivity {

    private User user;

<<<<<<< HEAD
    // Firebase Database reference
    private DatabaseReference databaseReference;

=======
>>>>>>> 172b022a49342446412c20a1f92646cf78a14948
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_us);
        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Retrieve the User object
        user = getIntent().getParcelableExtra("user");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);

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
            } else if (item.getItemId() == R.id.bottom_profile) {
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                intent.putExtra("user", user); // Attach the Parcelable User object
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });
<<<<<<< HEAD

        // Upper Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarRateUs);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_ios_24);
        if (upArrow != null) {
            DrawableCompat.setTint(upArrow, ContextCompat.getColor(this, R.color.black)); // Set your desired color
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        // Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback");

        // Initialize views
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        EditText additionalComments = findViewById(R.id.ETAdditionalComments);
        Button submitFeedback = findViewById(R.id.BtnSubmitFeedback);

        // Set up button click listener
        submitFeedback.setOnClickListener(view -> {
            // Get user input
            float rating = ratingBar.getRating();
            String feedbackText = additionalComments.getText().toString().trim();

            String feedbackDatetime = getISO8601Date();

            Feedback feedback = new Feedback(feedbackText, rating, feedbackDatetime);

            // Save the feedback to Firebase
            saveFeedbackToFirebase(feedback);
        });
    }

    /**
     * Method to save Feedback object to Firebase
     */
    private void saveFeedbackToFirebase(Feedback feedback) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Feedback");

        // Generate a unique feedback ID
        String feedbackId = databaseRef.push().getKey();

        if (feedbackId != null) {
            // Save the feedback under the generated ID and user ID
            databaseRef.child(feedbackId)
                    .child(user.getUserId())
                    .setValue(feedback)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(RateUs.this, "Feedback submitted successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RateUs.this, Profile.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        finish(); // Close the current activity
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(RateUs.this, "Failed to submit feedback: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(RateUs.this, "Error generating feedback ID.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getISO8601Date() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Ensure UTC timezone
        return dateFormat.format(new Date());
    }

=======
    }
>>>>>>> 172b022a49342446412c20a1f92646cf78a14948
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the Up button action
            finish(); // Closes the current activity and returns to the parent
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}