package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private String[] quoteWords; // Array of words from the quote
    private int currentWordIndex = 0; // To track the current word being typed
    private String randomQuote; // The random quote

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

        InputStream inputStream = getResources().openRawResource(R.raw.typing_race_quotes);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder stringBuilder = new StringBuilder();
        String line;

        // Read the file line by line
        while (true) {
            try {
                if (!((line = reader.readLine()) != null))
                    break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stringBuilder.append(line).append("\n");
        }

        // Convert the whole content into a single string
        String allQuotes = stringBuilder.toString();

        // Split the quotes by a line break, assuming each quote is on a new line
        String[] quotesArray = allQuotes.split("\n");

        // Set up TextViews for the quote and typing feedback
        TextView TheText = findViewById(R.id.TheText);
        TheText.setSingleLine(false);
        TheText.setEllipsize(null);
        TheText.setEnabled(false);
        EditText TypingText = findViewById(R.id.TypingText);

        // Select a random quote from the array
        randomQuote = quotesArray[new Random().nextInt(quotesArray.length)].toLowerCase();
        quoteWords = randomQuote.split(" "); // Split the quote into words

        // Set the full quote in TheText TextView
        TheText.setText(randomQuote.toLowerCase());

        // Add a TextWatcher to TypingText for real-time feedback
        TypingText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String userInput = s.toString();

                // Get the current word the user should be typing
                if (currentWordIndex < quoteWords.length) {
                    String currentWord = quoteWords[currentWordIndex];

                    // Create a SpannableString to highlight the text
                    SpannableString spannableQuote = new SpannableString(randomQuote);

                    // Compare each character in userInput with the current word
                    for (int i = 0; i < userInput.length(); i++) {
                        if (i < currentWord.length()) {
                            // Correct character - make it green
                            if (userInput.charAt(i) == currentWord.charAt(i)) {
                                TypingText.setTextColor(Color.GREEN);
                                TypingText.setHint(currentWord);
                                spannableQuote.setSpan(new ForegroundColorSpan(Color.GREEN),
                                        getWordStartIndex(currentWordIndex) + i,
                                        getWordStartIndex(currentWordIndex) + i + 1,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            // Incorrect character - make it red
                            else {
                                TypingText.setHint(currentWord);
                                TypingText.setTextColor(Color.RED);
                                spannableQuote.setSpan(new ForegroundColorSpan(Color.RED),
                                        getWordStartIndex(currentWordIndex) + i,
                                        getWordStartIndex(currentWordIndex) + i + 1,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                    }

                    // If the user correctly types the whole word, move to the next word
                    if (userInput.equals(currentWord)) {
                        currentWordIndex++;
                        TypingText.setText(""); // Clear the input for the next word
                    }

                    // Update TheText with the highlighted feedback
                    TheText.setText(spannableQuote);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Helper method to calculate the starting index of a word in the full quote
    private int getWordStartIndex(int wordIndex) {
        int startIndex = 0;
        for (int i = 0; i < wordIndex; i++) {
            startIndex += quoteWords[i].length() + 1; // Add 1 for the space between words
        }
        return startIndex;
    }
}
