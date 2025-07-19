package com.example.finalproject.ui.tasks;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finalproject.DatabaseHelper;
import com.example.finalproject.R;
import com.example.finalproject.Task;

import java.util.Calendar;

public class NewTaskFragment extends Fragment {
    private String userEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_task, container, false);

        userEmail = getArguments() != null ? getArguments().getString("USER_EMAIL") : null;

        EditText titleEditText = view.findViewById(R.id.task_title);
        EditText descriptionEditText = view.findViewById(R.id.task_description);
        EditText dueDateEditText = view.findViewById(R.id.task_due_date);
        EditText dueTimeEditText = view.findViewById(R.id.task_due_time);
        Spinner prioritySpinner = view.findViewById(R.id.task_priority);
        Button addTaskButton = view.findViewById(R.id.add_task_button);

        String[] priorities = { "Medium","High", "Low"};
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                priorities
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);

        dueDateEditText.setOnClickListener(v -> showDatePickerDialog(date -> dueDateEditText.setText(date)));

        dueTimeEditText.setOnClickListener(v -> showTimePickerDialog(time -> dueTimeEditText.setText(time)));

        addTaskButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            String dueDate = dueDateEditText.getText().toString().trim();
            String dueTime = dueTimeEditText.getText().toString().trim();
            String priority = prioritySpinner.getSelectedItem().toString();

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(dueDate) || TextUtils.isEmpty(dueTime)) {
                Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userEmail == null) {
                Toast.makeText(getContext(), "User email is not available", Toast.LENGTH_SHORT).show();
                return;
            }

            Task newTask = new Task();
            newTask.setTitle(title);
            newTask.setDescription(description);
            newTask.setDueDate(dueDate + " " + dueTime);
            newTask.setPriority(priority);
            newTask.setUserEmail(userEmail);

            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            dbHelper.insertTask(newTask, userEmail);

            Toast.makeText(getContext(), "Task added successfully", Toast.LENGTH_SHORT).show();

            titleEditText.setText("");
            descriptionEditText.setText("");
            dueDateEditText.setText("");
            dueTimeEditText.setText("");
            prioritySpinner.setSelection(0);
        });

        return view;
    }


    private void showDatePickerDialog(OnDateSetListener listener) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    String formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    listener.onDateSet(formattedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }


    private void showTimePickerDialog(OnTimeSetListener listener) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (TimePicker view, int selectedHour, int selectedMinute) -> {
                    String formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                    listener.onTimeSet(formattedTime);
                },
                hour, minute, true
        );

        timePickerDialog.show();
    }


    private interface OnDateSetListener {
        void onDateSet(String date);
    }


    private interface OnTimeSetListener {
        void onTimeSet(String time);
    }
}
