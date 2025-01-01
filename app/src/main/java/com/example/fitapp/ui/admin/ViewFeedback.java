package com.example.fitapp.ui.admin;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fitapp.R;
import com.example.fitapp.javahelperfile.profile.User;
import com.example.fitapp.javahelperfile.ratingsystem.Feedback;
import com.example.fitapp.javahelperfile.ratingsystem.FeedbackAdapter;
import com.example.fitapp.javahelperfile.ratingsystem.FeedbackWithUser;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewFeedback extends AppCompatActivity {

    private User user;
    private boolean isAdmin;
    private RecyclerView recyclerView;
    private FeedbackAdapter adapter;
    private List<FeedbackWithUser> feedbackList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_feedback);

        // Retrieve the User object
        user = getIntent().getParcelableExtra("user");
        // Check if the user is an admin
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        recyclerView = findViewById(R.id.RVFeedbackList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FeedbackAdapter(feedbackList, this);
        recyclerView.setAdapter(adapter);

        loadFeedbackWithUserData();
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
        Toolbar toolbar = findViewById(R.id.toolbarViewFeedback);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_ios_24);
        if (upArrow != null) {
            DrawableCompat.setTint(upArrow, ContextCompat.getColor(this, R.color.black)); // Set your desired color
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
    }

    // for toolbar use to return to previous page
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFeedbackWithUserData() {
        DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("Feedback");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");

        feedbackRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot feedbackSnapshot) {
                feedbackList.clear();

                for (DataSnapshot feedbackIdSnapshot : feedbackSnapshot.getChildren()) {
                    for (DataSnapshot userFeedbackSnapshot : feedbackIdSnapshot.getChildren()) {
                        Feedback feedback = userFeedbackSnapshot.getValue(Feedback.class);
                        if (feedback != null) {
                            String userId = userFeedbackSnapshot.getKey();  // Get the user ID

                            userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                    String username = userSnapshot.child("username").getValue(String.class);
                                    String profilePicPath = userSnapshot.child("profile_pic_path").getValue(String.class);

                                    if (username == null) username = "Unknown User";
                                    if (profilePicPath == null) profilePicPath = "";

                                    FeedbackWithUser feedbackWithUser = new FeedbackWithUser(
                                            feedback.getFeedbackText(),
                                            feedback.getRating(),
                                            userId,
                                            username,
                                            profilePicPath
                                    );

                                    feedbackList.add(feedbackWithUser);
                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("UserFetchError", "Failed to fetch user data: " + error.getMessage());
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FeedbackFetchError", "Failed to fetch feedback data: " + error.getMessage());
            }
        });
    }

    private void loadProfilePictureFromDatabase(String userId, ImageView profilePic) {
        // Firebase reference to fetch profile picture path
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userId);

        userRef.child("profile_pic_path").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().getValue() != null) {
                String imageUrl = task.getResult().getValue(String.class);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(this)
                            .load(imageUrl)
                            .circleCrop()
                            .placeholder(R.drawable.profilepic)
                            .error(R.drawable.profilepic)
                            .into(profilePic);
                } else {
                    profilePic.setImageResource(R.drawable.profilepic);
                }
            } else {
                profilePic.setImageResource(R.drawable.profilepic);
            }
        });
    }
}


