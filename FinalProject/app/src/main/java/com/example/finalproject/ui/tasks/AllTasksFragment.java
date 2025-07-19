package com.example.finalproject.ui.tasks;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finalproject.DatabaseHelper;
import com.example.finalproject.HttpManager;
import com.example.finalproject.R;
import com.example.finalproject.Task;
import com.example.finalproject.TaskJsonParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AllTasksFragment extends Fragment {

    private ListView allTasksListView;
    private Button btnFetchFromDB, btnFetchFromAPI;
    private Spinner sortSpinner;
    private TodayTasksAdapter adapter;
    private String userEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_tasks, container, false);

        allTasksListView = view.findViewById(R.id.all_tasks_list);
        btnFetchFromDB = view.findViewById(R.id.btn_fetch_tasks_db);
        btnFetchFromAPI = view.findViewById(R.id.btn_fetch_tasks_api);
        sortSpinner = view.findViewById(R.id.sort_spinner_all);

        if (getArguments() != null) {
            userEmail = getArguments().getString("USER_EMAIL");
        }

        btnFetchFromDB.setOnClickListener(v -> fetchTasksFromDatabase());
        btnFetchFromAPI.setOnClickListener(v -> fetchTasksFromAPI());

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

    private void fetchTasksFromDatabase() {
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(getContext(), "User email is missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            List<Task> tasks = dbHelper.getTasksForUser(userEmail);

            adapter = new TodayTasksAdapter(getContext(), tasks, userEmail);
            allTasksListView.setAdapter(adapter);

            if (tasks.isEmpty()) {
                Toast.makeText(getContext(), "No tasks found in the database!", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e("AllTasksFragment", "Error fetching tasks from database: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Error fetching tasks. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchTasksFromAPI() {
        String apiUrl = "https://mocki.io/v1/d8305501-3645-43d1-81af-da6f7ea883ef";
        new FetchTasksAsyncTask(apiUrl).execute();
    }

    private class FetchTasksAsyncTask extends AsyncTask<Void, Void, List<Task>> {
        private String apiUrl;

        public FetchTasksAsyncTask(String apiUrl) {
            this.apiUrl = apiUrl;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getContext(), "Fetching tasks from API...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected List<Task> doInBackground(Void... voids) {
            try {
                String jsonData = HttpManager.getData(apiUrl);
                if (jsonData != null && !jsonData.isEmpty()) {
                    return TaskJsonParser.getObjectFromJson(jsonData);
                }
            } catch (Exception e) {
                Log.e("AllTasksFragment", "Error fetching tasks from API: " + e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Task> tasks) {
            super.onPostExecute(tasks);

            if (tasks != null) {
                adapter = new TodayTasksAdapter(getContext(), tasks, userEmail);
                allTasksListView.setAdapter(adapter);

                if (tasks.isEmpty()) {
                    Toast.makeText(getContext(), "No tasks found in the API response!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Failed to fetch data from the API!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}