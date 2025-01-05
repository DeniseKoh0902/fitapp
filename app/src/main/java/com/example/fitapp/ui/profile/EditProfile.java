package com.example.fitapp.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fitapp.ui.admin.AdminProfile;
import com.example.fitapp.ui.analytics.Analytics;
import com.example.fitapp.ui.home.MainActivity;
import com.example.fitapp.R;
import com.example.fitapp.javahelperfile.profile.User;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfile extends AppCompatActivity {

    private Button updateProfileBtn, saveButton;
    private ImageView profilePic;
    private Uri selectedImageUri;
    private DatabaseReference userRef;
    private EditText username, dob, phone;
    private User user;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        user = getIntent().getParcelableExtra("user");
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        if (user != null) {
            username = findViewById(R.id.username);
            EditText userType = findViewById(R.id.usertype);
            dob = findViewById(R.id.dob);
            EditText email = findViewById(R.id.emailadd);
            phone = findViewById(R.id.phone);
            EditText points = findViewById(R.id.point);

            username.setText("            "+user.getUsername());
            dob.setText("            "+user.getDob());
            email.setText("            "+user.getEmail());
            phone.setText("            "+user.getPhone());

            DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("User")
                    .child(user.getUserId());

            userRef.child("user_points").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Integer updatedPoints = snapshot.getValue(Integer.class);
                        if (updatedPoints != null) {
                            points.setText("            "+updatedPoints + " points");

                            String newUserType = calculateUserType(updatedPoints);
                            userType.setText("            "+newUserType);

                            userRef.child("user_types").setValue(newUserType)
                                    .addOnSuccessListener(aVoid -> Log.d("FirebaseUpdate", "User type updated successfully"))
                                    .addOnFailureListener(e -> Log.e("FirebaseUpdate", "Failed to update user type: " + e.getMessage()));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("MainActivity", "Failed to retrieve user points: " + error.getMessage());
                }
            });


            disableEditText(email, points, userType);
        }

        username.addTextChangedListener(new SpaceTextWatcher(username));
        dob.addTextChangedListener(new SpaceTextWatcher(dob));
        phone.addTextChangedListener(new SpaceTextWatcher(phone));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);

        if (isAdmin) {
            bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.purple));
        }

        RelativeLayout rootLayout = findViewById(R.id.main);

        if (isAdmin) {
            rootLayout.setBackgroundResource(R.drawable.admin_profile_background);
        } else {
            rootLayout.setBackgroundResource(R.drawable.profilebackground);
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

        profilePic = findViewById(R.id.profilepic);
        updateProfileBtn = findViewById(R.id.updateprofilepic);

        saveButton = findViewById(R.id.savebutton);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userRef = database.getReference("User").child(user.getUserId());

        loadProfilePictureFromDatabase();

        ActivityResultLauncher<Intent> imagePickLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            saveProfilePictureToDatabase(selectedImageUri);
                        } else {
                            Toast.makeText(this, "Invalid image selected.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        updateProfileBtn.setOnClickListener(v -> ImagePicker.with(this)
                .cropSquare()
                .compress(150)
                .maxResultSize(150, 150)
                .createIntent(intent -> {
                    imagePickLauncher.launch(intent);
                    return null;
                }));

        saveButton.setOnClickListener(v -> {
            String newUsername = username.getText().toString().trim();
            String newDob = dob.getText().toString().trim();
            String newPhone = phone.getText().toString().trim();

            if (validateInputs(newUsername, newDob, newPhone)) {
                checkUsernameAvailability(newUsername, newDob, newPhone, user.getUserId());
            }
        });

    }

    private String calculateUserType(int points) {
        if (points < 25) {
            return "Bronze";
        } else if (points >= 26 && points <= 75) {
            return "Silver";
        } else if (points > 76 && points <= 200) {
            return "Gold";
        } else if (points > 200) {
            return "Diamond";
        }
        return "Bronze";
    }

    private void disableEditText(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false);
            editText.setEnabled(false);
        }
    }

    private void loadProfilePictureFromDatabase() {
        userRef.child("profile_pic_path").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().getValue() != null) {
                String imageUrl = task.getResult().getValue(String.class);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(this).load(imageUrl).into(profilePic);
                } else {
                    profilePic.setImageResource(R.drawable.profilepic);
                }
            } else {
                profilePic.setImageResource(R.drawable.profilepic);
            }
        });
    }

    private void saveProfilePictureToDatabase(Uri imageUri) {
        if (imageUri == null) {
            Log.e("SaveProfile", "Image URI is null. Cannot save to database.");
            Toast.makeText(this, "No image selected to save.", Toast.LENGTH_SHORT).show();
            return;
        }

        String imagePath = imageUri.toString();
        Log.d("SaveProfile", "Saving image path: " + imagePath);

        userRef.child("profile_pic_path").setValue(imagePath).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Glide.with(this).load(imageUri).into(profilePic);
                Toast.makeText(this, "Profile picture updated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("SaveProfile", "Failed to save image path to database", task.getException());
                Toast.makeText(this, "Failed to update profile picture.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs(String username, String dob, String phone) {
        if (username.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!dob.equals("Prefer Not to Say") && !dob.matches("\\d{4}-\\d{2}-\\d{2}")) {
            Toast.makeText(this, "Invalid date format. Use YYYY-MM-DD", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (phone.isEmpty() || phone.length() < 10 || (!phone.equals("Prefer Not to Say") && !phone.matches("\\d+"))) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void checkUsernameAvailability(String newUsername, String newDob, String newPhone, String currentUserId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("User");

        usersRef.orderByChild("username").equalTo(newUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean usernameTaken = false;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String userId = snapshot.getKey();
                        if (!userId.equals(currentUserId)) {
                            usernameTaken = true;
                            break;
                        }
                    }

                    if (usernameTaken) {
                        Toast.makeText(EditProfile.this, "Username is already taken!", Toast.LENGTH_SHORT).show();
                    } else {
                        updateProfile(newUsername, newDob, newPhone);
                    }
                } else {
                    updateProfile(newUsername, newDob, newPhone);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditProfile.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile(String newUsername, String newDob, String newPhone) {
        userRef.child("username").setValue(newUsername);
        userRef.child("user_birthday").setValue(newDob);
        userRef.child("user_phonenumber").setValue(newPhone);

        user.setUsername(newUsername);
        user.setDob(newDob);
        user.setPhone(newPhone);
        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        Intent intent;
        if (isAdmin) {
            intent = new Intent(EditProfile.this, AdminProfile.class);
        } else {
            intent = new Intent(EditProfile.this, Profile.class);
        }
        intent.putExtra("user", user);
        intent.putExtra("isAdmin", isAdmin);
        startActivity(intent);
        finish();
    }

    private class SpaceTextWatcher implements TextWatcher {
        private EditText editText;

        public SpaceTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            if (start == 0 && count == 0 && after == 0) {
                if (editText.getText().toString().length() < 12) {
                    editText.setText("            ");
                }
            }
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() < 12) {
                editable.replace(0, editable.length(), "            " + editable.toString().trim());
            }
        }
    }
}