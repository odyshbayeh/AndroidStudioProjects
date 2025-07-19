//odyshbayeh----1201462
package com.example.a120142_ody_shbayeh;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button button,Restart;
    int manpartsindex,manplacesindex,letterindex,correctwordindex = 0;
    String[] man = {"O", "|", "/", "\\", "/", "\\"};
    TextView[] textViews,Letters;
    TextView body1, Counts, CorrectGuess,message,playersname ;
    String []correctword= {"b","i","r","z","e","i","t"};


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

        textViews = new TextView[]{
                findViewById(R.id.Head),
                findViewById(R.id.Body),
                findViewById(R.id.LeftArm),
                findViewById(R.id.RightArm),
                findViewById(R.id.LeftLeg),
                findViewById(R.id.RightLeg)
        };

        Letters = new TextView[]{
                findViewById(R.id.Letter1),
                findViewById(R.id.letter2),
                findViewById(R.id.letter3),
                findViewById(R.id.letter4),
                findViewById(R.id.letter5),
                findViewById(R.id.letter6),
                findViewById(R.id.letter7),
        };

        Counts = findViewById(R.id.Counts);
        body1 = findViewById(R.id.Body1);
        button = findViewById(R.id.button);
        Restart = findViewById(R.id.Restart);
        CorrectGuess = findViewById(R.id.CorrectGuess);
        message = findViewById(R.id.message);
        playersname = findViewById(R.id.Playername);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = Letters[letterindex].getText().toString().toLowerCase();
                if(s.equals(correctword[correctwordindex])){
                    message.setTextColor(Color.GREEN);
                    message.setText("you guessed a correct character");
                    Letters[letterindex].setEnabled(false);
                    correctwordindex++;
                    letterindex++;
                    int S = Integer.parseInt(CorrectGuess.getText().toString()) + 1;
                    CorrectGuess.setText(Integer.toString(S));
                    if (S > 6 ){
                        S=6;
                        CorrectGuess.setText(Integer.toString(S));
                        message.setTextColor(Color.GREEN);
                        message.setText("Congratulations,"+playersname.getText().toString()+" You won the game");
                        button.setEnabled(false);
                    }
                    Restart.setEnabled(true);
                } else if (s.isEmpty()) {
                    message.setTextColor(Color.YELLOW);
                    message.setText("you should enter a character !");
                }else {
                    message.setTextColor(Color.RED);
                    message.setText("you guessed wrong");
                    Letters[letterindex].setText("");
                    int l = Integer.parseInt(Counts.getText().toString()) - 1;
                    Counts.setText(Integer.toString(l));
                     if (textViews[manplacesindex].getId() == R.id.Body) {
                        body1.setText(man[manpartsindex]);
                        textViews[manplacesindex].setText(man[manpartsindex]);
                    } else if (l == 0) {
                        //l = 6;
                        Counts.setText(Integer.toString(l));
                         textViews[manplacesindex].setText(man[manpartsindex]);
                         message.setTextColor(Color.RED);
                        message.setText("Sorry," + playersname.getText().toString() + " You Lost the game");
                        for(TextView L:Letters){
                            L.setEnabled(false);
                        }
                        button.setEnabled(false);
                        playersname.setEnabled(false);
                    }else {
                        textViews[manplacesindex].setText(man[manpartsindex]);
                    }
                    manpartsindex++;
                    manplacesindex++;
                    Restart.setEnabled(true);
                }
            }
        });

        Restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(TextView pop :textViews){
                    pop.setText("");
                }
                for(TextView por :Letters){
                    por.setText("");
                    por.setEnabled(true);
                }
                body1.setText("");
                manpartsindex=0;
                manplacesindex=0;
                letterindex=0;
                correctwordindex=0;
                Counts.setText("6");
                CorrectGuess.setText("0");
                message.setText("");
                button.setEnabled(true);
                Restart.setEnabled(false);
                playersname.setEnabled(true);
            }
        });
    }
}


