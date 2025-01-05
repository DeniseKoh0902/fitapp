package com.example.fitapp.ui.analytics;

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

import com.example.fitapp.ui.admin.AdminProfile;
import com.example.fitapp.ui.home.MainActivity;
import com.example.fitapp.R;
import com.example.fitapp.javahelperfile.profile.User;
import com.example.fitapp.ui.profile.Profile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ViewHealthReport extends AppCompatActivity {

    private User user;
    private FirebaseDatabase database;
    private DatabaseReference waterIntakeRef;

    private String userId;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_health_report);

        user = getIntent().getParcelableExtra("user");
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        userId=user.getUserId();

        database = FirebaseDatabase.getInstance();
        waterIntakeRef = database.getReference("Water_Intake");

        getAverageWaterIntake(userId);
        getAverageSleepHours(userId);
        getTotalWeightReduction(userId);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_analytics);

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
            } else if (item.getItemId() == R.id.bottom_profile) {
                Intent intent;
                if (isAdmin) {
                    intent = new Intent(ViewHealthReport.this, AdminProfile.class);
                } else {
                    intent = new Intent(ViewHealthReport.this, Profile.class);
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

        Toolbar toolbar = findViewById(R.id.toolbarHealthReport);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_ios_24);
        if (upArrow != null) {
            DrawableCompat.setTint(upArrow, ContextCompat.getColor(this, R.color.black)); // Set your desired color
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

    private void getAverageWaterIntake(String userId) {
        DatabaseReference userWaterRef = waterIntakeRef;

        userWaterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalWaterIntake = 0;
                int count = 0;

                Calendar calendar = Calendar.getInstance();
                int currentMonth = calendar.get(Calendar.MONTH) + 1;
                int currentYear = calendar.get(Calendar.YEAR);

                Log.d("WaterIntake", "Current Month: " + currentMonth + ", Current Year: " + currentYear);

                if (!dataSnapshot.exists()) {
                    Log.d("WaterIntake", "No records found.");
                    return;
                }

                for (DataSnapshot waterIntakeSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot userSnapshot : waterIntakeSnapshot.getChildren()) {
                        if (userSnapshot.getKey() != null && userSnapshot.getKey().equals(userId)) {
                            Integer waterIntake = userSnapshot.child("water_intake").getValue(Integer.class);
                            String waterDatetime = userSnapshot.child("water_datetime").getValue(String.class);

                            if (waterIntake != null && waterDatetime != null) {
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                                    Date date = sdf.parse(waterDatetime);

                                    if (date != null) {
                                        Calendar recordCalendar = Calendar.getInstance();
                                        recordCalendar.setTime(date);

                                        int recordMonth = recordCalendar.get(Calendar.MONTH) + 1;
                                        int recordYear = recordCalendar.get(Calendar.YEAR);

                                        Log.d("WaterIntake", "Record Month: " + recordMonth + ", Record Year: " + recordYear);

                                        if (recordMonth == currentMonth && recordYear == currentYear) {
                                            totalWaterIntake += waterIntake;
                                            count++;
                                        }
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

                Log.d("WaterIntake", "Total: " + totalWaterIntake + ", Count: " + count);

                if (count > 0) {
                    int averageWaterIntake = totalWaterIntake / count;
                    displayAverageWaterIntake(averageWaterIntake);
                } else {
                    displayAverageWaterIntake(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error retrieving water intake data", databaseError.toException());
            }
        });
    }

    private void displayAverageWaterIntake(int averageWaterIntake) {
        TextView averageWaterIntakeText = findViewById(R.id.TVMonthlyAverageWaterIntake);
        String averageText = averageWaterIntake + " ml";
        averageWaterIntakeText.setText(averageText);
    }

    private void getAverageSleepHours(String userId) {
        DatabaseReference sleepRecordRef = FirebaseDatabase.getInstance().getReference("Sleep_Record");

        sleepRecordRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalSleepHours = 0;
                int count = 0;

                Calendar calendar = Calendar.getInstance();
                int currentMonth = calendar.get(Calendar.MONTH) + 1;
                int currentYear = calendar.get(Calendar.YEAR);

                Log.d("SleepRecord", "Current Month: " + currentMonth + ", Current Year: " + currentYear);

                if (!dataSnapshot.exists()) {
                    Log.d("SleepRecord", "No records found.");
                    return;
                }

                for (DataSnapshot sleepRecordSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot userSnapshot : sleepRecordSnapshot.getChildren()) {
                        if (userSnapshot.getKey() != null && userSnapshot.getKey().equals(userId)) {
                            Integer sleepHours = userSnapshot.child("sleep_hours").getValue(Integer.class);
                            String sleepRecordDate = userSnapshot.child("sleeprecord_date").getValue(String.class);

                            if (sleepHours != null && sleepRecordDate != null) {
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                                    Date date = sdf.parse(sleepRecordDate);

                                    if (date != null) {
                                        Calendar recordCalendar = Calendar.getInstance();
                                        recordCalendar.setTime(date);

                                        int recordMonth = recordCalendar.get(Calendar.MONTH) + 1;
                                        int recordYear = recordCalendar.get(Calendar.YEAR);

                                        Log.d("SleepRecord", "Record Month: " + recordMonth + ", Record Year: " + recordYear);

                                        if (recordMonth == currentMonth && recordYear == currentYear) {
                                            totalSleepHours += sleepHours;
                                            count++;
                                        }
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

                Log.d("SleepRecord", "Total: " + totalSleepHours + ", Count: " + count);

                if (count > 0) {
                    float averageSleepHours = (float) totalSleepHours / count;

                    displayAverageSleepHours(averageSleepHours);
                } else {
                    displayAverageSleepHours(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("SleepRecord", "Error retrieving sleep data", databaseError.toException());
            }
        });
    }

    private void displayAverageSleepHours(float averageSleepHours) {
        DecimalFormat decimalFormat = new DecimalFormat("#.0");
        String formattedAverage = decimalFormat.format(averageSleepHours);

        TextView averageSleepHoursText = findViewById(R.id.TVMonthlyAverageSleepingHours);
        String averageText = formattedAverage + " hours";
        averageSleepHoursText.setText(averageText);
    }

    private void getTotalWeightReduction(String userId) {
        DatabaseReference weightBmiRef = FirebaseDatabase.getInstance().getReference("User_Weight_BMI");

        weightBmiRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float firstWeight = -1;
                float lastWeight = -1;
                float totalWeightReduction = 0;

                Calendar calendar = Calendar.getInstance();
                int currentMonth = calendar.get(Calendar.MONTH) + 1;
                int currentYear = calendar.get(Calendar.YEAR);

                Log.d("WeightRecord", "Current Month: " + currentMonth + ", Current Year: " + currentYear);

                if (!dataSnapshot.exists()) {
                    Log.d("WeightRecord", "No records found.");
                    return;
                }

                for (DataSnapshot weightBmiSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot userSnapshot : weightBmiSnapshot.getChildren()) {
                        if (userSnapshot.getKey() != null && userSnapshot.getKey().equals(userId)) {
                            Float userWeight = userSnapshot.child("user_weight").getValue(Float.class);
                            String recordedDate = userSnapshot.child("recorded_date").getValue(String.class);

                            if (userWeight != null && recordedDate != null) {
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                                    Date date = sdf.parse(recordedDate);

                                    if (date != null) {
                                        Calendar recordCalendar = Calendar.getInstance();
                                        recordCalendar.setTime(date);

                                        int recordMonth = recordCalendar.get(Calendar.MONTH) + 1;
                                        int recordYear = recordCalendar.get(Calendar.YEAR);

                                        Log.d("WeightRecord", "Record Month: " + recordMonth + ", Record Year: " + recordYear);

                                        if (recordMonth == currentMonth && recordYear == currentYear) {
                                            if (firstWeight == -1) {
                                                firstWeight = userWeight;
                                            }
                                            lastWeight = userWeight;
                                        }
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

                if (firstWeight != -1 && lastWeight != -1) {
                    if (lastWeight < firstWeight) {
                        totalWeightReduction = firstWeight - lastWeight;
                        displayTotalWeightReduction(totalWeightReduction);
                    } else {
                        displayTotalWeightReduction(0);
                    }
                } else {
                    displayTotalWeightReduction(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("WeightRecord", "Error retrieving weight data", databaseError.toException());
            }
        });
    }

    private void displayTotalWeightReduction(float weightReduction) {
        int weightReductionInt = (int) weightReduction;

        TextView totalWeightReductionText = findViewById(R.id.TVMonthlyWeightReduce);
        String reductionText = "-"+weightReductionInt + " kg";
        totalWeightReductionText.setText(reductionText);
    }




}
