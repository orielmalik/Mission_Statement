package com.example.missionstatement.paints;

import java.util.*;

public class DinicGraph {
    private Map<Node, List<Edge>> adjList;
    private int[][] capacity;
    private int[][] flow;
    private int[] level;
    private int V;

    public DinicGraph(int V) {
        this.V = V;
        this.adjList = new HashMap<>();
        capacity = new int[V][V];
        flow = new int[V][V];
        level = new int[V];
    }

    public void addNode(Node node) {
        adjList.putIfAbsent(node, new ArrayList<>());
    }

    public void addEdge(Node source, Node destination, int capacityValue) {
        if(source==null||adjList==null||destination==null)
        {
            return;
        }
        adjList.get(source).add(new Edge(source, destination, capacityValue));
        capacity[source.getId()][destination.getId()] = capacityValue;
    }

    private boolean bfs(int source, int sink) {
        Arrays.fill(level, -1);
        level[source] = 0;
        Queue<Integer> queue = new LinkedList<>();
        queue.add(source);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            if(null!=adjList.get(getNodeById(u))){
            for (Edge edge : adjList.get(getNodeById(u))) {
                int v = edge.destination.getId();
                if (level[v] < 0 && flow[u][v] < capacity[u][v]) {
                    level[v] = level[u] + 1;
                    queue.add(v);
                }
            }
        }}
        return level[sink] >= 0;
    }

    private int dfs(int u, int sink, int minFlow) {
        if (u == sink) return minFlow;
            if (adjList.get(getNodeById(u))!=null) {
            for (Edge edge : adjList.get(getNodeById(u))) {
                int v = edge.destination.getId();
                if (level[v] == level[u] + 1 && flow[u][v] < capacity[u][v]) {
                    int currFlow = Math.min(minFlow, capacity[u][v] - flow[u][v]);
                    int tempFlow = dfs(v, sink, currFlow);

                    if (tempFlow > 0) {
                        flow[u][v] += tempFlow;
                        flow[v][u] -= tempFlow;
                        return tempFlow;
                    }
                }
            }
        }
        return 0;
    }

    public Result dinicMaxFlow(int source, int sink) {
        int maxFlow = 0;
        while (bfs(source, sink)) {
            int flow;
            while ((flow = dfs(source, sink, Integer.MAX_VALUE)) > 0) {
                maxFlow += flow;
            }
        }
        List<Integer> path = getAugmentingPath(source, sink);
        return new Result(maxFlow, path);
    }

    public Map<Node, List<Edge>> getAdjList() {
        return adjList;
    }

    private List<Integer> getAugmentingPath(int source, int sink) {
        List<Integer> path = new ArrayList<>();
        boolean[] visited = new boolean[V];
        Stack<Integer> stack = new Stack<>();
        stack.add(source);

        while (!stack.isEmpty()) {
            int u = stack.pop();
            if (u == sink) {
                break;
            }
            for (Edge edge : adjList.get(getNodeById(u))) {
                int v = edge.destination.getId();
                if (!visited[v] && flow[u][v] > 0) {
                    visited[v] = true;
                    stack.add(v);
                    path.add(v);
                }
            }
        }
        return path;
    }

    public int getV() {
        return V;
    }

    public Node getNodeById(int id) {
        for (Node node : adjList.keySet()) {
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

        public List<Integer> getPath() {
            return path;
        }

        public String toString(DinicGraph graph) {
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

    // Edge class as inner class
    private static class Edge {
        Node source;
        Node destination;
        int capacity;
        int flow;

        Edge(Node source, Node destination, int capacity) {
            this.source = source;
            this.destination = destination;
            this.capacity = capacity;
            this.flow = 0;
        }
        @Override
        public String toString() {
            return "Edge{" +
                    "source=" + source +
                    ", destination=" + destination +
                    ", capacity=" + capacity +
                    ", flow=" + flow +
                    '}';
        }
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nodes:\n");
        for (Node node : adjList.keySet()) {
            sb.append(node.toString()).append("\n");
            sb.append("  Edges:\n");
            for (Edge edge : adjList.get(node)) {
                sb.append("    ").append(edge.toString()).append("\n");
            }
        }
        return sb.toString();
    }
}

