package com.example.fitapp.ui.generalmodule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitapp.ui.home.MainActivity;
import com.example.fitapp.R;
import com.example.fitapp.javahelperfile.profile.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private Button signUp;
    private Button forgotPassword;
    private EditText usernameField, passwordField;
    private Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signUp = findViewById(R.id.signUp_loginpage);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });
        forgotPassword= findViewById(R.id.forgotPasswordButton);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        signInButton = findViewById(R.id.signInButton);

        signInButton.setOnClickListener(view -> {
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(Login.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(username, password);
            }
        });
    }

    private void loginUser(String username, String password) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("User");

        usersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean loginSuccess = false;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String userId = snapshot.getKey();
                        String email = snapshot.child("user_email").getValue(String.class);

                            boolean isAdmin = "Admin_1735718979761".equals(userId);

                            if (email != null) {
                                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                                User user = snapshot.getValue(User.class);
                                                user.setUserId(userId);
                                                user.setUsername(snapshot.child("username").getValue(String.class));
                                                user.setEmail(snapshot.child("user_email").getValue(String.class));
                                                user.setDob(snapshot.child("user_birthday").getValue(String.class));
                                                user.setPhone(snapshot.child("user_phonenumber").getValue(String.class));
                                                user.setPoints(snapshot.child("user_points").getValue(Integer.class));
                                                user.setUserType(snapshot.child("user_types").getValue(String.class));
                                                user.setProfilePicPath(snapshot.child("profile_pic_path").getValue(String.class));

                                                if (firebaseUser != null && firebaseUser.isEmailVerified()) {
                                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                                    intent.putExtra("isAdmin", isAdmin);
                                                    intent.putExtra("user", user);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(Login.this, "Email not verified!", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(Login.this, "Invalid password!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                return;
                            }

                    }

                    if (!loginSuccess) {
                        Toast.makeText(Login.this, "Invalid Password!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login.this, "Username not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Login.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}