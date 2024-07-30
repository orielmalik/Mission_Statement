package com.example.missionstatement.Manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.missionstatement.CallBackType.Callback_test;
import com.example.missionstatement.Firebase.Realtime;
import com.example.missionstatement.Fragment.*;
import com.example.missionstatement.Fragment.FragmentQuest;
import com.example.missionstatement.GV.GridAdapter;
import com.example.missionstatement.Menu.Personality_Test;
import com.example.missionstatement.Objects.User;
import com.example.missionstatement.R;
import com.example.missionstatement.Tools.CryptoUtils;
import com.example.missionstatement.Tools.Functions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ManagerActivity extends AppCompatActivity {
    private User manager;
    private FrameLayout frameLayout;
    private FragmentQuest fragmentQuest;
    private DataFragment dataFragment;
    private Callback_test callbackTest;
    private String category;
    private Realtime server;
    private int maxClients;
    private float maxRating;
    private String[] operator;
    private boolean[] arr;
    private ProgressBar progressBar;

    public interface Callbackk {
        void addCategory(Set<String> set);

        void maxRating(Set<Float> set);

        void maxClients(List<Integer> clientsize);

        void writeWithAtomic(AtomicInteger integer1, AtomicInteger integer2, AtomicInteger integer3);

        void gridVieFill(GridView gridView, Context context, List<String[]> lst, String[] arr);
    }

    Callbackk callbackk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager);

        // Initialize UI elements
        progressBar = findViewById(R.id.progress_bar);

        try {
            manager = (User) getIntent().getSerializableExtra("user");
        } catch (NullPointerException n) {
            n.printStackTrace();
            Log.e("nullpointer", "onCreate: " + n.getMessage());
            Toast.makeText(this, "NO MANAGER", Toast.LENGTH_SHORT).show();
        }
        server = new Realtime(this);
        fragmentQuest = new FragmentQuest();
        dataFragment = new DataFragment();
        arr = new boolean[3];

        makeCallback();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.manager_fragment, fragmentQuest).commit();
    }

    private void makeCallback() {
        callbackTest = new Callback_test() {
            @Override
            public void uploadAnswers(RadioGroup radioGroup) {
                makeOptions(radioGroup);
            }

            @Override
            public void getResult(int index) {
                fragmentQuest.getRadioGroup().setVisibility(View.VISIBLE);
                switch (index) {
                    case 0:
                        Intent ad = new Intent(ManagerActivity.this, Personality_Test.class);
                        ad.putExtra("user", manager);
                        startActivity(ad);
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.manager_fragment, dataFragment).commit();
                        analyze();

                        break;
                }
                fragmentQuest.getRadioGroup().setVisibility(View.GONE);
                fragmentQuest.getView().setBackground(getDrawable(R.drawable.bck_manager));
            }

            @Override
            public boolean getCounterQuestion(int counter) {
                return false;
            }

            @Override
            public void setAnswer(String string, int i) {
            }

            @Override
            public boolean canStartBuildTest() {
                return false;
            }

            @Override
            public int state() {
                return 1;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public void ToastFrom(String s) {
            }
        };
        fragmentQuest.setCallback_test(callbackTest);
    }

    private void makeOptions(RadioGroup radioGroup) {
        radioGroup.setVisibility(View.VISIBLE);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton button = (RadioButton) radioGroup.getChildAt(i);

            if (button.getId() == selectedId) {
                radioGroup.clearCheck();
                switch (i) {
                    case 0:
                        Intent ad = new Intent(this, Personality_Test.class);
                        ad.putExtra("user", manager);
                        startActivity(ad);
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.manager_fragment, dataFragment).commit();
                        break;
                }
                radioGroup.setVisibility(View.GONE);
            }
        }
    }

    private void analyze() {
        callbackk = new Callbackk() {
            @Override
            public void addCategory(Set<String> set) {
                setCategory(Functions.findMostFrequent(set));
                arr[0] = true;
                gridVieFill(dataFragment.getGridView(), dataFragment.getContext(), dataFragment.getData(), new String[]{"FrequentCategory", getCategory()});
                Log.d("level tag", getCategory());
            }

            @Override
            public void maxRating(Set<Float> set) {
                setMaxRating(Functions.findMaxInSet(set));
                arr[1] = true;
                gridVieFill(dataFragment.getGridView(), dataFragment.getContext(), dataFragment.getData(), new String[]{"MaxRating", operator[1] + " sum " + getMaxClients()});
                Log.d("level tag", "" + getMaxRating());
            }

            @Override
            public void maxClients(List<Integer> clientsize) {
                setMaxClients(Functions.findMaxInList(clientsize));
                arr[2] = true;
                gridVieFill(dataFragment.getGridView(), dataFragment.getContext(), dataFragment.getData(), new String[]{"MaxClient", operator[0] + " count " + getMaxClients()});
                Log.d("level tag", "" + getMaxClients());
            }

            @Override
            public void writeWithAtomic(AtomicInteger integer1, AtomicInteger integer2, AtomicInteger integer3) {
            }

            @Override
            public void gridVieFill(GridView gridView, Context context, List<String[]> lst, String[] arr) {
                List<String[]> data = new ArrayList<>(lst);
                lst.add(new String[]{"Most common Results category", getCategory()});
                lst.add(new String[]{"Max rating", "operator" + getOperator()[0] + " number " + String.valueOf(getMaxRating())});
                lst.add(new String[]{"Max Clients", "operator" + getOperator()[1] + " number " + String.valueOf(getMaxClients())});

                GridAdapter adapter = new GridAdapter(context, data);
                gridView.setAdapter(adapter);

                gridView.setOnItemClickListener((parent, view1, position, id) -> {
                    String item = (String) adapter.getItem(position);
                    Toast.makeText(context, "Item clicked: " + item, Toast.LENGTH_SHORT).show();
                });
            }

            private void checkCompletion() {
                // Check if all operations are complete
                if (arr[0] || arr[1] || arr[2]) {
                    // Hide progress bar
                    progressBar.setVisibility(View.GONE);
                } else {
                    Log.d("level tag", "" + arr[0] + arr[1] + arr[2] + getMaxClients() + getCategory());
                }
            }
        };

        // Show progress bar while loading
        progressBar.setVisibility(View.GONE);
        Set<String> category = new HashSet<>();
        Set<Float> floats = new HashSet<>();
        List<Integer> clients = new ArrayList<>();
        AtomicInteger indexClient = new AtomicInteger(0);
        AtomicInteger index = new AtomicInteger(0);
        operator = new String[]{" ", " "};
        dataFragment.setCallbackk(callbackk);

        server.checkDataSnapshotnew(null).thenAccept(big -> {
            big.forEach((s, hashMap) -> {
                Log.d("key level 1", "" + s);

                switch (s) {
                    case "Results":
                        for (Object i : hashMap.values()) {
                            category.add(i.toString());
                            Log.d("key level up", i.toString());
                        }

                        callbackk.addCategory(category);
                        Log.d("key level 2", "" + category);
                        break;

                    case "OPERATOR":
                        hashMap.forEach((key, value) -> {
                            Log.d("key level 2", "" + key);
                            Map<String, Object> map = (Map<String, Object>) value;

                            if (!key.equals("clients")) {
                                Map<String, Object> innermap = (Map<String, Object>) map.get(key);
                                Log.d("key level 3", "" + innermap.containsKey(key) + " " + innermap.containsValue(key) + "  " + innermap.toString());

                                try {
                                    float a = Float.parseFloat(innermap.get("rating").toString());
                                    floats.add(a);
                                    if (a * 10000 > index.get() * 10000) {
                                        index.set((int) (a * 10000));
                                    }
                                    operator[1] += "\n" + key;
                                } catch (NullPointerException |
                                         NumberFormatException numberFormatException) {
                                    numberFormatException.printStackTrace();
                                    Log.e("key err", numberFormatException.getMessage());
                                }
                            } else {
                                Map<String, List<String>> lstmap = (Map<String, List<String>>) value;
                                clients.add(lstmap.get(key).size());
                                if (lstmap.get(key).size() > indexClient.get()) {
                                    indexClient.set(lstmap.get(key).size());
                                    operator[0] += "\n" + key;
                                }
                            }
                        });

                        callbackk.maxRating(floats);
                        callbackk.maxClients(clients);
                        dataFragment.setCallbackk(callbackk);
                        break;

                    case "USER":
                        break;
                }
            });

        });

    }

    private void findBy(int state) {
        switch (state) {
            case 0:
            if (!operator[0].isEmpty()) {
                String[] split = operator[0].split("\n");
                for (String op : split) {
                    server.checkDataSnapshotnew("human").thenAccept(
                            big ->
                            {
                                big.forEach((s, hashMap) ->
                                {
                                    try {
                                        if (hashMap.containsKey("position") && CryptoUtils.decrypt((String) hashMap.get("position")).equals("OPERATOR")&&s.equals(op)) {

                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(this, "WIFI FAILED CONNECTION", Toast.LENGTH_LONG).show();
                                        throw new RuntimeException(e);

                                    }
                                });
                            }
                    );

                }
            }
        }


}
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getMaxClients() {
        return maxClients;
    }

    public void setMaxClients(int maxClients) {
        this.maxClients = maxClients;
    }

    public float getMaxRating() {
        return maxRating;
    }

    public void setMaxRating(float maxRating) {
        this.maxRating = maxRating;
    }

    public String[] getOperator() {
        return operator;
    }
}

