//odyshbayeh---1201462
package com.example.a1201462_ody_shbayeh;

import static android.app.ProgressDialog.show;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class DataBaseHelper extends SQLiteOpenHelper {
    //the same object from the EXP4
    public DataBaseHelper(Context context, String name,
                          SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //creating the users Tab;e
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE User(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT UNIQUE NOT NULL, EMAIL TEXT, BIRTHDATE TEXT,SCORE INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    //Method to insert the user info to the table
    public boolean insertUser(User user) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", user.getUsername());
        contentValues.put("BIRTHDATE", user.getBirthdate());
        contentValues.put("EMAIL", user.getEmail());

        // Insert into the Users table and check if insertion was successful
        long result = sqLiteDatabase.insert("User", null, contentValues);

        return result != -1;
    }

    //Method to return the top 5 users and their scores
    public Cursor getAllUsersScores() {
        //here we get the top players depending on the score
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT NAME, SCORE FROM User ORDER BY SCORE DESC LIMIT 5", null);
    }

    //Method to return the unique named players:
    public int getTotalPlayers() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        //here we get the distinct users from the database
        // which should all be cause we are checking if they already exists in the first activity if
        //they do we don't accept them
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(DISTINCT NAME) AS TotalPlayers FROM User", null);
        int totalPlayers = 0;
        if (cursor.moveToFirst()) {
            totalPlayers = cursor.getInt(0);
        }
        cursor.close();
        return totalPlayers;
    }

    //method to get the players score based on a specific name
    public int getPlayerScores(String playerName) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        int scores=0;
        //here we get the score from the user provides a name
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT SCORE FROM User WHERE NAME = ?", new String[]{playerName});
        if (cursor.moveToFirst()) {
            scores=cursor.getInt(0);
        }
        cursor.close();
        return scores;
    }

    //Method to calculate the average score value
    public double getAverageScore() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        double averageScore = 0.0;
        //here the query must return a number for the avg scores among the records
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT AVG(SCORE) AS AverageScore FROM User", null);

        if (cursor.moveToFirst()) {
            averageScore = cursor.getDouble(0);
        }
        cursor.close();
        return averageScore;
    }

    //get the highiest score
    public String getHighestScoreWithName() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String result = "No players found"; // Default message if no players exist

        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT NAME, SCORE FROM User WHERE SCORE = (SELECT MAX(SCORE) FROM User)", null);

        if (cursor.moveToFirst()) {
            //here the query must return the user that have the max score
            String playerName = cursor.getString(0); // Get the player's name
            double highestScore = cursor.getDouble(1); // Get the highest score
            result ="Highest Score: " + highestScore +" By the Player: " + playerName;
        }
        cursor.close();
        return result;
    }

    //Method to set the user score.
    public boolean updateUserScore(String username, int score) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("SCORE", score);
        // Update the user score where username matches the provided username
        int result = sqLiteDatabase.update("User", contentValues, "NAME = ?", new String[]{username});
        return result > 0;
    }

    //Method to validate the username if it's already in the database or not
    public boolean isUsernameValid(String username) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM User WHERE NAME = ?", new String[]{username});
        boolean isUnique = false;
        if (cursor.moveToFirst()) {
            isUnique = cursor.getInt(0) == 0;
        }
        cursor.close();
        return isUnique;
    }
}

