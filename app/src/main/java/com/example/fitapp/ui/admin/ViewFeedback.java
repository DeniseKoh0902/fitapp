package com.example.fitapp.ui.admin;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;


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

        user = getIntent().getParcelableExtra("user");
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        recyclerView = findViewById(R.id.RVFeedbackList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FeedbackAdapter(feedbackList, this);
        recyclerView.setAdapter(adapter);

        loadFeedbackWithUserData();
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
        Toolbar toolbar = findViewById(R.id.toolbarViewFeedback);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_ios_24);
        if (upArrow != null) {
            DrawableCompat.setTint(upArrow, ContextCompat.getColor(this, R.color.black));
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
    }

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

                List<FeedbackWithUser> feedbackWithUserList = new ArrayList<>();

                for (DataSnapshot feedbackIdSnapshot : feedbackSnapshot.getChildren()) {
                    for (DataSnapshot userFeedbackSnapshot : feedbackIdSnapshot.getChildren()) {
                        Feedback feedback = userFeedbackSnapshot.getValue(Feedback.class);
                        if (feedback != null) {
                            String userId = userFeedbackSnapshot.getKey();

                            String feedbackDatetime = feedback.getFeedbackDatetime();
                            Date feedbackDate = parseFeedbackDatetime(feedbackDatetime);

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
                                            profilePicPath,
                                            feedbackDate
                                    );

                                    feedbackWithUserList.add(feedbackWithUser);

                                    Collections.sort(feedbackWithUserList, (f1, f2) -> f2.getFeedbackDatetime().compareTo(f1.getFeedbackDatetime()));

                                    feedbackList.clear();
                                    feedbackList.addAll(feedbackWithUserList);
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

    private Date parseFeedbackDatetime(String datetime) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            return dateFormat.parse(datetime);
        } catch (ParseException e) {
            Log.e("DateParseError", "Failed to parse date: " + datetime);
            return new Date();
        }
    }

}


