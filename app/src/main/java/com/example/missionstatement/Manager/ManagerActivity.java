package com.example.missionstatement.Manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
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
import com.example.missionstatement.Tools.Functions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ManagerActivity extends AppCompatActivity {
    private User manager;
    private FrameLayout frameLayout;
    private FragmentQuest fragmentQuest;
    private DataFragment dataFragment;
    private Callback_test callbackTest;
    private String category;
    private Realtime server;
private int maxClients;
private  float maxRating;
    private String[] operator;


    public interface Callbackk
    {
        void addCategory(Set<String>set);
        void maxRating(Set<Float>set);
        void  maxClients(List<Integer>clientsize);
        void  writeWithAtomic(AtomicInteger integer1,AtomicInteger integer2,AtomicInteger integer3);
        void gridVieFill(GridView gridView, Context context);
    }
    Callbackk callbackk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager);
        try {
            manager=(User) getIntent().getSerializableExtra("user");

        }catch (NullPointerException n)
        {
            n.printStackTrace();
            Log.e("nullpoiner", "onCreate: "+n.getMessage());
            Toast.makeText(this, "NO MANAGER", Toast.LENGTH_SHORT).show();
        }
        server=new Realtime(this);
        fragmentQuest=new FragmentQuest();
        dataFragment=new DataFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.manager_fragment,fragmentQuest).commit();
        makeCallback();

    }
    //here we dont need all methods'we only do actions with radioGroup
    private void makeCallback()
    {
        callbackTest=new Callback_test() {
            @Override
            public void uploadAnswers(RadioGroup radioGroup) {
                makeOptions(radioGroup);
            }

            @Override
            public void getResult(int index) {

            }

            @Override
            public boolean getCounterQuestion(int counter) {
                return false;
            }

            @Override
            public void setAnswer(String string, int i) {
                switch (i)
                {
                    case 0:
                        string="DEVELOP TEST";
                        break;
                }
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
                switch (i)
                {
                    case 0:
                        Intent ad=new Intent(this, Personality_Test.class);
                        ad.putExtra("user",manager);
                        startActivity(ad);
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.manager_fragment,dataFragment).commit();
                        break;
                }
                radioGroup.setVisibility(View.GONE);
            }
        }

    }



    //find the most
    private void analyze()
    {
        callbackk=new Callbackk() {
            @Override
            public void addCategory(Set<String> set) {
                setCategory( Functions.findMostFrequent(set));
            }

            @Override
            public void maxRating(Set<Float> set) {
                setMaxRating(Functions.findMaxInSet(set));
            }

            @Override
            public void maxClients(List<Integer> clientsize) {
            }

            @Override
            public void writeWithAtomic(AtomicInteger integer1, AtomicInteger integer2, AtomicInteger integer3) {

            }

            @Override
            public void gridVieFill(GridView gridView,Context context) {
                List<String[]>data=new ArrayList<>();
                // TODO ADD DATA
                data.add(  new String[]{"Most Clients",getOperator()[0]+" "+getMaxClients()});
                data.add(  new String[]{"Most Rating",getOperator()[1]+" "+getMaxClients()});
                data.add(  new String[]{"Most Category",getCategory()});


                GridAdapter adapter = new GridAdapter(context, data);
                gridView.setAdapter(adapter);
                // Set item click listener


                gridView.setOnItemClickListener((parent, view1, position, id) -> {
                    String item = (String) adapter.getItem(position);
                    Toast.makeText(context, "Item clicked: " + item, Toast.LENGTH_SHORT).show();
                });
            }
        };
        Set<String> category = new HashSet<>();
        Set<Float>floats=new HashSet<>();
        List<Integer>clients=new ArrayList<>();
        AtomicInteger indexClient=new AtomicInteger(0);
        AtomicInteger index=new AtomicInteger(0);
      operator=new String[2];
        server.checkDataSnapshotnew(null).thenAccept(big ->
        {
            big.forEach((s, hashMap) ->
            {
                switch (s) {
                    case "Results":
                        for (Object i : hashMap.values()) {
                            category.add(i.toString());
                        }
                        callbackk.addCategory(category);
                        break;
                    case "OPERATOR":
                        hashMap.forEach((key, value) ->
                        {
                            if(!key.equals("clients"))
                            {
                                Map<String,Object>map= (Map<String, Object>) value;
                                try {
                                    float a= Float.parseFloat(map.get("rating").toString());
                                    floats.add(a);
                                    if(a*10000>index.get()*10000)
                                    {
                                        index.set((int) (a*10000));
                                    }
                                    operator[1]=key;
                                }
                                catch (NumberFormatException numberFormatException)
                                {
                                    numberFormatException.printStackTrace();
                                }
                            }
                            else {
                                Map<String, List<String>>map= (Map<String, List<String>>) value;
                                clients.add(new Integer(map.get(key).size()));
                                if(map.get(key).size()>indexClient.get())
                                {
                                    indexClient.set(map.get(key).size());
                                    operator[0]=(key);
                                }
                            }
                        });
                        callbackk.maxRating(floats);
                        callbackk.maxClients(clients);
                }
            });
        });


        dataFragment.setCallbackk(callbackk);
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
