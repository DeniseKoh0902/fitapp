package com.example.fitapp.ui.admin;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitapp.R;
import com.example.fitapp.javahelperfile.profile.User;
import com.example.fitapp.javahelperfile.profile.UserAdapter;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Collections;
import java.util.Comparator;

public class UserList extends AppCompatActivity {

    private User user;
    private boolean isAdmin;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        user = getIntent().getParcelableExtra("user");
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        recyclerView = findViewById(R.id.RVUserList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userRef = FirebaseDatabase.getInstance().getReference("User");

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);
        recyclerView.setAdapter(userAdapter);

        loadUserList();


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);

        if (isAdmin) {
            bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.purple));
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("user", user);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (item.getItemId() == R.id.bottom_analytics) {
                Intent intent = new Intent(getApplicationContext(), Analytics.class);
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("user", user);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (item.getItemId() == R.id.bottom_profile) {
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("user", user);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });

        Toolbar toolbar = findViewById(R.id.toolbarUserList);
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
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUserList() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");

         userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    String username = userSnapshot.child("username").getValue(String.class);
                    String signUpDate = userSnapshot.child("user_sign_up_date").getValue(String.class);
                    String profilePicPath = userSnapshot.child("profile_pic_path").getValue(String.class);

                    if (username == null) username = "Unknown User";
                    if (profilePicPath == null) profilePicPath = "";
                    if (signUpDate == null) signUpDate = "";

                    User userItem = new User(userId, username, signUpDate, profilePicPath);

                    userList.add(userItem);
                }

                Collections.sort(userList, new Comparator<User>() {
                    @Override
                    public int compare(User user1, User user2) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");  // Adjust to match your date format
                        try {
                            Date date1 = sdf.parse(user1.getSignUpDate());
                            Date date2 = sdf.parse(user2.getSignUpDate());

                            return date2.compareTo(date1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0;  // If parsing fails, no sorting is done
                        }
                    }
                });

               userAdapter.notifyDataSetChanged();

               TextView tvTotal = findViewById(R.id.TVTotal);
                tvTotal.setText(String.valueOf(userAdapter.getItemCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UserFetchError", "Failed to fetch user data: " + databaseError.getMessage());
            }
        });
    }

}