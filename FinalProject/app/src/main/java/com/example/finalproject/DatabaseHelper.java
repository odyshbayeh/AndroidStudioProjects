package com.example.finalproject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "task_manager.db";
    private static final int DATABASE_VERSION = 1;
    private static final String USER_TABLE = "users";
    private static final String TASK_TABLE = "tasks";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_FIRST_NAME = "first_name";
    private static final String COLUMN_LAST_NAME = "last_name";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DUE_DATE = "due_date";
    private static final String COLUMN_PRIORITY = "priority";
    private static final String COLUMN_COMPLETION_STATUS = "completion_status";
    private static final String COLUMN_REMINDER_TIME = "reminder_time";
    private static final String COLUMN_USER_EMAIL = "user_email";
    public static final String PRIORITY_HIGH = "High";
    public static final String PRIORITY_MEDIUM = "Medium";
    public static final String PRIORITY_LOW = "Low";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + USER_TABLE + "(" +
                COLUMN_EMAIL + " TEXT PRIMARY KEY, " +
                COLUMN_FIRST_NAME + " TEXT, " +
                COLUMN_LAST_NAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(CREATE_USER_TABLE);

        String CREATE_TASK_TABLE = "CREATE TABLE " + TASK_TABLE + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_DUE_DATE + " TEXT, " +
                COLUMN_PRIORITY + " TEXT, " +
                COLUMN_COMPLETION_STATUS + " INTEGER, " +
                COLUMN_REMINDER_TIME + " TEXT, " +
                COLUMN_USER_EMAIL + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_USER_EMAIL + ") REFERENCES " + USER_TABLE + "(" + COLUMN_EMAIL + ") " +
                "ON DELETE CASCADE ON UPDATE CASCADE)";
        db.execSQL(CREATE_TASK_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE);
        onCreate(db);
    }


    /////////////////////UserDataBaseFunctions//////////////////////////////

    public void insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", user.getEmail());
        values.put("first_name", user.getFirstName());
        values.put("last_name", user.getLastName());
        values.put("password", user.getPassword());

        long result = db.insert("users", null, values);
        db.close();

        if (result == -1) {
            Log.e("DatabaseHelper", "Failed to insert user.");
        } else {
            Log.d("DatabaseHelper", "User inserted successfully.");
        }
    }

    public String getUserNameByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String fullName = null;
        Cursor cursor = db.query(
                USER_TABLE,
                new String[]{COLUMN_FIRST_NAME, COLUMN_LAST_NAME},
                COLUMN_EMAIL + " = ?",
                new String[]{email},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME));
            fullName = firstName + " " + lastName;
            cursor.close();
        }

        db.close();
        return fullName;
    }

    public boolean verifyUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                "users",
                null,
                "email = ? AND password = ?",
                new String[]{email, password},
                null,
                null,
                null
        );
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public int updateUser(String oldEmail, String newEmail, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        int rowsAffected = 0;

        try {
            ContentValues userValues = new ContentValues();
            userValues.put("email", newEmail);
            userValues.put("password", newPassword);

            rowsAffected = db.update("users", userValues, "email = ?", new String[]{oldEmail});

            if (rowsAffected > 0) {
                Log.d("DatabaseHelper", "User credentials updated successfully in users table.");

                ContentValues taskValues = new ContentValues();
                taskValues.put("user_email", newEmail);

                int tasksUpdated = db.update("tasks", taskValues, "user_email = ?", new String[]{oldEmail});
                Log.d("DatabaseHelper", "Updated " + tasksUpdated + " tasks to the new email in tasks table.");

                db.setTransactionSuccessful();
            } else {
                Log.d("DatabaseHelper", "Failed to update user credentials. Old Email not found: " + oldEmail);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error updating user credentials and tasks: " + e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }

        return rowsAffected;
    }








    /////////////////////////////////Tasks DataBaseFunctions//////////////////////

    public void insertTask(Task task, String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_DUE_DATE, task.getDueDate());
        values.put(COLUMN_PRIORITY, task.getPriority() != null ? task.getPriority() : PRIORITY_MEDIUM);
        values.put(COLUMN_COMPLETION_STATUS, task.isCompletionStatus() ? 1 : 0);
        values.put(COLUMN_REMINDER_TIME, task.getReminderTime());
        values.put(COLUMN_USER_EMAIL, userEmail);

        db.insert(TASK_TABLE, null, values);
        db.close();
    }

    public List<Task> getTasksForToday(String todayDate, String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Task> taskList = new ArrayList<>();

        Cursor cursor = db.query(
                TASK_TABLE,
                null,
                COLUMN_DUE_DATE + " LIKE ? AND " + COLUMN_USER_EMAIL + " = ?",
                new String[]{todayDate + "%", userEmail}, // Match any time on the given date
                null,
                null,
                COLUMN_PRIORITY + " ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETION_STATUS)) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REMINDER_TIME))
                );
                taskList.add(task);
                Log.d("DatabaseHelper", "Fetched Task: " + task.getTitle());
            } while (cursor.moveToNext());
        } else {
            Log.d("DatabaseHelper", "No tasks found for date: " + todayDate + " and email: " + userEmail);
        }
        cursor.close();
        db.close();
        return taskList;
    }

    public List<Task> getTasksForUser(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Task> taskList = new ArrayList<>();
        Cursor cursor = db.query(
                TASK_TABLE,
                null,
                COLUMN_USER_EMAIL + " = ?",
                new String[]{userEmail},
                null,
                null,
                COLUMN_DUE_DATE + " ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETION_STATUS)) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REMINDER_TIME))
                );
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return taskList;
    }

    public void markTaskAsCompleted(int taskId, String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("completion_status", 1);

        int rowsAffected = db.update(
                TASK_TABLE,
                values,
                COLUMN_ID + " = ? AND " + COLUMN_USER_EMAIL + " = ?",
                new String[]{String.valueOf(taskId), userEmail}
        );

        if (rowsAffected > 0) {
            Log.d("DatabaseHelper", "Task marked as completed.");
        } else {
            Log.e("DatabaseHelper","the user Email is "+userEmail);
            Log.e("DatabaseHelper","the task ID is "+taskId);
            Log.e("DatabaseHelper", "Failed to mark task as completed or task does not belong to the user.");
        }

        db.close();
    }

    public void markTaskAsCompletedFalse(int taskId, String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("completion_status", 0);

        int rowsAffected = db.update(
                TASK_TABLE,
                values,
                COLUMN_ID + " = ? AND " + COLUMN_USER_EMAIL + " = ?",
                new String[]{String.valueOf(taskId), userEmail}
        );

        if (rowsAffected > 0) {
            Log.d("DatabaseHelper", "Task marked as completed.");
        } else {
            Log.e("DatabaseHelper","the user Email is "+userEmail);
            Log.e("DatabaseHelper","the task ID is "+taskId);
            Log.e("DatabaseHelper", "Failed to mark task as completed or task does not belong to the user.");
        }

        db.close();
    }

    public void deleteTask(int taskId, String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();

        int rowsDeleted = db.delete(
                TASK_TABLE,
                COLUMN_ID + " = ? AND " + COLUMN_USER_EMAIL + " = ?",
                new String[]{String.valueOf(taskId), userEmail}
        );

        db.close();

        if (rowsDeleted > 0) {
            Log.d("DatabaseHelper", "Task deleted successfully for user: " + userEmail);
        } else {
            Log.d("DatabaseHelper", "No task found with id " + taskId + " for user: " + userEmail);
        }
    }


    public List<Task> getCompletedTasks(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Task> taskList = new ArrayList<>();

        Cursor cursor = db.query(
                TASK_TABLE,
                null,
                "completion_status = ? AND user_email = ?",
                new String[]{"1", userEmail},
                null,
                null,
                "due_date ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getString(cursor.getColumnIndexOrThrow("due_date")),
                        cursor.getString(cursor.getColumnIndexOrThrow("priority")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("completion_status")) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow("reminder_time"))
                );
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return taskList;
    }

    public int updateTaskReminder(int taskId, String userEmail, String newReminderTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("reminder_time", newReminderTime);

        int rowsAffected = db.update(
                TASK_TABLE,
                values,
                "id = ? AND user_email = ?",
                new String[]{String.valueOf(taskId), userEmail}
        );

        db.close();
        return rowsAffected;
    }

    public boolean areAllTasksCompletedForToday(String todayDate, String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                "tasks",
                null,
                "due_date LIKE ? AND completion_status = 0 AND user_email = ?",
                new String[]{todayDate + "%", userEmail},
                null,
                null,
                null
        );

        boolean allCompleted = !cursor.moveToFirst();
        if (allCompleted) {
            Log.d("DatabaseHelper", "All tasks completed for date: " + todayDate + " and email: " + userEmail);
        } else {
            Log.d("DatabaseHelper", "Incomplete tasks exist for date: " + todayDate + " and email: " + userEmail);
        }

        cursor.close();
        db.close();
        return allCompleted;
    }

    public List<Task> searchTasksByKeywordAndDateRange(String keyword, String startDate, String endDate, String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Task> taskList = new ArrayList<>();

        String query = "SELECT * FROM tasks WHERE user_email = ?";
        List<String> args = new ArrayList<>();
        args.add(userEmail);

        if (keyword != null && !keyword.isEmpty()) {
            query += " AND (title LIKE ? OR description LIKE ?)";
            args.add("%" + keyword + "%");
            args.add("%" + keyword + "%");
        }

        if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
            query += " AND due_date BETWEEN ? AND ?";
            args.add(startDate);
            args.add(endDate);
        }

        query += " ORDER BY due_date ASC";

        Cursor cursor = db.rawQuery(query, args.toArray(new String[0]));

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getString(cursor.getColumnIndexOrThrow("due_date")),
                        cursor.getString(cursor.getColumnIndexOrThrow("priority")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("completion_status")) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow("reminder_time"))
                );
                taskList.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return taskList;
    }
}