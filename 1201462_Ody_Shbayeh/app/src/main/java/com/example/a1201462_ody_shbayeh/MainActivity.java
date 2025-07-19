//odyshbayeh----1201462
package com.example.a1201462_ody_shbayeh;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button Start = (Button) findViewById(R.id.StartButton);
        TextView UserName = (TextView) findViewById(R.id.UserName);
        TextView Email = (TextView) findViewById(R.id.Email);
        TextView DateOfBirth = (TextView) findViewById(R.id.Date);
        TextView messagetext = (TextView)findViewById(R.id.messagetext);
        DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this, "User", null, 1);

        DateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current date
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Create and show the DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                                // Update the DateOfBirth TextView with the selected date
                                String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                                DateOfBirth.setText(selectedDate);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = UserName.getText().toString().trim();
                //here we check if the username is valid and it doesn't exists in the database also
                //the email and date formates are valid if so we register the new user
                if (    isValidName(name) &&
                        isValidEmail(Email.getText().toString().trim()) &&
                        isValidBirthdate(DateOfBirth.getText().toString().trim())) {
                    //here the user entered the the information with a correct validation
                    User newUser = new User(name, Email.getText().toString().trim(), DateOfBirth.getText().toString().trim());
                    //here we check if the username exists in the database
                    if(dataBaseHelper.isUsernameValid(name)) {
                        if (dataBaseHelper.insertUser(newUser)) {
                            Toast.makeText(MainActivity.this, "Welcome To The Game", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, MathGame.class);
                            //here we are passing the username with the intent so that we can register
                            // the score of the player when he finishes the quiz in the next activity scene
                            intent.putExtra("USERNAME", name);
                            startActivity(intent);
                            finish();
                        }
                    }else {
                        Toast.makeText(MainActivity.this, "Registiration Failed User already exists", Toast.LENGTH_SHORT).show();
                        messagetext.setText("Registiration Failed");
                        UserName.setText("");
                        UserName.setHint("Please enter your name");
                        Email.setText("");
                        Email.setHint("Email as : ody@gmail.com");
                        DateOfBirth.setText("");
                        DateOfBirth.setHint("DateOfbirth as :'yyyy-MM-dd'");
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Wrong Format in the inputs", Toast.LENGTH_SHORT).show();
                    messagetext.setText("Wrong Format in the inputs");
                    UserName.setText("");
                    UserName.setHint("Please enter your name");
                    Email.setText("");
                    Email.setHint("Email as : ody@gmail.com");
                    DateOfBirth.setText("");
                    DateOfBirth.setHint("DateOfbirth as :'yyyy-MM-dd'");
                }
            }
        });


    }
    //Method to validate the Email if it contains the form of @gmail.com
    public boolean isValidEmail(String email) {
        if (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true;
        } else {
            Toast.makeText(MainActivity.this, "Email Format is Wrong", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    //Method to validate the BirthDate if it goes as yyyy-MM--dd
    public boolean isValidBirthdate(String birthdate) {
        if (birthdate == null || birthdate.trim().isEmpty()) {
            Toast.makeText(MainActivity.this, "Birthday Field is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(birthdate);
            return true;
        } catch (ParseException e) {
            Toast.makeText(MainActivity.this, "Birthday Format is Wrong", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // Method to validate the name if it's null or has a number at the start
    public boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            Toast.makeText(MainActivity.this, "UserName is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        char firstChar = name.charAt(0);
        if (Character.isDigit(firstChar)) {
            Toast.makeText(MainActivity.this, "UserName Must Start With a Letter", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}