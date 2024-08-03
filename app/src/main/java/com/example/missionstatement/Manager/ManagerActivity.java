package com.example.missionstatement.Manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.missionstatement.CallBackType.Callback_test;
import com.example.missionstatement.Firebase.Realtime;
import com.example.missionstatement.Firebase.Storage;
import com.example.missionstatement.Fragment.*;
import com.example.missionstatement.Fragment.FragmentQuest;
import com.example.missionstatement.GV.GridAdapter;
import com.example.missionstatement.Menu.Personality_Test;
import com.example.missionstatement.Objects.Human;
import com.example.missionstatement.Objects.User;
import com.example.missionstatement.R;
import com.example.missionstatement.Tools.CryptoUtils;
import com.example.missionstatement.Tools.Functions;
import com.example.missionstatement.VP.ViewPagerFragment;
import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ManagerActivity extends AppCompatActivity {
    private User manager;
    private FrameLayout frameLayout,framevp;
    private FragmentQuest fragmentQuest;
    private DataFragment dataFragment;
    private List<FragmentProfile>data;
    private Callback_test callbackTest;
    private String category;
    private Realtime server;
    private int maxClients;
    private float maxRating;
    private String[] operator;
    private boolean[] arr;
    AtomicInteger minage,maxage;
    private ProgressBar progressBar;
    private StringBuilder allData;

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
        progressBar = findViewById(R.id.progress_bar_man);

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
        framevp=findViewById(R.id.viewPagerContainer);
        data=new ArrayList<>();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.manager_fragment, fragmentQuest).commit();
        makeCallback();
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
                                .replace(R.id.manager_fragment, dataFragment)
                                .hide(fragmentQuest).commit();
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

    private void makevp()
    {
        framevp.setVisibility(View.VISIBLE);
        if(data == null || data.isEmpty())
        {
            return;
        }
        ViewPagerFragment viewPagerFragment = ViewPagerFragment.newInstance(data, 0);
        if(viewPagerFragment.getFabClose()!=null&&viewPagerFragment.getFabNext()!=null) {
            viewPagerFragment.getFabClose().setVisibility(View.GONE);
            viewPagerFragment.getFabNext().setVisibility(View.GONE);
        }

        showViewPager(0,data,viewPagerFragment);



    }
    private void showViewPager(int position, List<FragmentProfile> data,  ViewPagerFragment viewPagerFragment ) {
        String tag = "VIEW_PAGER_FRAGMENT" + position;

        ViewPagerFragment existingFragment = (ViewPagerFragment) getSupportFragmentManager().findFragmentByTag(tag);

        if (existingFragment == null || !existingFragment.isAdded()) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.viewPagerContainer, viewPagerFragment, tag) // השתמש ב-ID של ה-FrameLayout כאן
                    .addToBackStack(null)
                    .commit();
        }
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
                lst.add(new String[]{" Most common Results category ", getCategory()});
                lst.add(new String[]{" Max rating ", " operator " + getOperator()[0] + " number " + (getMaxRating())});
                lst.add(new String[]{" Max Clients ", " operator" + getOperator()[1] + " number " + (getMaxClients())});
                lst.add(new String[]{" Max/Min USER Age ", " " + maxage.get() + "/" + minage.get()});

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
                    // Show ViewPager when data is ready
                    makevp();
                } else {
                    Log.d("level tag", "" + arr[0] + arr[1] + arr[2] + getMaxClients() + getCategory());
                }
            }
        };

        // Show progress bar while loading
        //progressBar.setVisibility(View.VISIBLE);
        Set<String> category = new HashSet<>();
        Set<Float> floats = new HashSet<>();
        List<Integer> clients = new ArrayList<>();
        AtomicInteger indexClient = new AtomicInteger(0);
        AtomicInteger index = new AtomicInteger(0);
        minage = new AtomicInteger(200);
        maxage = new AtomicInteger(0);
        operator = new String[]{" ", " "};
        dataFragment.setCallbackk(callbackk);

        allData = new StringBuilder();

        server.checkDataSnapshotnew(null).thenAccept(big -> {
            big.forEach((s, hashMap) -> {
                Log.d("key level 1", "" + s);

                switch (s) {
                    case "Results":
                        for (Object i : hashMap.values()) {
                            category.add(i.toString());
                            allData.append("Results: ").append(i.toString()).append("\n");
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
                                    allData.append("Operator Rating: ").append(a).append("\n");
                                    if (a * 10000 > index.get() * 10000) {
                                        index.set((int) (a * 10000));
                                    }
                                    if (!operator[1].isEmpty()) {
                                        operator[1] = "";
                                    }
                                    operator[1] += "\n" + key;
                                    FragmentProfile fragmentProfile2 = new FragmentProfile();
                                    fragmentProfile2.setDeatils(Functions.fromOperatorMap(innermap).OperatorStringMap());
                                    fragmentProfile2.putOperatorWithHash(fragmentProfile2.getDeatils());
                                    data.add(fragmentProfile2);
                                } catch (NullPointerException | NumberFormatException numberFormatException) {
                                    numberFormatException.printStackTrace();
                                    Log.e("key err", numberFormatException.getMessage());
                                }
                            } else {
                                Map<String, List<String>> lstmap = (Map<String, List<String>>) value;
                                clients.add(lstmap.get(key).size());
                                allData.append("Clients: ").append(lstmap.get(key).size()).append("\n");
                                if (lstmap.get(key).size() > indexClient.get()) {
                                    indexClient.set(lstmap.get(key).size());
                                    if (!operator[0].isEmpty()) {
                                        operator[0] = "";
                                    }
                                    operator[0] += "\n" + key;
                                }
                            }
                        });

                        callbackk.maxRating(floats);
                        callbackk.maxClients(clients);

                        break;

                    case "USER":
                        big.forEach((key, value) -> {
                            try {
                                int a = Functions.calculateAge(value.get("birthdate").toString());
                                if (a < minage.get()) {
                                    minage.set(a);
                                }
                                if (a > maxage.get()) {
                                    maxage.set(a);
                                }
                                allData.append("User Age: ").append(a).append("\n");
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        break;

                    case "human":
                        HashMap<String, String> dec = CryptoUtils.decryptHuman((HashMap<String, String>) Functions.convertToMapOfStrings(hashMap));
                        if ("USER".equals(dec.get("position"))) {
                            FragmentProfile f = new FragmentProfile();
                            f.setDeatils(dec);
                            f.putDeatilsWithHash(f.getDeatils());
                            data.add(f);
                            allData.append("USER: ").append(dec.toString()).append("\n");
                        }
                        break;
                }
            });
        });
    }

    private static final int REQUEST_CODE = 1;



    public void exportToTextFile(String data, Context context) {
        try {
            byte[] bytes = data.getBytes();
            InputStream stream = new ByteArrayInputStream(bytes);

            Storage.getInstance().getStorageReference().child("output" + new Date().toString() + ".txt").putStream(stream)
                    .addOnSuccessListener(taskSnapshot -> Toast.makeText(context, "File uploaded successfully", Toast.LENGTH_LONG).show())
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        Toast.makeText(context, "Error uploading file: " + e.getMessage() + " c " + e.getCause(), Toast.LENGTH_LONG).show();
                    });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error: " + e.getMessage() + " c " + e.getCause(), Toast.LENGTH_LONG).show();
        }
    }

    static int clickframevp=0;
    @Override
    protected void onResume() {
        super.onResume();

        MaterialButton materialButton=findViewById(R.id.BTN_man);
        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(allData!=null) {
                    exportToTextFile(allData.toString(), ManagerActivity.this);

                }
            }
        });
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

