package com.example.fitapp.ui.generalmodule;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPassword extends AppCompatActivity {

    private EditText emailField;
    private Button sendEmailButton, backButton;
    private TextView tvNotification;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailField = findViewById(R.id.emailField);
        sendEmailButton = findViewById(R.id.sendEmailButton);
        backButton = findViewById(R.id.backButton);
        tvNotification = findViewById(R.id.TVNotification);

        auth = FirebaseAuth.getInstance();

        tvNotification.setVisibility(View.INVISIBLE);

        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(ForgotPassword.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                } else {
                    sendPasswordResetEmail(email);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void sendPasswordResetEmail(String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        tvNotification.setVisibility(View.VISIBLE);
                        Toast.makeText(ForgotPassword.this, "Reset email sent. Check your inbox.", Toast.LENGTH_LONG).show();
                    } else {
                        String error = "Failed to send reset email.";
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            error = "No account linked with this email.";
                        }
                        Toast.makeText(ForgotPassword.this, error, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
