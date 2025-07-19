package com.example.finalproject.ui.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.finalproject.DatabaseHelper;
import com.example.finalproject.R;
import com.example.finalproject.SharedPreferencesManager;

public class ProfileFragment extends Fragment {
    private TextView Welcome;
    private EditText editPreviousEmail, editNewEmail, editNewPassword, editConfirmPassword;
    private CheckBox checkboxDarkMode;
    private Button buttonUpdateProfile;
    private String userEmail;

    private SharedPreferencesManager sharedPreferencesManager;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        Welcome = view.findViewById(R.id.welcome_text);
        editPreviousEmail = view.findViewById(R.id.edit_previous_email);
        editNewEmail = view.findViewById(R.id.edit_new_email);
        editNewPassword = view.findViewById(R.id.edit_new_password);
        editConfirmPassword = view.findViewById(R.id.edit_confirm_password);
        checkboxDarkMode = view.findViewById(R.id.checkbox_dark_mode);
        buttonUpdateProfile = view.findViewById(R.id.button_update_profile);

        // Initialize helpers
        sharedPreferencesManager = new SharedPreferencesManager(requireContext());
        databaseHelper = new DatabaseHelper(requireContext());

        // Set initial state for Dark Mode checkbox
        checkboxDarkMode.setChecked(sharedPreferencesManager.isDarkModeEnabled());

        // Re-fetch arguments to ensure userEmail is available
        if (getArguments() != null) {
            userEmail = getArguments().getString("USER_EMAIL");
            Log.d("ProfileFragment", "fetched userEmail: " + userEmail);
        }

        String username = databaseHelper.getUserNameByEmail(userEmail);
        Welcome.setText("Welcome " + username);

        // Listen to Dark Mode checkbox changes
        checkboxDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferencesManager.saveThemeMode(isChecked);
            applyTheme(isChecked);
            Toast.makeText(requireContext(), "Dark Mode preference saved.", Toast.LENGTH_SHORT).show();
        });

        // Update button click listener
        buttonUpdateProfile.setOnClickListener(v -> updateProfile());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Apply the saved theme preference
        boolean isDarkMode = sharedPreferencesManager.isDarkModeEnabled();
        applyTheme(isDarkMode);

        // Sync the checkbox state
        checkboxDarkMode.setChecked(isDarkMode);
    }

    private void applyTheme(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void updateProfile() {
        String previousEmail = editPreviousEmail.getText().toString().trim();
        String newEmail = editNewEmail.getText().toString().trim();
        String newPassword = editNewPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();

        // Validate: All fields required
        if (previousEmail.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            if (previousEmail.isEmpty()) editPreviousEmail.setError("Previous email is required");
            if (newEmail.isEmpty()) editNewEmail.setError("New email is required");
            if (newPassword.isEmpty()) editNewPassword.setError("New password is required");
            if (confirmPassword.isEmpty()) editConfirmPassword.setError("Confirm password is required");
            return;
        }

        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(previousEmail).matches()) {
            editPreviousEmail.setError("Invalid previous email format");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            editNewEmail.setError("Invalid new email format");
            return;
        }

        // Validate password length (6 to 12 chars)
        if (newPassword.length() < 6 || newPassword.length() > 12) {
            editNewPassword.setError("Password must be between 6 and 12 characters long");
            return;
        }

        // Validate password content: at least one uppercase, one lowercase, and one digit
        if (!newPassword.matches(".*[A-Z].*")) {
            editNewPassword.setError("Password must contain at least one uppercase letter");
            return;
        }
        if (!newPassword.matches(".*[a-z].*")) {
            editNewPassword.setError("Password must contain at least one lowercase letter");
            return;
        }
        if (!newPassword.matches(".*\\d.*")) {
            editNewPassword.setError("Password must contain at least one digit");
            return;
        }

        // Validate confirm password matches password
        if (!newPassword.equals(confirmPassword)) {
            editConfirmPassword.setError("Passwords do not match");
            return;
        }

        // Update user credentials in the database
        int result = databaseHelper.updateUser(previousEmail, newEmail, newPassword);
        if (result > 0) {
            sharedPreferencesManager.saveUserCredentials(newEmail, newPassword); // Save to SharedPreferences
            Toast.makeText(requireContext(), "Profile updated successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Failed to update profile. Check your previous email.", Toast.LENGTH_SHORT).show();
        }
    }
}
