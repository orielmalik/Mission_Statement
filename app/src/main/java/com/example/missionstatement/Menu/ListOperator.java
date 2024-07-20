package com.example.missionstatement.Menu;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ListOperator extends AppCompatActivity {
    private Operator operator;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private  Realtime server;
    private Storage storage;
    int[]id={R.id.menu_item1,R.id.menu_item2,R.id.menu_item3,R.id.menu_item4,R.id.menu_item5};
    private List<FragmentProfile> lst;
    int[]Operatoricon={R.drawable.ic_operatorone,R.drawable.ic_operatortwo,R.drawable.ic_operatorthree,R.drawable.ic_operatorfour,R.drawable.ic_operatorsix,R.drawable.ic_operatorseven};
    private String filter;
    private List<Map<String,String>>mapList;
    private Set<String> setCategory;
    private Callback_list callbackList;
    private boolean fillHash=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_operator);
        lst=new ArrayList<>();
        this.mapList=new ArrayList<>();
        server=new Realtime(this);
        recyclerView = findViewById(R.id.lst_operator);
        searchView = findViewById(R.id.searchView_LST);
        setCategory=new HashSet<>();
        // Set up SearchView
        setSearchView();



    }
    private  void  setSearchView()
    {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                performSearch(query);

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



    private void performSearch(String query) {

        callbackList = new Callback_list() {
            @Override
            public void addToList(FragmentProfile f) {
                if(f.getDeatils().get(ListOperator.this.filter).equalsIgnoreCase(query.replace(" ",""))||additionalCondition( query,f))
                {
                    lst.add(f);

                }
            }

            @Override
            public void start() {
                ListOperator.this.fillHash=true;
                for (Map<String,String>map:getMapList()) {
                    try {
                        initOpeartor((String) CryptoUtils.decrypt(map.get("email")), (String) CryptoUtils.decrypt(map.get("PhoneNumber")));

                    } catch (Exception e) {
                        Log.e("err", e.getMessage());
                    }
                    FragmentProfile frag = new FragmentProfile();

                    frag.setDeatils(operator.OperatorStringMap());
                    ListOperator.this.setCategory = frag.getDeatils().keySet();
                    //getLst().add(frag);//adhashmapd to j
                    this.addToList(frag);
                    Log.d("sta", "" + getLst().get(0).getDeatils().get("email"));

                }
                MyAdapter adapter = new MyAdapter(getLst(), getSupportFragmentManager());

                recyclerView.setLayoutManager(new LinearLayoutManager(ListOperator.this));
                recyclerView.setAdapter(adapter);
                ListOperator.this.lst=new ArrayList<>();
            }

            @Override
            public void addHashList(HashMap<String, String> map, List<Map<String, String>> lst) {
                lst.add(map);

            }


        };
        joinTables(server, callbackList);

        Toast.makeText(this, "Searching for: " + query, Toast.LENGTH_SHORT).show();
    }

    private boolean additionalCondition(String query,FragmentProfile f) {
        int v;
        float t;
        if(filter.equals("rating"))
        {
            try {
                 v=Integer.parseInt(query);
                 t=Float.parseFloat(f.getDeatils().get(filter));
            }catch (NumberFormatException numberFormatException)
            {
                numberFormatException.printStackTrace();
                Toast.makeText(this,"NOT NUMBER",Toast.LENGTH_SHORT);
                return  false;
            }
           return  (int) t>=v;
        }
        return  false;
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();

        inflater.inflate(R.menu.popu_menu, popup.getMenu());
        String[]op={"area","rating","category","email","clients"};
        int[]Operatoricon={R.drawable.ic_locat,R.drawable.ic_heartt,R.drawable.ic_validate,R.drawable.ic_google,R.drawable.ic_smiley,R.drawable.ic_operatorseven};
        for (int i = 0; i <id.length ; i++) {
            popup.getMenu().findItem(id[i]).setIcon(Operatoricon[i]);
            popup.getMenu().findItem(id[i]).setTitle(op[i]);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                boolean ok=false;
                getIconAction(id,item,op);


                return  true;
            }
        });
        popup.show();
    }
    private void getIconAction(int[] id, MenuItem item,String[]op) {

        for (int i = 0; i < id.length; i++) {
            if(id[i]==item.getItemId())
            {
//callbackList.addCategory(op[i]);
                this.filter=op[i];
            }
        }

    }





    private  void joinTables(Realtime server, Callback_list init)
    {
        server.checkDataSnapshot("human").thenAccept(big ->
        {
            if(!isSetData()) {
                big.forEach((s, hashMap) ->
                {
                    try {
                        if (hashMap.containsKey("position") && CryptoUtils.decrypt((String) hashMap.get("position")).equals("OPERATOR")) {
                            init.addHashList(hashMap, getMapList());

                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "WIFI FAILED CONNECTION", Toast.LENGTH_LONG).show();
                        throw new RuntimeException(e);

                    }

                });
            }
            init.start();

        });
    }

    private boolean isSetData() {
        return  this.fillHash;
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
    private  void initOpeartor(String email,String ph)
    {
        operator=new Operator(getRandomCategory(),getRandomArea()," i am professtiona at education",getRandomFloat(),getRandomIcon());
        operator.setEmail(email);
        operator.setPhoneNumber(ph);
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
}
