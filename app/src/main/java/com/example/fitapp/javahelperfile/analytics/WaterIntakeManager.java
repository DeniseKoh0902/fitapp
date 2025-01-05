package com.example.fitapp.javahelperfile.analytics;

import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitapp.javahelperfile.profile.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class WaterIntakeManager {

    private static final int DAILY_WATER_GOAL = 1600;
    private DatabaseReference databaseReference;
    private String userId;
    private ProgressBar progressBar;
    private TextView progressText;
    private Context context;

    public WaterIntakeManager(User user, ProgressBar progressBar, TextView progressText, Context context) {
        this.context = context;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userId= user.getUserId();
        this.progressBar = progressBar;
        this.progressText = progressText;
    }

    public void addWaterIntake(int intakeAmount) {
        String currentDateTime = getCurrentISODateTime();

        databaseReference.child("Water_Intake").orderByChild(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean entryExists = false;
                String existingKey = null;
                int existingIntake = 0;

                for (DataSnapshot intakeSnapshot : snapshot.getChildren()) {
                    HashMap<String, Object> data = (HashMap<String, Object>) intakeSnapshot.child(userId).getValue();
                    if (data != null && data.get("water_datetime") != null) {
                        String existingDateTime = (String) data.get("water_datetime");
                        String existingDate = existingDateTime.substring(0, 10);

                        if (existingDate.equals(currentDateTime.substring(0, 10))) {
                            entryExists = true;
                            existingKey = intakeSnapshot.getKey();
                            existingIntake = ((Long) data.get("water_intake")).intValue();
                            break;
                        }
                    }
                }

                if (entryExists && existingKey != null) {
                    int newIntake = Math.min(existingIntake + intakeAmount, DAILY_WATER_GOAL);
                    updateWaterIntake(existingKey, currentDateTime, newIntake);
                } else {
                    int newIntake = Math.min(intakeAmount, DAILY_WATER_GOAL);
                    addNewWaterIntake(currentDateTime, newIntake);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("WaterIntakeManager", "Error checking water intake: " + error.getMessage());
            }
        });
    }

    private void addNewWaterIntake(String dateTime, int intakeAmount) {
        String waterIntakeId = databaseReference.child("Water_Intake").push().getKey();
        if (waterIntakeId != null) {
            HashMap<String, Object> waterIntakeData = new HashMap<>();
            waterIntakeData.put("water_datetime", dateTime);
            waterIntakeData.put("water_intake", intakeAmount);

            databaseReference.child("Water_Intake").child(waterIntakeId).child(userId).setValue(waterIntakeData)
                    .addOnSuccessListener(aVoid -> Log.d("WaterIntakeManager", "Water intake added successfully"))
                    .addOnFailureListener(e -> Log.e("WaterIntakeManager", "Failed to add water intake: " + e.getMessage()));
        }
    }

    private void updateWaterIntake(String key, String dateTime, int intakeAmount) {
        HashMap<String, Object> updatedData = new HashMap<>();
        updatedData.put("water_datetime", dateTime);
        updatedData.put("water_intake", intakeAmount);

        databaseReference.child("Water_Intake").child(key).child(userId).updateChildren(updatedData)
                .addOnSuccessListener(aVoid -> Log.d("WaterIntakeManager", "Water intake updated successfully"))
                .addOnFailureListener(e -> Log.e("WaterIntakeManager", "Failed to update water intake: " + e.getMessage()));
    }

    private String getCurrentISODateTime() {
        Date now = new Date();

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        return isoFormat.format(now);
    }

    public void getWaterIntakeFromDatabase() {
        String currentDate = getCurrentISODateTime().substring(0, 10);

        databaseReference.child("Water_Intake")
                .orderByChild(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Log.d("WaterIntakeManager", "Number of children in snapshot: " + snapshot.getChildrenCount());

                        int totalIntake = 0;
                        boolean dataFoundForToday = false;

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Log.d("WaterIntakeManager", "Data Snapshot: " + dataSnapshot.getKey());

                            String waterDateTime = dataSnapshot.child(userId).child("water_datetime").getValue(String.class);
                            if (waterDateTime != null && waterDateTime.length() >= 10) {
                                String recordDate = waterDateTime.substring(0, 10);
                                if (recordDate.equals(currentDate)) {
                                    Integer waterIntake = dataSnapshot.child(userId).child("water_intake").getValue(Integer.class);
                                    if (waterIntake != null) {
                                        totalIntake += waterIntake;
                                        dataFoundForToday = true;
                                    }
                                }
                            }

                        }

                        if (!dataFoundForToday) {
                            totalIntake = 0;
                        }

                        updateUIWithWaterIntake(totalIntake);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(context, "Failed to read water intake data: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateUIWithWaterIntake(int totalIntake) {
        progressText.post(new Runnable() {
            @Override
            public void run() {

                progressText.setText(totalIntake + " / " + DAILY_WATER_GOAL);
                progressBar.setProgress(totalIntake);
            }
        });
    }



}