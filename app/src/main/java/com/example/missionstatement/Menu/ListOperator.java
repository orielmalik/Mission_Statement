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
private  String userId;
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
try {
  userId=  getIntent().getStringExtra("ph");
}catch (NullPointerException nullPointerException)
{
    nullPointerException.printStackTrace();
}

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



    private void performSearch(String query) {

        callbackList = new Callback_list() {
            @Override
            public void addToList(FragmentProfile f) {
                if((!f.isAddedd()&&!getLst().contains(f))&&Functions.filterBy(filter,query,f.getDeatils(),searchView))
                {
                    lst.add(f);
                    f.setAdded(true);
                    Functions.orderList(getLst(),filter);
                }
            }

            @Override
            public void start() {
                for (Map<String,String>map:getMapList()) {
                    if(! ListOperator.this.fillHash) {
                        try {
                            initOpeartor((String) CryptoUtils.decrypt(map.get("email")), (String) CryptoUtils.decrypt(map.get("PhoneNumber")));
                        } catch (Exception e) {
                            Log.e("err", e.getMessage());
                        }
                    }
                    FragmentProfile frag = new FragmentProfile();

                    frag.setDeatils(operator.OperatorStringMap());
                    ListOperator.this.setCategory = frag.getDeatils().keySet();

                      //  getLst().add(frag);//adhashmapd to j
                   // frag.setAdded(true);
                    frag.getDeatils().put("user",getUserId());
                    this.addToList(frag);
                    ListOperator.this.fillHash=true;

                }
                MyAdapter adapter = new MyAdapter(getLst(), getSupportFragmentManager());
                recyclerView.setLayoutManager(new LinearLayoutManager(ListOperator.this));
                recyclerView.setAdapter(adapter);
                for(FragmentProfile fragmentProfile: getLst())
                {
                    fragmentProfile.setAdded(false);
                }
                setLst(new ArrayList<>());

            }

            @Override
            public void addHashList(HashMap<String, String> map, List<Map<String, String>> lst) {
                lst.add(map);

            }

            @Override
            public void cleanup(List<FragmentProfile> list) {
                for(FragmentProfile f:list)
                {
                    f.setAdded(false);
                }
            }

            @Override
            public Operator containsData(String ph, String email) {
                return null;
            }


        };
        callbackList.cleanup(getLst());
        joinTables(server, callbackList);

        Toast.makeText(this, "Searching for: " + query, Toast.LENGTH_SHORT).show();
    }

    public void setLst(List<FragmentProfile> lst) {
        this.lst = lst;
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();

        inflater.inflate(R.menu.popu_menu, popup.getMenu());
        String[]op={"results","area","rating","category","email"};
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
                setLst(new ArrayList<>());
                this.filter=op[i];
            }
        }

    }

    private  boolean uploadOperatorsOnce(String email)
    {
        AtomicBoolean bool=new AtomicBoolean(false);
        server.checkDataSnapshotnew("OPERATOR").thenAccept(big -> {

            big.forEach((s, hashMap) -> {
                if(email.equals(operator.getEmail()))
                {
                    bool.set(true);
                }
            });
        });


        return bool.get();
    }



    private  void joinTables(Realtime server, Callback_list init)
    {
        if(!isSetData()) {

            server.checkDataSnapshot("human").thenAccept(big ->
            {
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

            });
        }
        init.start();

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
        if(!uploadOperatorsOnce(email.replace(" ",""))) {

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
        }
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
