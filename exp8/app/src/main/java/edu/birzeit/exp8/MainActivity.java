package edu.birzeit.exp8;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements BlankFragment2.communicator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void respond(String data) {
        BlankFragment2 secondFragment =
                ( BlankFragment2)getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView2);
        secondFragment.changeData(data);
    }

}