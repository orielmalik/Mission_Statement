package com.example.missionstatement.paints;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.missionstatement.Objects.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Graph {
    private Map<Node, List<Edge>> adjList;
    private final int[][] capacity; // מטריצת הקיבולות
    private final int V; // מספר הצמתים בגרף

    public Graph(int v) {
        this.V = v;
        this.adjList = new HashMap<>();
        capacity = new int[V][V];
    }

    public void addNode(Node node) {
        adjList.putIfAbsent(node, new ArrayList<>());
    }

    public void addEdge(Node source, Node destination, int weight) {
        adjList.get(source).add(new Edge(source, destination, weight));
    }

    public List<Edge> getEdges(Node node) {
        return adjList.get(node);
    }

    public Set<Node> getNodes() {
        return adjList.keySet();
    }

    public Map<Node, Node> dijkstra(Node start) {
        Map<Node, Integer> distances = new HashMap<>();
        Map<Node, Node> previousNodes = new HashMap<>();
        for (Node node : adjList.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
            previousNodes.put(node, null);
        }
        distances.put(start, 0);

        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        pq.add(start);

        while (!pq.isEmpty()) {
            Node current = pq.poll();

            for (Edge edge : adjList.get(current)) {
                Node neighbor = edge.destination;
                int newDist = distances.get(current) + edge.weight;

                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previousNodes.put(neighbor, current);
                    pq.add(neighbor);
                }
            }
        }

        return previousNodes;
    }

    public  Node findNodeById(Graph graph, int id) {
        for (Node node : graph.getNodes()) {
            if (node.getId() == id) {
                return node;
            }
        }
        return null; // או להחזיר אופציונלי
    }
public  Node findNodeBylabel(Graph graph, String id) {
        for (Node node : graph.getNodes()) {
            if (node.getLabel().equals(id)) {
                return node;
            }
        }
        return null; // או להחזיר אופציונלי
    }

    public List<Node> getShortestPath(Node start, Node end) {
        Map<Node, Node> previousNodes = dijkstra(start);
        List<Node> path = new ArrayList<>();
        for (Node at = end; at != null; at = previousNodes.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }

    // פונקציה לבדיקת מסלול מתווך (augmenting path) באמצעות BFS
    private boolean bfs(int[] parent, int source, int sink, List<Integer> path) {
        boolean[] visited = new boolean[V];
        Queue<Integer> queue = new LinkedList<>();
        queue.add(source);
        visited[source] = true;
        parent[source] = -1;

        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (int v = 0; v < V; v++) {
                if (!visited[v] && capacity[u][v] > 0) {
                    if (v == sink) {
                        parent[v] = u;
                        path.add(v);
                        return true;
                    }
                    queue.add(v);
                    parent[v] = u;
                    visited[v] = true;
                    path.add(v);
                }
            }
        }
        return false;
    }

    // פונקציה לחישוב הזרימה המקסימלית ולהחזרת הצמתים שבמסלול והזרימה המקסימלית
    public Result fordFulkerson(int source, int sink) {
        int u, v;
        int maxFlow = 0;
        int[] parent = new int[V];
        List<Integer> path = new ArrayList<>();

        // עד שיש מסלול מתווך, חפש והגדל את הזרימה
        while (bfs(parent, source, sink, path)) {
            int pathFlow = Integer.MAX_VALUE;

            // מצא את הזרימה המקסימלית במסלול המתווך שנמצא
            for (v = sink; v != source; v = parent[v]) {
                u = parent[v];
                pathFlow = Math.min(pathFlow, capacity[u][v]);
            }

            // עדכן את הקיבולות של הקשתות השונות במסלול
            for (v = sink; v != source; v = parent[v]) {
                u = parent[v];
                capacity[u][v] -= pathFlow;
                capacity[v][u] += pathFlow;
            }

            // הוסף את הזרימה המקסימלית למסלול הכללי
            maxFlow += pathFlow;
        }

        return new Result(maxFlow, path);
    }
    public Node getNodeById(int id) {
        for (Node node : getNodes()) {
            if (node.getId() == id) {
                return node;
            }
        }
        return null;
    }

    public static class Result {
        private int maxFlow;
        private List<Integer> path;

        public Result(int maxFlow, List<Integer> path) {
            this.maxFlow = maxFlow;
            this.path = path;
        }

        public String toString(Graph graph) {
            StringBuilder sb = new StringBuilder();
            sb.append("Max Flow: ").append(maxFlow).append("\n");
            sb.append("Path: ");
            for (Integer nodeId : path) {
                Node node = graph.getNodeById(nodeId);
                if (node != null) {
                    sb.append(node.toString()).append(" ");
                } else {
                    sb.append("Node ").append(nodeId).append(" ");
                }
            }
            return sb.toString();
        }

        public int getMaxFlow() {
            return maxFlow;
        }

        public void setMaxFlow(int maxFlow) {
            this.maxFlow = maxFlow;
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph:\n");

        sb.append("Nodes:\n");
        for (Node node : adjList.keySet()) {
            sb.append(node.toString()).append("\n");
        }

        sb.append("Edges:\n");
        for (Node node : adjList.keySet()) {
            for (Edge edge : adjList.get(node)) {
                sb.append(edge.toString()).append("\n");
            }
        }

        sb.append("Capacities:\n");
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                if (capacity[i][j] > 0) {
                    sb.append("From Node ").append(i).append(" to Node ").append(j)
                            .append(": Capacity ").append(capacity[i][j]).append("\n");
                }
            }
        }

        return sb.toString();
    }
    public void setGraph(int locationPoints, List<Test> tests, int sizeTests) {

    }


    public Map<Node, List<Edge>> getAdjList() {
        return adjList;
    }
}
