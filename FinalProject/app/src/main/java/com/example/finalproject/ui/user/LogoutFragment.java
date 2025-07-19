package com.example.finalproject.ui.user;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.finalproject.MainActivity;
import com.example.finalproject.R;

public class LogoutFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout once and return it
        View view = inflater.inflate(R.layout.fragment_logout, container, false);
        // Show a toast message
        Toast.makeText(getActivity(), "Hope You Finished Your Daily Tasks", Toast.LENGTH_SHORT).show();
        return view;
    }
}
