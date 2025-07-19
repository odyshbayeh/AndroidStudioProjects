package com.example.finalproject.ui.tasks;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.finalproject.DatabaseHelper;
import com.example.finalproject.R;
import com.example.finalproject.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CompletedTasksFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private String userEmail;
    private Spinner sortSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed_tasks, container, false);

        sortSpinner = view.findViewById(R.id.sort_spinner_completed);

        if (getArguments() != null) {
            userEmail = getArguments().getString("USER_EMAIL");
        }

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(requireContext(), "User email not found", Toast.LENGTH_SHORT).show();
            return view;
        }

        dbHelper = new DatabaseHelper(requireContext());

        List<Task> completedTasks = dbHelper.getCompletedTasks(userEmail);
        if (completedTasks.isEmpty()) {
            Toast.makeText(requireContext(), "No completed tasks found", Toast.LENGTH_SHORT).show();
        } else {
            ListView listView = view.findViewById(R.id.completed_tasks_list);
            TodayTasksAdapter adapter = new TodayTasksAdapter(requireContext(), completedTasks, userEmail);
            listView.setAdapter(adapter);
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
}
