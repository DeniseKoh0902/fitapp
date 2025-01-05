package com.example.fitapp.javahelperfile.analytics;

import android.content.Context;
import android.util.Log;
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

public class UserWeightBMIManager {

    private DatabaseReference databaseReference;
    private Context context;
    private String userId;

    private TextView tvheight,tvweight,tvUserBmi;

    public UserWeightBMIManager(User user, TextView tvheight, TextView tvweight, TextView tvUserBmi, Context context) {
        this.context = context;
        this.tvheight = tvheight;
        this.tvweight = tvweight;
        this.tvUserBmi = tvUserBmi;
        this.userId = user.getUserId();
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void storeUserWeightBMI(int height, int weight) {
        String currentDateTime = getCurrentISODateTime();

        databaseReference.child("User_Weight_BMI")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        boolean recordExists = false;
                        String recordKey = null;

                        for (DataSnapshot recordSnapshot : snapshot.getChildren()) {
                            DataSnapshot userSnapshot = recordSnapshot.child(userId);

                            if (userSnapshot.exists()) {
                                String recordedDate = userSnapshot.child("recorded_date").getValue(String.class);

                                if (recordedDate != null && recordedDate.startsWith(currentDateTime.substring(0, 10))) {
                                    recordExists = true;
                                    recordKey = recordSnapshot.getKey();
                                    break;
                                }
                            }
                        }

                        if (recordExists && recordKey != null) {
                            updateUserWeightBMI(recordKey, height, weight);
                        } else {
                            addNewUserWeightBMI(height, weight);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(context, "Failed to check existing weight data: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void addNewUserWeightBMI(int height, int weight) {
        String currentDateTime = getCurrentISODateTime();
        String recordKey = databaseReference.child("User_Weight_BMI").push().getKey();

        if (recordKey != null) {
            HashMap<String, Object> weightBMIData = new HashMap<>();
            weightBMIData.put("recorded_date", currentDateTime);
            weightBMIData.put("user_height", height);
            weightBMIData.put("user_weight", weight);

            databaseReference.child("User_Weight_BMI").child(recordKey).child(userId)
                    .setValue(weightBMIData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Weight/BMI data saved successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to save weight/BMI data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateUserWeightBMI(String recordKey, int height, int weight) {
        String currentDateTime = getCurrentISODateTime();
        HashMap<String, Object> updatedData = new HashMap<>();
        updatedData.put("recorded_date", currentDateTime);
        updatedData.put("user_height", height);
        updatedData.put("user_weight", weight);

        databaseReference.child("User_Weight_BMI").child(recordKey).child(userId)
                .updateChildren(updatedData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Weight/BMI data updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to update weight/BMI data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getWeightBMIFromDatabase() {
        String currentDate = getCurrentISODateTime().substring(0, 10);
        Log.d("Current Date", "Current Date: " + currentDate);

        databaseReference.child("User_Weight_BMI")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Log.d("FirebaseData", "Data snapshot: " + snapshot.toString());
                        boolean dataFoundForToday = false;

                        for (DataSnapshot recordSnapshot : snapshot.getChildren()) {
                            DataSnapshot userSnapshot = recordSnapshot.child(userId);

                            if (userSnapshot.exists()) {
                                String recordDateTime = userSnapshot.child("recorded_date").getValue(String.class);
                                if (recordDateTime != null) {
                                    String recordedDate = recordDateTime.substring(0, 10);

                                    if (recordedDate.equals(currentDate)) {
                                        Integer userHeight = userSnapshot.child("user_height").getValue(Integer.class);
                                        Integer userWeight = userSnapshot.child("user_weight").getValue(Integer.class);

                                        if (userHeight != null && userWeight != null) {
                                            updateUIWithWeightBMIData(userHeight, userWeight);
                                            dataFoundForToday = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        if (!dataFoundForToday) {
                            updateUIWithWeightBMIData(0, 0);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(context, "Failed to read weight and BMI data: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateUIWithWeightBMIData(int height, int weight) {
        tvheight.setText(String.valueOf(height));
        tvweight.setText(String.valueOf(weight));
        double heightInMeters = height / 100.0;

        double bmi = weight / (heightInMeters * heightInMeters);

        tvUserBmi.setText(String.format("%.1f", bmi));
    }

    private String getCurrentISODateTime() {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        return isoFormat.format(new Date());
    }
}
