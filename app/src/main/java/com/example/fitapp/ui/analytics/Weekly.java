package com.example.fitapp.ui.analytics;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.fitapp.R;
import com.example.fitapp.javahelperfile.profile.User;
import com.example.fitapp.ui.admin.AdminProfile;
import com.example.fitapp.ui.home.MainActivity;
import com.example.fitapp.ui.profile.Profile;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class Weekly extends AppCompatActivity {

    private BarChart barChart;
    private TextView tvTotalCalories;
    private DatabaseReference databaseReference;
    private ArrayList<BarEntry> barEntries = new ArrayList<>();
    private ArrayList<String> labels = new ArrayList<>();
    private HashMap<String, Float> calorieData = new HashMap<>();
    private User user;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly);

        user = getIntent().getParcelableExtra("user");
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        if (user == null) {
            Toast.makeText(this, "Error: User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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
                    intent = new Intent(Weekly.this, AdminProfile.class);
                } else {
                    intent = new Intent(Weekly.this, Profile.class);
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

        Toolbar toolbar = findViewById(R.id.toolbarWeeklyAnalytics);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_ios_24);
        if (upArrow != null) {
            DrawableCompat.setTint(upArrow, ContextCompat.getColor(this, R.color.black));
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        barChart = findViewById(R.id.barChartView);
        tvTotalCalories = findViewById(R.id.tvTotalCaloriesBurnt);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(this, "Error: User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Users_Exercise_History")
                .child(user.getUserId());

        fetchDataFromFirebase();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchDataFromFirebase() {
        SimpleDateFormat labelDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        labelDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
        SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        isoDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));

        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        Date startOfWeek = calendar.getTime();
        String startOfWeekStr = labelDateFormat.format(startOfWeek);

        calendar.add(Calendar.DAY_OF_WEEK, 6);
        Date endOfWeek = calendar.getTime();
        String endOfWeekStr = labelDateFormat.format(endOfWeek);

        labels.clear();
        calorieData.clear();

        calendar.setTime(startOfWeek);
        for (int i = 0; i < 7; i++) {
            labels.add(new SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.getTime()));
            calorieData.put(labelDateFormat.format(calendar.getTime()), 0f);
            calendar.add(Calendar.DATE, 1);
        }

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot exerciseSnapshot : dataSnapshot.getChildren()) {
                    String exerciseDate = exerciseSnapshot.child("exercise_date").getValue(String.class);
                    Float caloriesBurned = exerciseSnapshot.child("calories_burned").getValue(Float.class);

                    if (exerciseDate != null && caloriesBurned != null && caloriesBurned > 0) {
                        try {
                            Date parsedDate = isoDateFormat.parse(exerciseDate);
                            if (parsedDate != null) {
                                String dateKey = labelDateFormat.format(parsedDate);

                                // Only include data within the current week
                                if (dateKey.compareTo(startOfWeekStr) >= 0 && dateKey.compareTo(endOfWeekStr) <= 0) {
                                    if (calorieData.containsKey(dateKey)) {
                                        float currentCalories = calorieData.get(dateKey);
                                        calorieData.put(dateKey, currentCalories + caloriesBurned);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.e("ERROR", "Date parsing error: " + e.getMessage());
                        }
                    }
                }
                populateBarChart();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                tvTotalCalories.setText("Error loading data: " + databaseError.getMessage());
            }
        });
    }


    private void populateBarChart() {
        barEntries.clear();
        ArrayList<String> dynamicLabels = new ArrayList<>();
        float totalCalories = 0;
        int index = 0;

        String[] weekDays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        HashMap<String, Float> orderedData = new HashMap<>();

        for (String day : weekDays) {
            orderedData.put(day, 0f);
        }

        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        dayOfWeekFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
        for (String date : calorieData.keySet()) {
            try {
                Date parsedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date);
                if (parsedDate != null) {
                    String dayOfWeek = dayOfWeekFormat.format(parsedDate);
                    if (orderedData.containsKey(dayOfWeek)) {
                        orderedData.put(dayOfWeek, orderedData.get(dayOfWeek) + calorieData.get(date));
                    }
                }
            } catch (Exception e) {
                Log.e("ERROR", "Date parsing error: " + e.getMessage());
            }
        }

        for (String day : weekDays) {
            float calories = orderedData.get(day);
            dynamicLabels.add(day);
            barEntries.add(new BarEntry(index++, calories));
            totalCalories += calories;
        }

        BarDataSet dataSet = new BarDataSet(barEntries, "Calories Burned");
        dataSet.setColor(getResources().getColor(R.color.yellow));
        dataSet.setValueTextColor(getResources().getColor(R.color.black));
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        barChart.setData(barData);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dynamicLabels));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.getAxisLeft().setAxisMinimum(0);
        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.invalidate();

        tvTotalCalories.setText(String.format(Locale.getDefault(), "Total Calories Burned: %.2f", totalCalories));
    }
}
