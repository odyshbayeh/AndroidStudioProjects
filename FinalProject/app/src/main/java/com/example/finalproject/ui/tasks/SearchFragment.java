package com.example.finalproject.ui.tasks;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finalproject.DatabaseHelper;
import com.example.finalproject.R;
import com.example.finalproject.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchFragment extends Fragment {

    private String userEmail;
    private Spinner sortSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        userEmail = getArguments() != null ? getArguments().getString("USER_EMAIL") : null;

        EditText keywordEditText = view.findViewById(R.id.search_keyword);
        EditText startDateEditText = view.findViewById(R.id.search_start_date);
        EditText endDateEditText = view.findViewById(R.id.search_end_date);
        Button searchButton = view.findViewById(R.id.search_button);
        ListView searchResultsList = view.findViewById(R.id.search_results_list);
        TextView noResultsText = view.findViewById(R.id.no_results_text);
        sortSpinner = view.findViewById(R.id.sort_spinner_search);

        startDateEditText.setOnClickListener(v -> showDatePickerDialog((date) -> startDateEditText.setText(date)));

        endDateEditText.setOnClickListener(v -> showDatePickerDialog((date) -> endDateEditText.setText(date)));

        searchButton.setOnClickListener(v -> {
            String keyword = keywordEditText.getText().toString().trim();
            String startDate = startDateEditText.getText().toString().trim();
            String endDate = endDateEditText.getText().toString().trim();

            if (TextUtils.isEmpty(keyword) && TextUtils.isEmpty(startDate) && TextUtils.isEmpty(endDate)) {
                Toast.makeText(getContext(), "Please provide at least one search criterion", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userEmail == null) {
                Toast.makeText(getContext(), "User email is not available", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            List<Task> searchResults = dbHelper.searchTasksByKeywordAndDateRange(keyword, startDate, endDate, userEmail);

            if (searchResults.isEmpty()) {
                noResultsText.setVisibility(View.VISIBLE);
                searchResultsList.setVisibility(View.GONE);
            } else {
                noResultsText.setVisibility(View.GONE);
                searchResultsList.setVisibility(View.VISIBLE);

                TodayTasksAdapter adapter = new TodayTasksAdapter(getContext(), searchResults,userEmail);
                searchResultsList.setAdapter(adapter);
                sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (adapter != null) {
                            List<Task> currentTasks = new ArrayList<>(adapter.getTasks());

                            if (position == 0) {
                                Collections.sort(currentTasks, Comparator.comparing(Task::getDueDate));
                            } else if (position == 1) {
                                Collections.sort(currentTasks, (task1, task2) -> {
                                    int priority1 = mapPriority(task1.getPriority());
                                    int priority2 = mapPriority(task2.getPriority());
                                    return Integer.compare(priority2, priority1);
                                });
                            }

                            adapter.updateTasks(currentTasks);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        });

        return view;

    }
    private int mapPriority(String priority) {
        switch (priority) {
            case "High":
                return 3;
            case "Medium":
                return 2;
            case "Low":
                return 1;
            default:
                return 0;
        }
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


    private interface OnDateSetListener {
        void onDateSet(String date);
    }
}
