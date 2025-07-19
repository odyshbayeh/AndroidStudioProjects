package com.example.finalproject.ui.tasks;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.finalproject.DatabaseHelper;
import com.example.finalproject.R;
import com.example.finalproject.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TodayTasksAdapter extends ArrayAdapter<Task> {
    private Context context;
    private List<Task> tasks;
    private String Email;

    public TodayTasksAdapter(Context context, List<Task> tasks,String Email) {
        super(context, R.layout.item_task, tasks);
        this.context = context;
        this.tasks = tasks;
        this.Email = Email;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        }

        Task task = tasks.get(position);

        TextView title = convertView.findViewById(R.id.task_title);
        TextView description = convertView.findViewById(R.id.task_description);
        TextView Completed = convertView.findViewById(R.id.completionstauts);
        TextView TaskDate = convertView.findViewById(R.id.TaskDate);
        CheckBox completedCheckBox = convertView.findViewById(R.id.task_completed);
        FloatingActionButton editButton = convertView.findViewById(R.id.edit_button);
        FloatingActionButton RemButton = convertView.findViewById(R.id.Rembtn);
        FloatingActionButton DeleteButton = convertView.findViewById(R.id.Delbtn);
        FloatingActionButton ShareButton = convertView.findViewById(R.id.Sharebtn);
        TextView TaskPriority = convertView.findViewById(R.id.task_priority);

        title.setText(task.getTitle());
        description.setText(task.getDescription());
        completedCheckBox.setChecked(task.isCompletionStatus());
        TaskDate.setText(task.getDueDate());
        TaskPriority.setText(task.getPriority());
        DatabaseHelper db = new DatabaseHelper(getContext());

        editButton.setOnClickListener(v -> {
            if(completedCheckBox.isChecked()) {
                db.markTaskAsCompleted(task.getId(), Email.trim());
                task.setCompletionStatus(true);
                Completed.setText("This Task Completion Status is " + task.isCompletionStatus());
                Toast.makeText(context, "Edit Task: " + task.getTitle(), Toast.LENGTH_SHORT).show();
            }else{
                db.markTaskAsCompletedFalse(task.getId(),Email.trim());
                task.setCompletionStatus(false);
                Completed.setText("This Task Completion Status is "+task.isCompletionStatus());
            }
        });

        DeleteButton.setOnClickListener(v -> {
            db.deleteTask(task.getId(), Email.trim());
            tasks.remove(position);
            notifyDataSetChanged();
            Toast.makeText(context, "Deleted Task: " + task.getTitle(), Toast.LENGTH_SHORT).show();
        });

        RemButton.setOnClickListener(v -> {
            openReminderDialog(task);
        });

        ShareButton.setOnClickListener(v -> {
            Intent gmailIntent = new Intent();
            gmailIntent.setAction(Intent.ACTION_SENDTO);
            gmailIntent.setData(Uri.parse("mailto:"));
            gmailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{Email});
            gmailIntent.putExtra(Intent.EXTRA_SUBJECT, "Task Details: " + task.getTitle());
            gmailIntent.putExtra(Intent.EXTRA_TEXT, createTaskDetailsMessage(task));

            if (gmailIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(gmailIntent);
            } else {
                Toast.makeText(context, "No email client found on this device.", Toast.LENGTH_SHORT).show();
            }
        });


        return convertView;
    }

    private String createTaskDetailsMessage(Task task) {
        return "Task Title: " + task.getTitle() + "\n" +
                "Description: " + task.getDescription() + "\n" +
                "Due Date: " + task.getDueDate() + "\n" +
                "Priority: " + task.getPriority() + "\n" +
                "Completion Status: " + (task.isCompletionStatus() ? "Completed" : "Not Completed") + "\n" +
                "Reminder Time: " + (task.getReminderTime() != null ? task.getReminderTime() : "No Reminder Set");
    }

    public void updateTasks(List<Task> updatedTasks) {
        this.tasks = updatedTasks;
        notifyDataSetChanged();
    }

    public List<Task> getTasks() {
        return tasks;
    }

    private void openReminderDialog(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_set_reminder, null);
        builder.setView(dialogView);

        EditText reminderTimeInput = dialogView.findViewById(R.id.reminder_time_input);
        Spinner reminderUnitSpinner = dialogView.findViewById(R.id.reminder_unit_spinner); // e.g., hours, days
        Button setReminderButton = dialogView.findViewById(R.id.set_reminder_button);
        Button snoozeButton = dialogView.findViewById(R.id.snooze_button);

        AlertDialog dialog = builder.create();

        setReminderButton.setOnClickListener(v1 -> {
            String reminderValue = reminderTimeInput.getText().toString().trim();
            String reminderUnit = reminderUnitSpinner.getSelectedItem().toString();

            if (!reminderValue.isEmpty()) {
                int reminderTime = Integer.parseInt(reminderValue);
                long reminderMillis = calculateReminderMillis(reminderTime, reminderUnit, task.getDueDate());

                DatabaseHelper dbHelper = new DatabaseHelper(context);
                dbHelper.updateTaskReminder(task.getId(), Email.trim(), String.valueOf(reminderMillis));

                scheduleNotification(task, reminderMillis);
                Log.d("DateDialogHelper", "Update date: " + task.getDueDate() + " and email: " + Email.trim());


                Toast.makeText(context, "Reminder set successfully!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Please enter a valid reminder time.", Toast.LENGTH_SHORT).show();
            }
        });

        snoozeButton.setOnClickListener(v2 -> {
            long snoozeMillis = System.currentTimeMillis() + 10 * 60 * 1000;
            scheduleNotification(task, snoozeMillis);
            Toast.makeText(context, "Reminder snoozed for 10 minutes!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private long calculateReminderMillis(int time, String unit, String dueDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            if (dueDate.endsWith("00:00")) {
                dueDate = dueDate.replace("00:00", "23:59");
            }

            Date dueDateTime = dateFormat.parse(dueDate);
            if (dueDateTime == null) {
                throw new Exception("Invalid due date format");
            }

            long dueMillis = dueDateTime.getTime();
            long reminderMillis;

            switch (unit) {
                case "Minutes":
                    reminderMillis = dueMillis - (time * 60 * 1000);
                    break;
                case "Hours":
                    reminderMillis = dueMillis - (time * 60 * 60 * 1000);
                    break;
                case "Days":
                    reminderMillis = dueMillis - (time * 24 * 60 * 60 * 1000);
                    break;
                default:
                    reminderMillis = dueMillis;
            }

            long currentTime = System.currentTimeMillis();
            if (reminderMillis <= currentTime) {
                throw new Exception("Reminder time is in the past. Current time: " + currentTime);
            }

            return reminderMillis;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error calculating reminder time. Please check the due date.", Toast.LENGTH_SHORT).show();
            return System.currentTimeMillis();
        }
    }


    private void scheduleNotification(@NonNull Task task, long reminderTime) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("TASK_TITLE", task.getTitle());
        intent.putExtra("TASK_DESCRIPTION", task.getDescription());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                task.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
        }
    }

}
