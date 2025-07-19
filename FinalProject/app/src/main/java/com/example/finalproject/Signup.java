package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        EditText email = findViewById(R.id.etEmail);
        EditText firstName = findViewById(R.id.etFirstName);
        EditText lastName = findViewById(R.id.etLastName);
        EditText password = findViewById(R.id.etPassword);
        EditText confirmPassword = findViewById(R.id.etConfirmPassword);
        Button signupButton = findViewById(R.id.btnSignUp);

        signupButton.setOnClickListener(v -> {
            String inputEmail = email.getText().toString().trim();
            String inputFirstName = firstName.getText().toString().trim();
            String inputLastName = lastName.getText().toString().trim();
            String inputPassword = password.getText().toString().trim();
            String inputConfirmPassword = confirmPassword.getText().toString().trim();

            if (inputEmail.isEmpty() || inputFirstName.isEmpty() || inputLastName.isEmpty() ||
                    inputPassword.isEmpty() || inputConfirmPassword.isEmpty()) {
                if (inputEmail.isEmpty()) {
                    email.setError("Email is required");
                }
                if (inputFirstName.isEmpty()) {
                    firstName.setError("First name is required");
                }
                if (inputLastName.isEmpty()) {
                    lastName.setError("Last name is required");
                }
                if (inputPassword.isEmpty()) {
                    password.setError("Password is required");
                }
                if (inputConfirmPassword.isEmpty()) {
                    confirmPassword.setError("Confirm password is required");
                }
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
                email.setError("Invalid email format");
                return;
            }

            if (inputFirstName.length() < 5 || inputFirstName.length() > 20) {
                firstName.setError("First name must be between 5 and 20 characters");
                return;
            }

            if (inputLastName.length() < 5 || inputLastName.length() > 20) {
                lastName.setError("Last name must be between 5 and 20 characters");
                return;
            }

            if (inputPassword.length() < 6 || inputPassword.length() > 12) {
                password.setError("Password must be between 6 and 12 characters long");
                return;
            }

            if (!inputPassword.matches(".*[A-Z].*")) {
                password.setError("Password must contain at least one uppercase letter");
                return;
            }
            if (!inputPassword.matches(".*[a-z].*")) {
                password.setError("Password must contain at least one lowercase letter");
                return;
            }
            if (!inputPassword.matches(".*\\d.*")) {
                password.setError("Password must contain at least one digit");
                return;
            }

            if (!inputPassword.equals(inputConfirmPassword)) {
                confirmPassword.setError("Passwords do not match");
                return;
            }

            if (dbHelper.verifyUser(inputEmail, inputPassword)) {
                email.setError("Email is already registered");
                return;
            }

            User user = new User(inputEmail, inputFirstName, inputLastName, inputPassword);
            dbHelper.insertUser(user);

            Toast.makeText(getApplicationContext(),
                    "Welcome " + dbHelper.getUserNameByEmail(inputEmail) + "! Sign-up completed.",
                    Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Signup.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
