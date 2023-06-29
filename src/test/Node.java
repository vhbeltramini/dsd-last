package test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Node implements Runnable {
    private int id;
    private boolean isElectionMessageReceived;
    private boolean isLeader;
    private int cost;
    private List<Node> ring;

    public Node(int id, List<Node> ring) {
        this.id = id;
        this.isElectionMessageReceived = false;
        this.isLeader = false;
        this.cost = new Random().nextInt(100); // Random cost between 0 and 99
        this.ring = ring;
    }

    public int getId() {
        return id;
    }

    public boolean hasReceivedElectionMessage() {
        return isElectionMessageReceived;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public int getCost() {
        return cost;
    }

    public void receiveElectionMessage() {
        isElectionMessageReceived = true;
    }

    public void startElection() {
        if (!hasReceivedElectionMessage()) {
            System.out.println("Node " + id + " (Cost: " + cost + ") started the election.");
            receiveElectionMessage();

            Node maxCostNode = this;
            for (Node node : ring) {
                if (node.getCost() > maxCostNode.getCost()) {
                    maxCostNode = node;
                }
            }

            if (maxCostNode == this) {
                isLeader = true;
                System.out.println("Node " + id + " (Cost: " + cost + ") is the leader.");
            } else {
                maxCostNode.startElection();
                isLeader = false;
                System.out.println("Node " + id + " (Cost: " + cost + ") is not the leader.");
            }
        }
    }

    @Override
    public void run() {
        startElection();
    }
}