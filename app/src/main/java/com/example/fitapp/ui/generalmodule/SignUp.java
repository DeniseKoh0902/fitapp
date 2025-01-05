package com.example.fitapp.ui.generalmodule;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SignUp extends AppCompatActivity {

    private Button signIn;
    private EditText usernameField, emailField, passwordField, confirmPasswordField;
    private Button signUpButton;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signIn = findViewById(R.id.signIn_signuppage);
        usernameField = findViewById(R.id.usernameField);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        confirmPasswordField = findViewById(R.id.confirmPasswordField);
        signUpButton = findViewById(R.id.signUpButton);

        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("User");

        signIn.setOnClickListener(v -> startActivity(new Intent(SignUp.this, Login.class)));

        signUpButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String username = usernameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                        if (firebaseUser != null) {
                            firebaseUser.sendEmailVerification()
                                    .addOnCompleteListener(verificationTask -> {
                                        if (verificationTask.isSuccessful()) {
                                            saveUserToDatabase(firebaseUser.getUid(), username, email, password);
                                            Toast.makeText(SignUp.this, "Verification email sent. Please check your inbox.", Toast.LENGTH_SHORT).show();
                                            mAuth.signOut();
                                            startActivity(new Intent(SignUp.this, Login.class));
                                            finish();
                                        } else {
                                            Toast.makeText(SignUp.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(SignUp.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToDatabase(String userId, String username, String email, String password) {
        String userID = username + "_" + System.currentTimeMillis();

        HashMap<String, Object> userHashMap = new HashMap<>();
        userHashMap.put("username", username);
        userHashMap.put("user_email", email);
        userHashMap.put("user_password", password);
        userHashMap.put("user_birthday", "Prefer Not to Say");
        userHashMap.put("user_phonenumber", "Prefer Not to Say");
        userHashMap.put("user_points", 0);
        userHashMap.put("profile_pic_path", null);
        userHashMap.put("user_sign_up_date", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(new Date()));
        userHashMap.put("user_types", "Bronze");

        databaseRef.child(userID).setValue(userHashMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUp.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignUp.this, "Failed to save user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
