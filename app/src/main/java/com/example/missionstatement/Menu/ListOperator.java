package com.example.missionstatement.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.missionstatement.Firebase.Realtime;
import com.example.missionstatement.Firebase.Storage;
import com.example.missionstatement.Fragment.FragmentProfile;
import com.example.missionstatement.R;
import com.example.missionstatement.Tools.CryptoUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class ListOperator extends AppCompatActivity {
    RecyclerView recyclerView;
    Realtime server;
    Storage storage;
    ArrayList<FragmentProfile> lst;
    private static int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lst = new ArrayList<>();
        setContentView(R.layout.activity_list_operator);
        recyclerView = findViewById(R.id.lst_operator);
        server = new Realtime(this);
        storage = Storage.getInstance();
        CompletableFuture<HashMap<String, HashMap<String, String>>> future = server.checkDataSnapshot("human");
        future.thenAccept(data -> {
            for (HashMap<String, String> minnerMap : data.values()) {
                HashMap<String, String> innerMap=decryptHuman(minnerMap);
                if (innerMap.get("position").equals("OPERATOR")) {
                    Log.d(null, "HashCreate: " + innerMap);
                    FragmentProfile fragmentProfile = new FragmentProfile();
                    fragmentProfile.setDeatils(innerMap);
                    lst.add(fragmentProfile);

                }
            }
            Log.d(null, "HashCreate: " + lst.get(0).getDeatils());
            MyAdapter adapter = new MyAdapter(lst, getSupportFragmentManager());

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

        }).exceptionally(e -> {
            // Handle exception if there was an error
            Log.e("lstActivity", "Error: " + e.getMessage());
            return null;
        });


    }

    private HashMap<String, String> decryptHuman(HashMap<String, String> mdeatils) {
        HashMap<String, String> deatils = mdeatils;
        deatils.forEach((key, value) -> {
            // בדיקה אם הערך הוא String
            if (value instanceof String) {
                // שינוי הערך
                try {
                    String strValue = CryptoUtils.decrypt((String) value);
                    deatils.put(key, strValue);
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        });
        return deatils;
    }
}