package com.example.tweetanimationapplication;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

        Button buttonRotate=(Button)findViewById(R.id.Rotate);
        Button buttonScale=(Button) findViewById(R.id.Scale);
        Button buttonTranslate =(Button) findViewById(R.id.Translate);
        Button buttonCode1 = (Button) findViewById(R.id.Peek);
        Button buttonCode2 = (Button) findViewById(R.id.RotateMaster);
        Button BirdFly =(Button) findViewById(R.id.BirdButton);
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        final ImageView Bird = (ImageView) findViewById(R.id.imageView2);
        buttonRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imageView.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.rotate));
            }
        });
        buttonScale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imageView.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.scale));
            }
        });
        buttonTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imageView.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.translate));
            }
        });
        buttonCode1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.peek));
            }
        });
        buttonCode2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.rotatemaster));
            }
        });
        BirdFly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bird.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.birdfly));
            }
        });
    }
}