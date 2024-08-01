package com.example.missionstatement.Menu;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.missionstatement.AREA;
import com.example.missionstatement.CallBackType.Callback_list;
import com.example.missionstatement.Category;
import com.example.missionstatement.Firebase.Realtime;
import com.example.missionstatement.Firebase.Storage;
import com.example.missionstatement.Fragment.FragmentProfile;
import com.example.missionstatement.Objects.Operator;
import com.example.missionstatement.R;
import com.example.missionstatement.Tools.CryptoUtils;
import com.example.missionstatement.Tools.Functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ListOperator extends AppCompatActivity {
    private Operator operator;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private AtomicInteger index;
    private Realtime server;
    private Set<String> emailsAdded;
    int[] id = {R.id.menu_item1, R.id.menu_item2, R.id.menu_item3, R.id.menu_item4, R.id.menu_item5};
    private List<FragmentProfile> lst;
    int[] Operatoriconn = {R.drawable.ic_locat, R.drawable.ic_heartt, R.drawable.ic_validate, R.drawable.ic_google, R.drawable.ic_smiley, R.drawable.ic_operatorseven};
    String[] op = {"results", "area", "rating", "category", "email"};

    int[] Operatoricon = {R.drawable.ic_operatorone, R.drawable.ic_operatortwo, R.drawable.ic_operatorthree, R.drawable.ic_operatorfour, R.drawable.ic_operatorsix, R.drawable.ic_operatorseven};
    private String filter = "";
    private List<Map<String, String>> mapList;
    private Set<String> setCategory;
    private Callback_list callbackList;
    private boolean fillHash = false;
    private String userId;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_operator);
        lst = new ArrayList<>();
        this.mapList = new ArrayList<>();
        index=new AtomicInteger(0);
        server = new Realtime(this);
        recyclerView = findViewById(R.id.lst_operator);
        searchView = findViewById(R.id.searchView_LST);
        setCategory = new HashSet<>();
        emailsAdded = new HashSet<>();
        // Set up SearchView
        setMapList(new ArrayList<>());

        try {
            userId = getIntent().getStringExtra("ph");
        } catch (NullPointerException nullPointerException) {
            nullPointerException.printStackTrace();
        }

        setCallback();
        joinTables(server, callbackList);

    }

    public MyAdapter getAdapter() {
        return adapter;
    }

    private void setSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                //   performSearch(query);

                return true;
            }
        });

        // Show popup menu when the SearchView is clicked
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }

    private  void setCallback()
    {
        callbackList = new Callback_list() {
            @Override
            public void addToList(FragmentProfile f) {
                //f.setVisibility(View.VISIBLE);
                lst.add(f);
            }

            @Override
            public void start() {

                ListOperator.this.fillHash = true;
                adapter = new MyAdapter(getLst(), getSupportFragmentManager());
                recyclerView.setLayoutManager(new LinearLayoutManager(ListOperator.this));
                recyclerView.setAdapter(adapter);


            }

            @Override
            public void initOperatorr(HashMap<String, Object> map,boolean b) {

                FragmentProfile fragmentProfile=new FragmentProfile();
                if(b) {
                    fragmentProfile.setDeatils(Functions.fromOperatorMap(map).OperatorStringMap());

                }
                else {
                    initOpeartor(map.get("email").toString(),map.get("password").toString());
                    fragmentProfile.setDeatils(operator.OperatorStringMap());
                }
                Log.d("mapOperator",fragmentProfile.getDeatils().toString());
                getLst().add(fragmentProfile);

            }



            @Override
            public void FilterBy(String criteria,String value) {
                if(getLst()==null){
                    return;
                }
                    if (criteria == null) {
                    } else {
                        getAdapter().updateData( getLst().stream()
                                .filter(profile -> {
                                    boolean matches = Functions.filterBy(criteria, value, profile);
                                    Log.d("filter", "Profile: " + profile.getDeatils() + ", Matches: " + matches);
                                    return matches;
                                })
                                .collect(Collectors.toList()));

                }  }

            @Override
            public void addHashList(HashMap<String, String> map, List<Map<String, String>> lst) {
                lst.add(map);

            }

            @Override
            public void cleanup(List<FragmentProfile> list) {
                for (FragmentProfile f : list) {
                    f.setAdded(false);
                }
            }

            @Override
            public Operator containsData(String ph, String email) {
                return null;
            }

            @Override
            public void setFilter(String filter) {
                ListOperator.this.filter=(filter);
            }


        };
    }
    private void performSearch(String query) {
        if(callbackList!=null)
        {
            callbackList.FilterBy(getFilter(),query);
        }
        Toast.makeText(this, "Searching for: " + query, Toast.LENGTH_SHORT).show();
    }

    public void setLst(List<FragmentProfile> lst) {
        this.lst = lst;
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();

        inflater.inflate(R.menu.popu_menu, popup.getMenu());
        //int[] Operatoricon = {R.drawable.ic_locat, R.drawable.ic_heartt, R.drawable.ic_validate, R.drawable.ic_google, R.drawable.ic_smiley, R.drawable.ic_operatorseven};
        for (int i = 0; i < id.length; i++) {
            popup.getMenu().findItem(id[i]).setTitle(op[i]);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                boolean ok = false;
                getIconAction(id, item, op);

                return true;
            }
        });
        popup.show();
    }

    private void getIconAction(int[] id, MenuItem item, String[] op) {

        for (int i = 0; i < id.length; i++) {
            if (id[i] == item.getItemId()) {

                index.set(i);
                //  this.filter = op[i];
                if(callbackList!=null) {
                    callbackList.setFilter(op[i]);
                }
            }
        }

    }

    public String getFilter() {
        return filter;
    }

    private void joinTables(Realtime server, Callback_list init) {
        if(!this.fillHash)
        {
            server.checkDataSnapshotnew(null).thenAccept(big ->
            {
                big.forEach((subject, value) ->
                {
                    switch (subject)
                    {
                        case "human":
                            value.forEach((ph, humanmap) ->
                            {
                                HashMap<String, String> humanmap1 = (HashMap<String, String>) humanmap;
                                try {
                                    if(CryptoUtils.decrypt(humanmap1.get("position")).equals("OPERATOR")) {
                                        callbackList.initOperatorr((HashMap<String, Object>) ((HashMap<String, Object>) big.get("OPERATOR").get(ph)).get(ph), big.get("OPERATOR").containsKey(ph));
                                    }
                                }catch (Exception nullPointerException)
                                {
                                    nullPointerException.printStackTrace();
                                }
                            });


                    }
                });
                callbackList.start();

            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FilterBy();
    }

    private  void FilterBy()
    {
        if(callbackList!=null)
        {
            setSearchView();

        }
    }


    public Category getRandomCategory() {
        Category[] categories = Category.values();
        Random random = new Random();
        int index = random.nextInt(categories.length);
        return categories[index];
    }

    public float getRandomFloat() {
        Random random = new Random();
        return random.nextFloat() * 5;
    }

    private void initOpeartor(String email, String ph) {

        Log.d("keye",""+emailsAdded.contains(email));

        if (!emailsAdded.contains(email))
            switch (email.replace(" ", "")) {
                case "mirham@afeka.ac.il":
                    operator = new Operator(Category.ENGINEER, AREA.TLV, " COLLEGE OF ENGINEERING  B.SC(SW,MD,MACHINE,ELECTRICITY ,INDUSTRIAL AND MORE)", 3.7f, getRandomIcon());
                    break;
                case "international@mta.ac.il":
                    operator = new Operator(Category.ENGINEER, AREA.TLV, " the college offers a variety of undergraduate and graduate programs in a variety of fields, including business, computer science, social work, and education.", 0.4f, getRandomIcon());
                    break;
                default:
                    operator = new Operator(getRandomCategory(), getRandomArea(), " i am professtiona at education", getRandomFloat(), getRandomIcon());
                    break;
            }
        operator.setEmail(email);
        operator.setPhoneNumber(ph);
        server.getmDatabase().child("OPERATOR").child(ph).setValue(operator.OperatorMap());
        emailsAdded.add(email);

    }



    public AREA getRandomArea() {
        AREA[] areas = AREA.values();
        Random random = new Random();
        int index = random.nextInt(areas.length);
        return areas[index];
    }
    public int getRandomIcon() {
        Random random = new Random();
        int index = random.nextInt(Operatoricon.length);
        return Operatoricon[index];
    }

    public List<FragmentProfile> getLst() {
        return lst;
    }

    public List<Map<String, String>> getMapList() {
        return mapList;
    }

    public void setMapList(List<Map<String, String>> mapList) {
        this.mapList = mapList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
