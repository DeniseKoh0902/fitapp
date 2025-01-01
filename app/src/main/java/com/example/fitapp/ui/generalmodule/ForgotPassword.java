package com.example.fitapp.ui.generalmodule;

import android.content.Intent;
import android.os.Bundle;
<<<<<<< HEAD
import android.view.View;
import android.widget.Button;
=======
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
>>>>>>> 172b022a49342446412c20a1f92646cf78a14948

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitapp.R;
<<<<<<< HEAD
=======
import com.google.firebase.auth.FirebaseAuth;
>>>>>>> 172b022a49342446412c20a1f92646cf78a14948

public class ForgotPassword extends AppCompatActivity {
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< HEAD
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
=======
        setContentView(R.layout.activity_forgot_password);

>>>>>>> 172b022a49342446412c20a1f92646cf78a14948
        back = findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPassword.this, Login.class);
                startActivity(intent);
            }
        });
<<<<<<< HEAD
=======

        Button sendResetEmailButton = findViewById(R.id.sendCodeButton);
        EditText emailField = findViewById(R.id.emailField);

        sendResetEmailButton.setOnClickListener(view -> {
            String email = emailField.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(ForgotPassword.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPassword.this, "Password reset email sent. Check your inbox.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ForgotPassword.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
>>>>>>> 172b022a49342446412c20a1f92646cf78a14948
    }
}