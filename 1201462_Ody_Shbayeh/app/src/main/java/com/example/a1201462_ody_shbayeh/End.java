//odyshbayeh---1201462
package com.example.a1201462_ody_shbayeh;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

import java.util.List;

public class End extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_end);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView yourScore =(TextView) findViewById(R.id.yourScore);
        String username = getIntent().getStringExtra("USERNAME");
        String score = getIntent().getStringExtra("SCORE");
        //getting the user score passed from the previous activity
        yourScore.setText(username+" Scored : "+score);


        Button Restart = (Button) findViewById(R.id.Restart);
        Button SearchUser = (Button) findViewById(R.id.SearchUser);
        TextView TopPlayers = (TextView) findViewById(R.id.TopPlayers);
        TextView AllPlayers = (TextView) findViewById(R.id.AllPLayers);
        TextView AVGScore = (TextView) findViewById(R.id.AVGScore);
        TextView HighiestScore = (TextView) findViewById(R.id.HighiestScore);
        TextView Searchname =(TextView) findViewById(R.id.Searchname);
        TextView SearchPlayerScore =(TextView) findViewById(R.id.SearchPlayerScore);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(End.this, "User", null, 1);
        //get the total players numbers in the database from the databasehelper query
        AllPlayers.setText("Total Players : "+dataBaseHelper.getTotalPlayers());
        //get the avg for the score for all the players from the databasehelper query
        AVGScore.setText("AVG Score : "+String.valueOf(dataBaseHelper.getAverageScore()));
        //get the highest score from the databasehelper query
        HighiestScore.setText(dataBaseHelper.getHighestScoreWithName());

        // Get the top 5 players and display them in the TopPlayers TextView
        Cursor cursor =dataBaseHelper.getAllUsersScores();
        StringBuilder topPlayersString = new StringBuilder("Top 5 Players:\n");
        if (cursor.moveToFirst()){
            do {
                String playerName = cursor.getString(0);
                double ScoreNew = cursor.getDouble(1);
                topPlayersString.append(playerName).append(": ").append(ScoreNew).append("\n");
            }while(cursor.moveToNext());
        }
        TopPlayers.setText(topPlayersString.toString());

        SearchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            SearchPlayerScore.setText(Searchname.getText().toString()+" Scored :"+dataBaseHelper.getPlayerScores(Searchname.getText().toString()));
            }
        });

        Restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(End.this, "Good Luck!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(End.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}