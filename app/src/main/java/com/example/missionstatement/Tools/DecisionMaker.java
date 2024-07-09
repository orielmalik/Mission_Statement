package com.example.missionstatement.Tools;

import com.example.missionstatement.Objects.Test;
import com.example.missionstatement.Objects.User;
import com.example.missionstatement.paints.DinicGraph;
import com.example.missionstatement.paints.Graph;
import com.example.missionstatement.paints.Node;

import java.util.ArrayList;
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
    private  List<Node>path;
    private DinicGraph graph;
    private int totalos;
    private int[] sum;

    public DecisionMaker(HashMap<String, Object> usermap) {
        categories = new int[4];
        this.usermap = usermap;
        totalos=0;
        sum=new int[4];
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
        String location = this.usermap.get("location").toString().toUpperCase();
        locationpoints = calcLocation(location);
        int v = 32;
        DinicGraph graph = new DinicGraph(v + tests.size());
        Node start = new Node(1, "start");
        Node agemin = new Node(2, "age<20");
        Node agemid = new Node(3, "20<age<35");
        Node agemax = new Node(4, "age>35");
        graph.addNode(start);
        graph.addNode(agemin);
        graph.addNode(agemid);
        graph.addNode(agemax);
        Node source = new Node(v + tests.size() + 10, "source");
        graph.addNode(source);

        graph.addEdge(start, agemin, locationpoints);
        graph.addEdge(start, agemax, locationpoints);
        graph.addEdge(start, agemid, locationpoints);

        Node center = new Node(5, "center");
        // Every node gets its agePoints
        graph.addEdge(agemin, center, 14);
        graph.addEdge(agemax, center, 18);
        graph.addEdge(agemid, center, 10);

        List<Integer> score = new ArrayList<>();
        List<Node> pro = new ArrayList<>();
        pro.add(new Node(10, this.Edu[0]));
        pro.add(new Node(11, this.Edu[1]));
        pro.add(new Node(12, this.Edu[2]));
        pro.add(new Node(13, this.Edu[3]));

        AtomicInteger i = new AtomicInteger(1);
        Node saver = null;
        sum = new int[4]; // Initialize sum array outside of the loop

        if (tests != null) {
            for (Test test : tests) {
                if (test == null) {
                    continue;
                }
                Node tNode = new Node(20 + i.get(), "test" + i.get());
                score.addAll(test.getResults());
                int frequentNumber = Functions.findMostFrequentNumber(score);
                sum[frequentNumber % Edu.length] += 20;
                i.getAndIncrement();
                if (saver != null) {
                    graph.addEdge(saver, tNode, 4); // Add edge between every test
                }
                saver = tNode; // The next test

                for (Node node : pro) {
                    if (node != null && test.getResults() != null) {
                        for (Integer integer : test.getResults()) {
                            sum[integer % Edu.length] += test.getPointsPerAnswer().get(integer % Edu.length);
                        }

                        for (Integer integer : test.getResults()) {
                            if (integer!=null&&node.getLabel().equals(Edu[integer % Edu.length])&&saver!=null) {
                                graph.addEdge(saver, node, sum[node.getId() / 10 - 1]);
                                graph.addEdge(node, source, 0);
                            }
                        }
                    }
                }
                sum = new int[4]; // Reset sum array after processing each test
            }
        }

        return graph;
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
