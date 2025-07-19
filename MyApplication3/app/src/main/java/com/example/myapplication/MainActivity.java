package com.example.myapplication;

import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView favoriteColorText;
    private Spinner colorSpinner;
    private ImageView circleRed, circleBlue, circleGreen;
    private Button saveColorButton, clearColorButton;

    private SharedPrefManager sharedPrefManager;
    private static final String COLOR_KEY = "favorite_color";
    private static final String IS_ROTATING_KEY = "is_rotating";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        favoriteColorText = findViewById(R.id.favoriteColorText);
        colorSpinner = findViewById(R.id.colorSpinner);
        circleRed = findViewById(R.id.circleRed);
        circleBlue = findViewById(R.id.circleBlue);
        circleGreen = findViewById(R.id.circleGreen);
        saveColorButton = findViewById(R.id.saveColorButton);
        clearColorButton = findViewById(R.id.clearColorButton);

        sharedPrefManager = SharedPrefManager.getInstance(this);
        // the idea is i want to add the colors to the spinner here circule blue,green,red
        //from there we can get the color and make the animations and save it to the shared preferensses
        //but i didn't have the time to do so
        //but this's the idea :)
        //colorSpinner.addChildrenForAccessibility();
        String savedColor = sharedPrefManager.readString(COLOR_KEY, null);
        boolean isRotating = sharedPrefManager.readBoolean(IS_ROTATING_KEY, false);

        if (savedColor != null) {
            favoriteColorText.setText("Favorite Color: " + savedColor);
            rotateCircle(savedColor, isRotating);
        } else {
            favoriteColorText.setText("No favorite color selected");
        }

        saveColorButton.setOnClickListener(v -> saveFavoriteColor());
        clearColorButton.setOnClickListener(v -> clearFavoriteColor());
    }

    private void saveFavoriteColor() {
        String selectedColor = colorSpinner.getSelectedItem().toString();
        sharedPrefManager.writeString(COLOR_KEY, selectedColor);
        sharedPrefManager.writeBoolean(IS_ROTATING_KEY, true);

        favoriteColorText.setText("Favorite Color: " + selectedColor);
        rotateCircle(selectedColor, true);
    }

    private void clearFavoriteColor() {
        sharedPrefManager.writeString(COLOR_KEY, null);
        sharedPrefManager.writeBoolean(IS_ROTATING_KEY, false);

        favoriteColorText.setText("No favorite color selected");
    }

    private void rotateCircle(String color, boolean shouldRotate) {

        if (shouldRotate) {
            switch (color) {
                case "Red":
                    circleRed.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotation));
                    break;
                case "Blue":
                    circleBlue.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotation));
                    break;
                case "Green":
                    circleGreen.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotation));
                    break;
            }
        }
    }

}
