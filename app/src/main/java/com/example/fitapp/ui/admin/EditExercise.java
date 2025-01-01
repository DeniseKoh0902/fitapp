package com.example.fitapp.ui.admin;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bumptech.glide.Glide;
import com.example.fitapp.R;
import com.example.fitapp.javahelperfile.profile.User;
import com.example.fitapp.ui.analytics.Analytics;
import com.example.fitapp.ui.home.MainActivity;
import com.example.fitapp.ui.profile.Profile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditExercise extends AppCompatActivity {

    private User user;
    private boolean isAdmin;
    private EditText etCalories;
    private DatabaseReference databaseReference;

    private EditText etExerciseName, etDuration, etCaloriesBurned;
    private ImageView ivEditIcon, ivExercisePicture;
    private Button btnChangePicture, btnSaveChanges;
    private String exerciseId; // To save the ID of the exercise
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exercise);

        // Retrieve User object
        user = getIntent().getParcelableExtra("user");
        // Retrieve the isAdmin flag
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        // Initialize views
        etExerciseName = findViewById(R.id.ETExercise);
        etDuration = findViewById(R.id.ETTimer);
        etCaloriesBurned = findViewById(R.id.ETCalorie);
        ivEditIcon = findViewById(R.id.IVEditIcon);
        ivExercisePicture = findViewById(R.id.IVExercisePic);
        btnChangePicture = findViewById(R.id.btnChangePic);
        btnSaveChanges = findViewById(R.id.BtnSaveChanges);


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

        // Inside the onCreate method of EditExercise Activity
        String exerciseName = getIntent().getStringExtra("exercise_name");
        int exerciseDuration = getIntent().getIntExtra("exercise_duration", 0);
        int exerciseImage = getIntent().getIntExtra("exercise_image", R.drawable.jogging);

        // Fetch the exerciseId from Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Exercises");
        fetchExerciseId(exerciseName);

        etExerciseName.setText(exerciseName); // Display exercise name

        int minutes = exerciseDuration;
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;
        int seconds = 0; // You can set seconds to 0 if you don't have a specific value

        // Set the formatted time in HH:MM:SS format
        etDuration.setText(String.format("%02d:%02d:%02d", hours, remainingMinutes, seconds));

        ImageView ivExerciseImage = findViewById(R.id.cardView).findViewById(R.id.IVExercisePic);
        ivExerciseImage.setImageResource(exerciseImage); // Set exercise image

        // Upper Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarEditExercise);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_ios_24);
        if (upArrow != null) {
            DrawableCompat.setTint(upArrow, ContextCompat.getColor(this, R.color.black)); // Set your desired color
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        Button BtnSaveChanges = findViewById(R.id.BtnSaveChanges);
        BtnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the NextActivity
                Intent intent = new Intent(EditExercise.this, ChooseExerciseToEdit.class);
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("user", user); // Attach the Parcelable User object
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // Optional transition
            }
        });

        // Reference to the Firebase database
        databaseReference = FirebaseDatabase.getInstance().getReference("Exercises");

        // Query to find the exerciseId based on exerciseName
        databaseReference.orderByChild("exerciseName").equalTo(exerciseName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                String exerciseId = childSnapshot.getKey(); // Get the exerciseId (node key)

                                // Fetch and display the caloriesBurned
                                int caloriesBurned = childSnapshot.child("caloriesBurned").getValue(Integer.class);
                                etCaloriesBurned.setText(String.valueOf(caloriesBurned));

                                // Log or use the exerciseId if needed
                                System.out.println("Exercise ID: " + exerciseId);
                                break; // Exit loop after finding the first match
                            }
                        } else {
                            Toast.makeText(EditExercise.this, "Exercise not found!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(EditExercise.this, "Failed to query data!", Toast.LENGTH_SHORT).show();
                    }
                });

        // Enable editing for the exercise name
        ivEditIcon.setOnClickListener(v -> {
            etExerciseName.setFocusableInTouchMode(true);
            etExerciseName.setFocusable(true);
        });

        btnSaveChanges.setOnClickListener(v -> saveChanges());
        btnChangePicture.setOnClickListener(v -> openImagePicker());
    }

    // for toolbar use to return to previous page
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Save changes to Firebase.
     */
    private void saveChanges() {
        String updatedName = etExerciseName.getText().toString();
        String updatedDuration = etDuration.getText().toString();
        int updatedCalories = Integer.parseInt(etCaloriesBurned.getText().toString());

        String[] timeParts = updatedDuration.split(":");
        int updatedDurationMinutes = Integer.parseInt(timeParts[0]) * 60 + Integer.parseInt(timeParts[1]);

        if (exerciseId != null) {
            DatabaseReference updateRef = databaseReference.child(exerciseId);
            updateRef.child("exerciseName").setValue(updatedName);
            updateRef.child("exerciseDuration").setValue(updatedDurationMinutes);
            updateRef.child("caloriesBurned").setValue(updatedCalories)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditExercise.this, "Exercise updated successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditExercise.this, "Failed to update exercise!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Exercise ID not found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchExerciseId(String exerciseName) {
        databaseReference.orderByChild("exerciseName").equalTo(exerciseName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                exerciseId = childSnapshot.getKey(); // Get exerciseId
                                int caloriesBurned = childSnapshot.child("caloriesBurned").getValue(Integer.class);
                                etCaloriesBurned.setText(String.valueOf(caloriesBurned));
                                break; // Exit loop after finding first match
                            }
                        } else {
                            Toast.makeText(EditExercise.this, "Exercise not found!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(EditExercise.this, "Failed to query data!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static final int PICK_IMAGE_REQUEST = 1;

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            ivExercisePicture.setImageURI(imageUri); // Display image immediately (optional)

            String imageUrl = String.valueOf(imageUri);
            updateExerciseImage(imageUrl);
        }
    }

    private void updateExerciseImage(String imageUrl) {
        if (exerciseId != null) {
            DatabaseReference exerciseRef = FirebaseDatabase.getInstance().getReference("Exercises").child(exerciseId);
            exerciseRef.child("exercisePicPath").setValue(imageUrl)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditExercise.this, "Exercise picture updated successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditExercise.this, "Failed to update exercise picture!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(EditExercise.this, "Exercise ID not found!", Toast.LENGTH_SHORT).show();
        }
    }

}