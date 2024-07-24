package com.example.missionstatement.Tools;

import android.util.Log;

import com.example.missionstatement.Objects.Test;
import com.example.missionstatement.Objects.User;
import com.example.missionstatement.paints.DinicGraph;
import com.example.missionstatement.paints.Graph;
import com.example.missionstatement.paints.Node;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DecisionMaker {

    int[] categories;
    private User user;
    private HashMap<String, Object> usermap;
    private String[] Edu = {"ECONOMIC", "ENGINEER", "MEDICAL", "EDUCATION"};
    private int locationpoints;
    private int sizeTests;
    private List<Map<String, Object>> testsMap;
    private List<Test> tests;
    private Node saver;
    private  List<Node>path;
    private DinicGraph graph;
    private int totalos;
    private int[] sum;
    private List<int[]>sumList;
    private List<Node> pro;
    private List<Node> saverList;

    public DecisionMaker(HashMap<String, Object> usermap) {
        categories = new int[4];
        this.usermap = usermap;
        totalos=0;

    }



    private  void calcCategory(String category, int sum)
    {

        //what the user chose
                    /* 7 כלכלה
                    12 רפואה
                    10 מהנדס
                    4 חינוך*/

        switch (category.toUpperCase())
        {
            case "ECONOMIC":
                sum+=7;
                break; case "MEDICAL":
            sum+=12;
            break; case "ENGINEER":
            sum+=10;
            break; case "EDUCATION":
            sum+=4;
            break;
        }
    }





    public DinicGraph getDGraph() {
        //String category =test.contains
        sum=new int[4];
        sumList=new ArrayList<>();
        String location = this.usermap.get("location").toString().toUpperCase();
        locationpoints = calcLocation(location);
        int v = 10;
        DinicGraph graph = new DinicGraph(v + tests.size()+2);graph.addNode(new Node(0,"none"));
        Node start = new Node(1, "start");
        Node agemin = new Node(2, "age<20");
        Node agemid = new Node(3, "20<age<35");
        Node agemax = new Node(4, "age>35");
        graph.addNode(start);
        graph.addNode(agemin);
        graph.addNode(agemid);
        graph.addNode(agemax);
        Node source = new Node(graph.getV()-1, "source");
        graph.addNode(source);

        graph.addEdge(start, agemin, locationpoints+14);
        graph.addEdge(start, agemax, locationpoints+18);
        graph.addEdge(start, agemid, locationpoints+10);

        Node center = new Node(5, "center");
        graph.addNode(center);

        // Every node gets its agePoints
        graph.addEdge(agemin, center, 1);
        graph.addEdge(agemax, center, 1);
        graph.addEdge(agemid, center, 1);

        List<Integer> score = new ArrayList<>();
        pro = new ArrayList<>();
        pro.add(new Node(10-4, this.Edu[0]));
        pro.add(new Node(11-4, this.Edu[1]));
        pro.add(new Node(12-4, this.Edu[2]));
        pro.add(new Node(13-4, this.Edu[3]));

        AtomicInteger i = new AtomicInteger(1);
        int id=10 +i.getAndIncrement()-1;


        Node tNode = new Node(id, "test" +(id-10));
        graph.addNode(tNode);
        graph.addEdge(center,tNode,1);

        if (tests != null||!tests.isEmpty()) {
            for (Test test : tests) {
                if (test == null) {
                    continue;
                }
                score.addAll(test.getResults());
                int frequentNumber = Functions.findMostFrequentNumber(score);
                sum[frequentNumber % Edu.length] += 20;
                saver = tNode; // The next test
                id=10 + i.getAndIncrement()-1;
                tNode=new Node(id, "test" + (id-10));
                graph.addNode(tNode);

                if (saver != null) {
                    graph.addEdge(saver, tNode, 4); // Add edge between every test
                }

                for (Node node : pro) {
                    if (node != null) {
                        graph.addNode(node);
                        graph.addEdge(node,source,1);
                        if (node != null && test.getResults() != null) {
                            for (Integer integer : test.getResults()) {
                                sum[integer.intValue() % Edu.length] += test.getPointsPerAnswer().get(integer.intValue() % Edu.length);
                                if(test.getCategory()!=null&&integer.intValue()!=Functions.indexCategory(test.getCategory()))//if we have specific category BY TEST AUTHOR so minus score or not
                                {
                                    sum[Functions.indexCategory(test.getCategory()) % Edu.length] -= test.getPointsPerAnswer().get(Functions.indexCategory(test.getCategory()) % Edu.length)/2;
                                }
                            }for (Integer integer : test.getResults()) {
                                graph.addEdge(tNode,node,sum[integer.intValue()]);
                            }
                            Log.d("map", Arrays.toString(sum));
                        }
                    }
                }
                score=new ArrayList<>();
                sum=new int[]{0,0,0,0};

            }
        }

        return graph;
    }

    private  void addToSumList()
    {
        saverList.forEach(node -> {
            for (int i = 0; i < sumList.size() ; i++) {
                graph.addEdge(node,graph.getNodeById(10+i),sum[i]);

            }

        });

    }
    private int calcLocation(String location) {
        if(location.contains("ISRAEL"))//i dont care if some street at OTHER COUNTRY     with this name
        {
            return 10;
        }
        else {
            return  5;
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DinicGraph getGraph() {
        return graph;
    }

    public void setGraph(DinicGraph graph) {
        this.graph = graph;
    }

    public List<Test> getTests() {
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
    }

    public void setTotalos(int totalos) {
        this.totalos = totalos;
    }
}
