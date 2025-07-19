//odyshabyeh---1201462
package com.example.a1201462_ody_shbayeh;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MathGame extends AppCompatActivity {

    private TextView timeText;
    private TextView questionsTrackText;
    private TextView scoreText;
    private TextView questionsText;
    int score;
    private List<String[]> selectedQuestions = new ArrayList<>();
    private Button[] selections;
    private CountDownTimer countDown;
    String username;
    int NumberOfCorrectAnswers=0;
    int NumberOfFalseAnswers=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_math_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //here we got the username that have sucessfully registered so that
        //we can assign the score for him in the database
        username = getIntent().getStringExtra("USERNAME");

        //Initialize views
        timeText = findViewById(R.id.TimeText);
        questionsTrackText = findViewById(R.id.QuestionsAnswered);
        scoreText = findViewById(R.id.Score);
        questionsText = findViewById(R.id.Questionstext);
        selections = new Button[]{
                findViewById(R.id.SelectionOne),
                findViewById(R.id.SelectionTwo),
                findViewById(R.id.SelectionThree),
                findViewById(R.id.SelectionFour)
        };

        scoreText.setText(String.valueOf(score));

        //Reading the questions.csv file and save the entire content to a Arraylist
        // so that we can load 5 random questions from the Array
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.questions);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            List<String[]> allQuestions = new ArrayList<>();
            String line;
            //here we save the question text and the answer
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    allQuestions.add(new String[]{parts[0].trim(), parts[1].trim()});
                }
            }

            reader.close();
            //changing the order of the questions randomly so that we can get random questions
            Collections.shuffle(allQuestions, new Random());
            //getting a 5 questions from the shuffeled array that contains all the questions
            selectedQuestions = allQuestions.subList(0, Math.min(5, allQuestions.size()));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //arrays to keep track of the questions and thier answers
        final int[] questionIndex = {0};
        final String[] correctAnswer = {""};

        // Display the first question
        if (!selectedQuestions.isEmpty()) {
            String[] questionAndAnswer = selectedQuestions.get(questionIndex[0]);
            questionsText.setText(questionAndAnswer[0]);
            correctAnswer[0] = questionAndAnswer[1];

            //Array list to contain the random false answers to create options for the quiz
            List<String> options = new ArrayList<>();
            options.add(correctAnswer[0]);
            Random random = new Random();

            //create three false options
            while (options.size() < 4) {
                int incorrectOption = random.nextInt(100);
                String optionString = String.valueOf(incorrectOption);
                if (!options.contains(optionString) && !optionString.equals(correctAnswer[0])) {
                    options.add(optionString);
                }
            }

            // Shuffle options and set them to buttons
            Collections.shuffle(options);
            questionsTrackText.setText(String.valueOf(questionIndex[0]));
            for (int i = 0; i < selections.length; i++) {
                //set the buttons options
                selections[i].setText(options.get(i));
                int ThisButton = i; // for lambda use to inner access for the button assigning and listening 
                //waiting if any button got clicked
                selections[i].setOnClickListener(v -> {
                    //stop the timer if a button got clicked
                    countDown.cancel();
                    if (selections[ThisButton].getText().toString().equals(correctAnswer[0])) {
                        Toast.makeText(MathGame.this, "Correct answer", Toast.LENGTH_SHORT).show();
                        NumberOfCorrectAnswers++;
                        score++;
                    } else {
                        Toast.makeText(MathGame.this, "False answer", Toast.LENGTH_SHORT).show();
                        NumberOfFalseAnswers++;
                        score--;
                        if(score<=0){
                            score=0;
                        }
                    }
                    scoreText.setText(String.valueOf(score));

                    //Move to the next question
                    questionIndex[0]++;
                    questionsTrackText.setText(String.valueOf(questionIndex[0]));
                    if (questionIndex[0] < selectedQuestions.size()) {
                        String[] nextQuestion = selectedQuestions.get(questionIndex[0]);
                        questionsText.setText(nextQuestion[0]);
                        correctAnswer[0] = nextQuestion[1];

                        //Generate new options for the next question to make the exam flow stays random
                        List<String> newOptions = new ArrayList<>();
                        newOptions.add(correctAnswer[0]);

                        while (newOptions.size() < 4) {
                            int newIncorrectOption = random.nextInt(100);
                            String newOptionString = String.valueOf(newIncorrectOption);
                            if (!newOptions.contains(newOptionString) && !newOptionString.equals(correctAnswer[0])) {
                                newOptions.add(newOptionString);
                            }
                        }

                        // Shuffle and set new options
                        Collections.shuffle(newOptions);
                        for (int j = 0; j < selections.length; j++) {
                            selections[j].setText(newOptions.get(j));
                        }

                        // Restart the timer for the next question or wait until the timer ends and
                        //moves to the next question
                        countDown.start();
                    } else {
                        //here if we don't have any more questions in the selected questions arraylist
                        //then the quiz ended and we have to check what the user scored
                        Toast.makeText(MathGame.this, "Great You Got :"+NumberOfCorrectAnswers+" Correct And Got :"+NumberOfFalseAnswers+" Wrong", Toast.LENGTH_SHORT).show();
                        //write the user score on the database
                        DataBaseHelper dataBaseHelper = new DataBaseHelper(MathGame.this, "User", null, 1);
                        dataBaseHelper.updateUserScore(username, score);
                        Intent intent = new Intent(MathGame.this, End.class);
                        //here we passed the name and the score to the next activity so that we can make
                        //the user sees his score
                        intent.putExtra("USERNAME", username);
                        intent.putExtra("SCORE", String.valueOf(score));
                        startActivity(intent);
                        finish();
                    }
                });
            }

            // Initialize and start the countdown timer
            countDown = new CountDownTimer(10000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    //set the timer remaining time as 10000/1000 = 10sec
                    //then the counter goes 9000/1000 = 9sec and so on
                    timeText.setText(String.valueOf(millisUntilFinished / 1000));
                }

                @Override
                public void onFinish() {
                    //Move to the next question if 10 sec ended
                    //here's the same logic as we are doing for the if the user clicked a button
                    questionIndex[0]++;
                    questionsTrackText.setText(String.valueOf(questionIndex[0]));
                    if (questionIndex[0] < selectedQuestions.size()) {
                        String[] nextQuestion = selectedQuestions.get(questionIndex[0]);
                        questionsText.setText(nextQuestion[0]);
                        correctAnswer[0] = nextQuestion[1];

                        List<String> newOptions = new ArrayList<>();
                        newOptions.add(correctAnswer[0]);

                        while (newOptions.size() < 4) {
                            int newIncorrectOption = random.nextInt(100);
                            String newOptionString = String.valueOf(newIncorrectOption);
                            if (!newOptions.contains(newOptionString) && !newOptionString.equals(correctAnswer[0])) {
                                newOptions.add(newOptionString);
                            }
                        }

                        Collections.shuffle(newOptions);
                        for (int j = 0; j < selections.length; j++) {
                            selections[j].setText(newOptions.get(j));
                        }

                        countDown.start();
                    } else {
                        Toast.makeText(MathGame.this, "Great You Got :"+NumberOfCorrectAnswers+"Correct And Got :"+NumberOfFalseAnswers+"Wrong", Toast.LENGTH_SHORT).show();
                        DataBaseHelper dataBaseHelper = new DataBaseHelper(MathGame.this, "User", null, 1);
                        dataBaseHelper.updateUserScore(username, score);
                        Intent intent = new Intent(MathGame.this, End.class);
                        intent.putExtra("USERNAME", username);
                        intent.putExtra("SCORE", String.valueOf(score));
                        startActivity(intent);
                        finish();
                    }
                }
            }.start();
        }
    }
}
