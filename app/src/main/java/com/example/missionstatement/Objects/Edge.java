package com.example.missionstatement.Objects;

import androidx.annotation.NonNull;

import com.example.missionstatement.paints.Node;

// Edge.java
public class Edge {
public Node source;
public Node destination;
public int weight;

public Edge(Node source, Node destination, int weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
        }

        @NonNull
        @Override
        public String toString() {
                return "source "+source
                        +" dest"+destination
                        +"weight "+weight
                        ;
        }
}
