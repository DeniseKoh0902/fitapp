package com.example.fitapp.ui.analytics;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.fitapp.ui.admin.AdminProfile;
import com.example.fitapp.ui.home.MainActivity;
import com.example.fitapp.R;
import com.example.fitapp.javahelperfile.analytics.SleepRecordManager;
import com.example.fitapp.javahelperfile.profile.User;
import com.example.fitapp.javahelperfile.analytics.UserWeightBMIManager;
import com.example.fitapp.javahelperfile.analytics.WaterIntakeManager;
import com.example.fitapp.ui.profile.Profile;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HealthRecord extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private TextView progressText;
    private int currentIntake = 0;
    private final int dailyGoal = 1600;
    Button buttonRecordHours,buttonRecordHeightAndWeight;
    TextView tvHoursRecord,tvheight, tvweight,tvUserBmi, username, tvUserCalorie;

    private WaterIntakeManager waterIntakeManager;
    private SleepRecordManager sleepRecordManager;

    private UserWeightBMIManager userWeightBMIManager;

    private User user;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_record);
        FirebaseApp.initializeApp(this);

        user = getIntent().getParcelableExtra("user");
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        progressBar = findViewById(R.id.progress_bar);
        progressText = findViewById(R.id.progress_text);
        progressBar.setMax(dailyGoal);

        waterIntakeManager = new WaterIntakeManager(user,progressBar,progressText,HealthRecord.this);
        waterIntakeManager.getWaterIntakeFromDatabase();

        buttonRecordHours = findViewById(R.id.BtnRecordHours);
        tvHoursRecord = findViewById(R.id.TVHoursRecord);

        sleepRecordManager = new SleepRecordManager(user,tvHoursRecord,HealthRecord.this);
        sleepRecordManager.getSleepDataFromDatabase();
        username = findViewById(R.id.TVUsername);
        username.setText(user.getUsername());

        buttonRecordHeightAndWeight = findViewById(R.id.btnRecordWeightHeight);
        tvheight = findViewById(R.id.TVUserHeight);
        tvweight = findViewById(R.id.TVUserWeight);
        tvUserBmi = findViewById(R.id.TVUserBmi);
        userWeightBMIManager = new UserWeightBMIManager(user,tvheight,tvweight,tvUserBmi,HealthRecord.this);
        userWeightBMIManager.getWeightBMIFromDatabase();

        tvUserCalorie = findViewById(R.id.TVUserCalorie);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_analytics);

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
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("user", user);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            } else if (item.getItemId() == R.id.bottom_profile) {
                Intent intent;
                if (isAdmin) {
                    intent = new Intent(HealthRecord.this, AdminProfile.class);
                } else {
                    intent = new Intent(HealthRecord.this, Profile.class);
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

        Toolbar toolbar = findViewById(R.id.toolbarHealthRecord);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_ios_24);
        if (upArrow != null) {
            DrawableCompat.setTint(upArrow, ContextCompat.getColor(this, R.color.black));
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        TextView dateTextView = findViewById(R.id.TVTodayDate);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
        String todayDate = dateFormat.format(calendar.getTime());

        dateTextView.setText(todayDate);

        Button viewHealthReport = findViewById(R.id.BtnViewHealthReport);
        viewHealthReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HealthRecord.this, ViewHealthReport.class);
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("user", user);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        Button addWaterButton = findViewById(R.id.add_water_button);
        addWaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int intakeAmount = 200;
                currentIntake += intakeAmount;

                if (currentIntake > dailyGoal) {
                    currentIntake = dailyGoal;
                }

                progressText.setText(currentIntake + " / " + dailyGoal);
                progressBar.setProgress(currentIntake);

                if (currentIntake == dailyGoal) {
                    Toast.makeText(getApplicationContext(), "Congratulations! You've reached your daily water goal!", Toast.LENGTH_SHORT).show();
                }

                waterIntakeManager.addWaterIntake(intakeAmount);
            }
        });

        buttonRecordHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSleepingHoursDialog();
            }
        });

        buttonRecordHeightAndWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHeightAndWeightDialog();
            }
        });

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Users_Exercise_History")
                .child(user.getUserId());

        fetchTodayCaloriesFromFirebase();

    }

    private void fetchTodayCaloriesFromFirebase() {
        SimpleDateFormat todayDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = todayDateFormat.format(Calendar.getInstance().getTime());

        final float[] totalCaloriesToday = {0f};

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());

                for (DataSnapshot exerciseSnapshot : dataSnapshot.getChildren()) {
                    String exerciseDate = exerciseSnapshot.child("exercise_date").getValue(String.class);
                    Float caloriesBurned = exerciseSnapshot.child("calories_burned").getValue(Float.class);

                    if (exerciseDate != null && caloriesBurned != null && caloriesBurned > 0) {
                        try {
                            Date parsedDate = isoDateFormat.parse(exerciseDate);

                            String formattedDate = todayDateFormat.format(parsedDate);

                            if (formattedDate.equals(todayDate)) {
                                totalCaloriesToday[0] += caloriesBurned;
                            }
                        } catch (Exception e) {
                            Log.e("ERROR", "Date parsing error: " + e.getMessage());
                        }
                    }
                }

                tvUserCalorie.setText(totalCaloriesToday[0] + "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                tvUserCalorie.setText("Error loading data: " + databaseError.getMessage());
            }
        });
    }

    private void showSleepingHoursDialog() {
        Dialog dialog = new Dialog(HealthRecord.this);
        dialog.setContentView(R.layout.record_sleeping_hours);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));

        EditText editTextDialogSleepingHours = dialog.findViewById(R.id.ETSleepingHours);
        Button buttonDialogRecordSleepingHours = dialog.findViewById(R.id.BtnRecord);

        buttonDialogRecordSleepingHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sleepingHoursInput = editTextDialogSleepingHours.getText().toString().trim();

                if (!sleepingHoursInput.isEmpty()) {
                    try {
                        int sleepingHours = Integer.parseInt(sleepingHoursInput);

                        if (sleepingHours < 0 || sleepingHours > 24) {
                            Toast.makeText(HealthRecord.this, "Please enter a valid number of hours (0-24)", Toast.LENGTH_SHORT).show();
                        } else {
                            tvHoursRecord.setText(String.valueOf(sleepingHours));
                            sleepRecordManager.storeSleepData(sleepingHours);
                            dialog.dismiss();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(HealthRecord.this, "Invalid input. Please enter a valid number.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HealthRecord.this, "Please enter your sleeping hours", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void showHeightAndWeightDialog() {
        Dialog dialog = new Dialog(HealthRecord.this);
        dialog.setContentView(R.layout.record_weight_and_height);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));

        EditText editTextHeight = dialog.findViewById(R.id.ETHeight);
        EditText editTextWeight = dialog.findViewById(R.id.ETWeight);
        Button buttonDialogRecordHeightAndWeight = dialog.findViewById(R.id.BtnRecordHeightAndWeight);

        buttonDialogRecordHeightAndWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String heightStr = editTextHeight.getText().toString().trim();
                String weightStr = editTextWeight.getText().toString().trim();

                if (heightStr.isEmpty() || weightStr.isEmpty()) {
                    Toast.makeText(HealthRecord.this, "Please enter both height and weight", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    int height = Integer.parseInt(heightStr);
                    int weight = Integer.parseInt(weightStr);

                    if (height < 50 || height > 300) {
                        Toast.makeText(HealthRecord.this, "Please enter a valid height between 50 and 300 cm", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (weight < 2 || weight > 500) {
                        Toast.makeText(HealthRecord.this, "Please enter a valid weight between 2 and 500 kg", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    tvheight.setText(heightStr.contains(".") ? heightStr : String.valueOf(height));
                    tvweight.setText(weightStr.contains(".") ? weightStr : String.valueOf(weight));

                    double heightInMeters = height / 100.0;

                    double bmi = weight / (heightInMeters * heightInMeters);

                    tvUserBmi.setText(String.format("%.1f", bmi));

                    userWeightBMIManager.storeUserWeightBMI(height, weight);

                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    Toast.makeText(HealthRecord.this, "Please enter valid integers for height and weight", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}