package com.example.finalproject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class TaskJsonParser {
    public static List<Task> getObjectFromJson(String json) {
        List<Task> tasks = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Task task = new Task(
                        jsonObject.getInt("id"),
                        jsonObject.getString("title"),
                        jsonObject.getString("description"),
                        jsonObject.getString("dueDate"),
                        jsonObject.getString("priority"),
                        jsonObject.getBoolean("completionStatus"),
                        jsonObject.getString("reminderTime")
                );
                tasks.add(task);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tasks;
    }
}

