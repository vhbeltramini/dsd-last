package test;

import java.util.ArrayList;
import java.util.List;

public class RingElection {
    public static void main(String[] args) {
        List<Node> ring = new ArrayList<>();
        int numNodes = 5; // number of nodes in the ring

        // Create the nodes in the ring
        for (int i = 0; i < numNodes; i++) {
            ring.add(new Node(i, ring));
        }

        // Configure and start the threads for the nodes in the ring
        List<Thread> threads = new ArrayList<>();
        for (Node node : ring) {
            Thread thread = new Thread(node);
            threads.add(thread);
            thread.start();
        }

        // Wait for the threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
