package com.example.finalproject.ui.tasks;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.finalproject.DatabaseHelper;
import com.example.finalproject.R;
import com.example.finalproject.Task;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class TodayFragment extends Fragment {
    private ListView todayTasksListView;
    private String userEmail;
    private Handler handler;
    private Runnable checkTasksRunnable;
    private AtomicBoolean tasksDoneForToday = new AtomicBoolean(false);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        todayTasksListView = view.findViewById(R.id.today_tasks_list);
        handler = new Handler();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getArguments() != null) {
            userEmail = getArguments().getString("USER_EMAIL");
            Log.d("TodayFragment", "Re-fetched userEmail: " + userEmail);
        }

        if (userEmail != null) {
            String todayDate = getTodayDate();
            Log.d("TodayFragment", "Starting periodic task check for date: " + todayDate);

            loadTasksForToday(todayDate);

            if (!tasksDoneForToday.get()) {
                checkTasksRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                            boolean allCompleted = dbHelper.areAllTasksCompletedForToday(todayDate.trim(), userEmail.trim());
                            if (allCompleted) {
                                tasksDoneForToday.set(true);
                                Toast.makeText(getActivity(), "Congratulations! All tasks for today are completed!", Toast.LENGTH_SHORT).show();
                                Log.d("TodayFragment", "All tasks are completed for date: " + todayDate);
                            } else {
                                Log.d("TodayFragment", "There are incomplete tasks for date: " + todayDate);
                            }
                            if (!tasksDoneForToday.get()) {
                                handler.postDelayed(this, 5000);
                            }
                        } catch (Exception e) {
                            Log.e("TodayFragment", "Error during task check: " + e.getMessage(), e);
                        }
                    }
                };

                handler.post(checkTasksRunnable);
            }
            handler.post(checkTasksRunnable);
        } else {
            Toast.makeText(getActivity(), "Error fetching tasks. Please try again.", Toast.LENGTH_SHORT).show();
            Log.e("TodayFragment", "User email is null in onResume(). Cannot fetch tasks.");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null && checkTasksRunnable != null) {
            handler.removeCallbacks(checkTasksRunnable);
        }
    }

    private void loadTasksForToday(String todayDate) {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            List<Task> todayTasks = dbHelper.getTasksForToday(todayDate.trim(), userEmail.trim());

            if (todayTasks != null && !todayTasks.isEmpty()) {
                Log.d("TodayFragment", "Number of tasks fetched: " + todayTasks.size());
                TodayTasksAdapter adapter = new TodayTasksAdapter(getContext(), todayTasks,userEmail);
                todayTasksListView.setAdapter(adapter);
                tasksDoneForToday.set(false);

            } else {
                Log.d("TodayFragment", "No tasks found for userEmail: " + userEmail + " on " + todayDate);
                Toast.makeText(getActivity(), "No tasks for today!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("TodayFragment", "Error loading tasks: " + e.getMessage(), e);
            Toast.makeText(getActivity(), "Error loading tasks. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getTodayDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatter.format(new Date());
    }
}
