package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ADDCustomer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_addcustomer);


        String[] options = { "Male", "Female" };
        final Spinner genderSpinner =findViewById(R.id.spinner);
        ArrayAdapter<String> objGenderArr = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, options);
        genderSpinner.setAdapter(objGenderArr);

        final EditText idEditText = (EditText)findViewById(R.id.editTextText);
        final EditText nameEditText = (EditText)findViewById(R.id.editTextText2);
        final EditText phoneEditText = (EditText)findViewById(R.id.editTextText3);
        final EditText EmailEditText = (EditText)findViewById(R.id.editTextText5);


        Button addCustomerButton = (Button) findViewById(R.id.button);
        addCustomerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Customer newCustomer =new Customer();
                if(idEditText.getText().toString().isEmpty()) newCustomer.setmCustomerId(0);

                else newCustomer.setmCustomerId(Long.parseLong(idEditText.getText().toString()));

                if(nameEditText.getText().toString().isEmpty()) newCustomer.setmName("No Name");

                else newCustomer.setmName(nameEditText.getText().toString());

                if(phoneEditText.getText().toString().isEmpty()) newCustomer.setmPhone("No Phone");

                else newCustomer.setmPhone(phoneEditText.getText().toString());

                if(EmailEditText.getText().toString().isEmpty()) newCustomer.setmEmail("No Email");

                else newCustomer.setmEmail(EmailEditText.getText().toString());

                newCustomer.setmGender(genderSpinner.getSelectedItem().toString());
 
                DataBaseHelper dataBaseHelper =new DataBaseHelper(ADDCustomer.this,"EXP4",null,1);
                dataBaseHelper.insertCustomer(newCustomer);

                Intent intent=new Intent(ADDCustomer.this,MainActivity.class);
                ADDCustomer.this.startActivity(intent);
                finish();

            }
        });
    }
}