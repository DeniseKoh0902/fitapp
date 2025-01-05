

package com.example.fitapp.ui.analytics;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.fitapp.R;
import com.example.fitapp.javahelperfile.profile.User;
import com.example.fitapp.ui.admin.AdminProfile;
import com.example.fitapp.ui.home.MainActivity;
import com.example.fitapp.ui.profile.Profile;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
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

public class Monthly extends AppCompatActivity {

    private PieChart pieChart;
    private TextView tvTotalCalories;
    private DatabaseReference databaseReference;
    private HashMap<String, Float> calorieData = new HashMap<>();

    private User user;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly);

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
                    intent = new Intent(Monthly.this, AdminProfile.class);
                } else {
                    intent = new Intent(Monthly.this, Profile.class);
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

        Toolbar toolbar = findViewById(R.id.toolbarMonthlyAnalytics);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_ios_24);
        if (upArrow != null) {
            DrawableCompat.setTint(upArrow, ContextCompat.getColor(this, R.color.black));
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        pieChart = findViewById(R.id.pieChartView);
        tvTotalCalories = findViewById(R.id.tvTotalCalories);

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
        SimpleDateFormat firebaseDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        firebaseDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));

        Calendar currentCalendar = Calendar.getInstance();
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int currentYear = currentCalendar.get(Calendar.YEAR);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float totalCalories = 0;

                for (DataSnapshot exerciseSnapshot : dataSnapshot.getChildren()) {
                    String exerciseDate = exerciseSnapshot.child("exercise_date").getValue(String.class);
                    Float caloriesBurned = exerciseSnapshot.child("calories_burned").getValue(Float.class);

                    if (exerciseDate != null && caloriesBurned != null) {
                        try {
                            Date parsedDate = firebaseDateFormat.parse(exerciseDate);

                            Calendar exerciseCalendar = Calendar.getInstance();
                            exerciseCalendar.setTime(parsedDate);

                            int exerciseMonth = exerciseCalendar.get(Calendar.MONTH);
                            int exerciseYear = exerciseCalendar.get(Calendar.YEAR);

                            if (exerciseMonth == currentMonth && exerciseYear == currentYear) {
                                totalCalories += caloriesBurned;

                                int weekOfMonth = exerciseCalendar.get(Calendar.WEEK_OF_MONTH);
                                String weekKey = "Week " + weekOfMonth;
                                calorieData.put(weekKey, calorieData.getOrDefault(weekKey, 0f) + caloriesBurned);
                            }
                        } catch (Exception e) {
                            Log.e("ERROR", "Date parsing error: " + e.getMessage());
                        }
                    }
                }

                tvTotalCalories.setText("Total Calories Burnt: " + totalCalories + " cal");
                populatePieChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tvTotalCalories.setText("Error loading data: " + databaseError.getMessage());
            }
        });
    }

    private void populatePieChart() {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        float totalCalories = 0;

        for (String week : calorieData.keySet()) {
            totalCalories += calorieData.get(week);
        }

        if (totalCalories == 0) {
            tvTotalCalories.setText("No calories burned this month.");
            return;
        }

        for (String week : calorieData.keySet()) {
            float weekCalories = calorieData.get(week);
            float percentage = (weekCalories / totalCalories) * 100;

            pieEntries.add(new PieEntry(percentage, week));
            colors.add(getColorForWeek(week));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, " ");
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextSize(24f);
        pieDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.black));

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(false);
        pieChart.invalidate();

        pieChart.setEntryLabelColor(ContextCompat.getColor(this, R.color.black));

        pieChart.getLegend().setTextColor(ContextCompat.getColor(this, R.color.black));

        tvTotalCalories.setText("Total Calories Burnt: " + totalCalories + " cal");
    }

    private int getColorForWeek(String week) {
        switch (week) {
            case "Week 1":
                return ContextCompat.getColor(this, R.color.orange);
            case "Week 2":
                return ContextCompat.getColor(this, R.color.yellow);
            case "Week 3":
                return ContextCompat.getColor(this, R.color.paleyellow);
            case "Week 4":
                return ContextCompat.getColor(this, R.color.morepaleorange);
            default:
                return ContextCompat.getColor(this, R.color.morepaleyellow);
        }
    }
}



