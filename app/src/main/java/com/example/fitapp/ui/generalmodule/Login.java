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
                loginUser(username, password); // Call the login function
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
                        String dbPassword = snapshot.child("user_password").getValue(String.class);

                        if (dbPassword != null && dbPassword.equals(password)) {
                            // Successfully authenticated
                            User user = snapshot.getValue(User.class);
                            user.setUserId(userId);
                            user.setUsername(snapshot.child("username").getValue(String.class));
                            user.setPasswordHash(snapshot.child("user_password").getValue(String.class));
                            user.setEmail(snapshot.child("user_email").getValue(String.class));
                            user.setDob(snapshot.child("user_birthday").getValue(String.class));
                            user.setPhone(snapshot.child("user_phonenumber").getValue(String.class));
                            user.setPoints(snapshot.child("user_points").getValue(Integer.class));
                            user.setUserType(snapshot.child("user_types").getValue(String.class));
                            user.setProfilePicPath(snapshot.child("profile_pic_path").getValue(String.class));

<<<<<<< HEAD
                            // Check if the user is an admin
                            boolean isAdmin = "Jeflyn_1733202461846".equals(userId);

=======
>>>>>>> 172b022a49342446412c20a1f92646cf78a14948
                            if (user != null) {
                                // Pass User object to MainActivity
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                intent.putExtra("user", user); // Parcelable User object
<<<<<<< HEAD
                                intent.putExtra("isAdmin", isAdmin); // Pass isAdmin flag
=======
>>>>>>> 172b022a49342446412c20a1f92646cf78a14948
                                startActivity(intent);
                                finish(); // Close login activity
                                loginSuccess = true;
                                break; // Exit loop once user is found
                            } else {
                                Toast.makeText(Login.this, "Error retrieving user data!", Toast.LENGTH_SHORT).show();
                            }
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