package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SharedPreferencesManager prefsManager = new SharedPreferencesManager(this);

        EditText email = findViewById(R.id.etEmail);
        EditText password = findViewById(R.id.etPassword);
        CheckBox rememberMe = findViewById(R.id.cbRememberMe);
        Button signinButton = findViewById(R.id.btnSignIn);
        Button signupButton = findViewById(R.id.btnSignUp);

        if (prefsManager.isRememberMeEnabled()) {
            email.setText(prefsManager.getSavedEmail());
            password.setText(prefsManager.getSavedPassword());
            rememberMe.setChecked(true);
        }

        signinButton.setOnClickListener(v -> {
            String inputEmail = email.getText().toString().trim();
            String inputPassword = password.getText().toString().trim();

            if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
                email.setError("Email and Password are required!");
                return;
            }

            if (dbHelper.verifyUser(inputEmail, inputPassword)) {
                if (rememberMe.isChecked()) {
                    prefsManager.saveUserCredentials(inputEmail, inputPassword);
                } else {
                    prefsManager.clearUserCredentials();
                }

                Toast.makeText(getApplicationContext(), "Welcome Back " + dbHelper.getUserNameByEmail(inputEmail), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, Home.class);
                intent.putExtra("USER_EMAIL", inputEmail); // Pass the email to Home
                startActivity(intent);
                finish();
            } else {
                email.setError("Invalid Email or Password");
            }
        });

        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Signup.class);
            startActivity(intent);
        });
    }
}
