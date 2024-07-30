package com.example.missionstatement.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.missionstatement.Firebase.Realtime;
import com.example.missionstatement.GV.GridAdapter;
import com.example.missionstatement.Manager.ManagerActivity;
import com.example.missionstatement.R;

import java.util.ArrayList;
import java.util.List;

public class DataFragment extends Fragment {
    private Context context;
    private Realtime server;
    private GridView gridView;
    private Button buttonAction;
private ManagerActivity.Callbackk callbackk;
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data, container, false);
        context = view.getContext();
        server = new Realtime(context);

        // Find views
        gridView = view.findViewById(R.id.gridView);
        buttonAction = view.findViewById(R.id.buttonDataAction);

        // Initialize data


     callbackk.gridVieFill(gridView,context);

        // Set button click listener
        buttonAction.setOnClickListener(v -> {
            // Perform action
            Toast.makeText(context, "Button clicked!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    public ManagerActivity.Callbackk getCallbackk() {
        return callbackk;
    }

    public void setCallbackk(ManagerActivity.Callbackk callbackk) {
        this.callbackk = callbackk;
    }
}
