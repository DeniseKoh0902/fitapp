package com.example.fitapp.javahelperfile.analytics;
import android.content.Context;
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

public class SleepRecordManager {

    private DatabaseReference databaseReference;
    private Context context;
    private String userId;
    private TextView tvHoursRecord;

    public SleepRecordManager(User user, TextView tvHoursRecord, Context context) {
        userId=user.getUserId();
        this.tvHoursRecord=tvHoursRecord;
        this.context = context;
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void storeSleepData(int sleepHours) {
        String currentDate = getCurrentISODateTime().substring(0, 10);

        databaseReference.child("Sleep_Record")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        boolean recordExists = false;
                        String recordKey = null;

                        for (DataSnapshot recordSnapshot : snapshot.getChildren()) {
                            DataSnapshot userSnapshot = recordSnapshot.child(userId);

                            if (userSnapshot.exists()) {
                                String recordDate = userSnapshot.child("sleeprecord_date").getValue(String.class);

                                if (recordDate != null && recordDate.startsWith(currentDate)) {
                                    recordExists = true;
                                    recordKey = recordSnapshot.getKey();
                                }
                            }
                        }

                        if (recordExists && recordKey != null) {
                            updateSleepData(recordKey, sleepHours);
                        } else {
                            addNewSleepData(sleepHours);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(context, "Failed to check existing sleep data: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void addNewSleepData(int sleepHours) {
        String sleepRecordDate = getCurrentISODateTime();

        String recordKey = databaseReference.child("Sleep_Record").push().getKey();

        if (recordKey != null) {
            HashMap<String, Object> sleepData = new HashMap<>();
            sleepData.put("sleeprecord_date", sleepRecordDate);
            sleepData.put("sleep_hours", sleepHours);

            databaseReference.child("Sleep_Record").child(recordKey).child(userId)
                    .setValue(sleepData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Sleep data saved successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to save sleep data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateSleepData(String recordKey, int sleepHours) {
        String sleepRecordDate = getCurrentISODateTime();

        HashMap<String, Object> updatedSleepData = new HashMap<>();
        updatedSleepData.put("sleeprecord_date", sleepRecordDate);
        updatedSleepData.put("sleep_hours", sleepHours);

        databaseReference.child("Sleep_Record").child(recordKey).child(userId)
                .updateChildren(updatedSleepData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Sleep data updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to update sleep data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getSleepDataFromDatabase() {
        String currentDate = getCurrentISODateTime().substring(0, 10);

        databaseReference.child("Sleep_Record")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        int totalSleepHours = 0;
                        boolean dataFoundForToday = false;

                        for (DataSnapshot sleepRecordSnapshot : snapshot.getChildren()) {
                            DataSnapshot userSnapshot = sleepRecordSnapshot.child(userId);

                            if (userSnapshot.exists()) {
                                String recordDateTime = userSnapshot.child("sleeprecord_date").getValue(String.class);

                                if (recordDateTime != null && recordDateTime.startsWith(currentDate)) {
                                    Integer sleepHours = userSnapshot.child("sleep_hours").getValue(Integer.class);

                                    if (sleepHours != null) {
                                        totalSleepHours += sleepHours;
                                        dataFoundForToday = true;
                                    }
                                }
                            }
                        }

                        if (dataFoundForToday) {
                            updateUIWithSleepData(totalSleepHours);
                        } else {
                            updateUIWithSleepData(0);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(context, "Failed to read sleep data: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void updateUIWithSleepData(int totalSleepHours) {
        tvHoursRecord.setText(String.valueOf(totalSleepHours));
    }

    private String getCurrentISODateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return sdf.format(new Date());
    }
}