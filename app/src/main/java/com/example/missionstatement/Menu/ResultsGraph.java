package com.example.missionstatement.Menu;

import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.missionstatement.CallBackType.Callback_results;
import com.example.missionstatement.Firebase.Realtime;
import com.example.missionstatement.Objects.Test;
import com.example.missionstatement.Objects.User;
import com.example.missionstatement.R;
import com.example.missionstatement.Tools.DecisionMaker;
import com.example.missionstatement.Tools.Functions;
import com.example.missionstatement.paints.DinicGraph;
import com.example.missionstatement.paints.Graph;
import com.google.android.material.textview.MaterialTextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ResultsGraph extends AppCompatActivity {
    private User user;
    private String field;
    private HashMap<String, Object> usermap;
    private Realtime server;
    private List<TableRow> arr;
    private TableLayout tableLayout;
    private DinicGraph graph;
    private DecisionMaker decisionMaker;
    private List<Test> tests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_graph);
        initTable();
        user = new User();
        server = new Realtime(this);
        try {
            field = getIntent().getStringExtra("user");
            Toast.makeText(this, field, Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void initServer(String ph, Callback_results callback) {
        server.checkDataSnapshotnew("USER").thenAccept(hashMap -> {
            hashMap.forEach((s, map) -> {
                if (s.equals(ph)) {
                    this.usermap = map;
                        callback.initmap(map);
                    }

                });
        }).exceptionally(throwable -> {
            // Log any exception that might occur
            Log.e("tagaa", "Error processing data", throwable);
            return null;
        });
    }

    private  void changeText(Integer[] score) {


    }
    private void changeScore(Graph graph)
    {
        int age=0;
        try {
            age= Functions.calculateAge((String) this.usermap.get("birthdate").toString());
        }catch (ParseException parseException)
        {
            Toast.makeText(this, parseException.getMessage()+" at\n"+this.usermap.get("birthdate").toString(), Toast.LENGTH_SHORT).show();
            return;
        }
        List<Integer>results=new ArrayList<>();

        for (int i = 10; i <14 ; i++) {
            results.add(graph.fordFulkerson(graph.findNodeBylabel(graph, Age(age)).getId(),i ).getMaxFlow());
        }
        for (int i = 1; i <tableLayout.getChildCount(); i++) {
            ((MaterialTextView) ((TableRow) tableLayout.getChildAt(i)).getChildAt(1)).setText(results.get(i-1).toString());


        }
    }

    public String Age(int age)
    {
        if(age<20)
            return "Age<20";
        else if(age>35)
            return  "Age>35";
        else
            return "20<Age<35";

    }




    private void getTests(HashMap<String,Object>usermap)
    {

        AtomicInteger check= new AtomicInteger();
        check.set(0);
        usermap.forEach((s, o) ->
        {
            if(s.equals("tests"))
            {
                List<Map<String, Object>> testsMap;
                tests = new ArrayList<>();
                try {
                    testsMap = (List<Map<String, Object>>) usermap.get("tests");
                } catch (ClassCastException e) {
                    Log.d("calcTests", "ClassCastException: " + e.getMessage());
                    check.set(1);
                    return  ;
                }
                if (testsMap == null) {
                    Log.d("calcTests", "testsMap is null");
                    return ;
                }

                 helper(testsMap);


            }});
            }
            private  void  helper(List<Map<String, Object>> testsMap)
            {
                List<Integer>score=new ArrayList<>();
                if(testsMap.size()>0) {

                    testsMap.forEach(stringObjectMap -> {
                        Log.d("map", "" + stringObjectMap);
                        if (stringObjectMap != null) {
                            stringObjectMap.forEach((key, value) ->{
                                Map<String, Object> v = (Map<String, Object>) value;

                                Test t = new Test();
                                t.setDone(true);
                                try {
                                    t.setResults((List<Integer>) Functions.convertLongListToIntList((List<?>) (v.get("results"))));
                                    score.addAll(t.getResults());
                                    t.setPointsPerAnswer((List<Integer>) Functions.convertLongListToIntList((List<?>) (List<Integer>) Functions.convertLongListToIntList((List<?>) (v.get("pointsPerAnswer")))));
                                }catch (NullPointerException numberFormatException)
                                {
                                    numberFormatException.printStackTrace();
                                    Toast.makeText(ResultsGraph.this, numberFormatException.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                if(t.getResults()!=null&&t.getPointsPerAnswer()!=null) {
                                    Log.d("map",""+t.toMap().values());
                                    tests.add(t);
                                }
                            });
                        }
                    });
                }

            }










    private void  initTable()
    {tableLayout=findViewById(R.id.TBL_main);
        arr=new ArrayList<>();
        arr.add(findViewById(R.id.TBL_titles));

    }

    @Override
    protected void onResume() {
        super.onResume();
        initServer(field, new Callback_results() {
            @Override
            public void initmap(HashMap<String,Object> usermap) {
                decisionMaker = new DecisionMaker(usermap);
                getTests(usermap);
                decisionMaker.setTests(tests);
                if(tests!=null&&!tests.isEmpty())
                {
                 // ResultsGraph.this.graph=decisionMaker.getDGraph();
graph(usermap,decisionMaker.getDGraph());





                }else {
                    Toast.makeText(ResultsGraph.this, "error with grpah", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void graph(Map<String,Object> usermap,DinicGraph graph)
    {
        if(graph==null)
        {
            return;
        }


        Log.d("map",""+graph.getNodeById(1).getId()+" label "+graph.getNodeById(1).getLabel());
        Log.d("map",""+graph.getNodeById(graph.getV()-1).getLabel()+" id"+graph.getNodeById(graph.getV()-1).getId());
        // Log.d("map",""+graph.dinicMaxFlow(1,12).getMaxFlow());
        int id=switchcaseAge(usermap);

        if(id>0) {
            ((MaterialTextView) ((TableRow) ResultsGraph.this.tableLayout.getChildAt(1)).getChildAt(1)).setText("" + graph.dinicMaxFlow(id, graph.getV()-1).getMaxFlow());
        }
      //  ((MaterialTextView) ((TableRow) ResultsGraph.this.tableLayout.getChildAt(1)).getChildAt(2)).setText(""+graph.dinicMaxFlow(1, graph.getV()-1).getMaxFlow());
        graph.dinicMaxFlow(id, graph.getV()-1).getPath()
                .forEach(integer ->
                {
                    Log.d("map","id: "+integer.intValue());

    });

    }







private  int  switchcaseAge(Map<String,Object>map)
{
    int id=-1;
    try {
        id=Functions.calculateAge(map.get("birthdate").toString());
    } catch (ParseException e) {
        e.printStackTrace();
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        return -2;
    }
    if(id<20)
    {
        return 2;
    }
    else if(id>35)
    {
        return  4;
    }
    else
    {
        return 3;
    }
}
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
