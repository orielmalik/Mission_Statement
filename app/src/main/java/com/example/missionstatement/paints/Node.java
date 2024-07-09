package com.example.missionstatement.paints;

import java.util.Objects;

public class Node implements Comparable<Node> {
    private int id ;
    private String label;

    public Node(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return id == node.id && Objects.equals(label, node.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, label);
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.id, other.id);
    }
}



