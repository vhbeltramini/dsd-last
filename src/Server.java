import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private final int port;
    private final int numClients;
    private final List<Socket> clients;
    private Integer winner;
    private boolean electionComplete;

    public Server(int port, int numClients) {
        this.port = port;
        this.numClients = numClients;
        this.clients = new ArrayList<>();
        this.winner = null;
        this.electionComplete = false;
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            System.out.println("Server listening on port " + port);
            System.out.println("Server IP " + InetAddress.getLocalHost().getHostAddress());

            for (int i = 0; i < numClients; i++) {
                Socket clientSocket = serverSocket.accept();
                clients.add(clientSocket);
                new Thread(() -> handleClient(clientSocket)).start();
            }

            initiateElection();

            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initiateElection() {
        Socket firstClient = clients.get(0);
        PrintWriter out = null;

        try {
            out = new PrintWriter(firstClient.getOutputStream(), true);
            out.println("ELECTION");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        int clientId = clients.size();
        System.out.println("New client connected: " + clientId);

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String data;
            while ((data = in.readLine()) != null) {
                if (data.startsWith("CANDIDATE_")) {
                    handleCandidate(Integer.parseInt(data.split("_")[1]), out);
                } else {
                    handleMessage(data, out);
                }
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void handleCandidate(int candidateId, PrintWriter out) {
        if (winner == null || candidateId > winner) {
            winner = candidateId;
        }

        int nextClientId = (clients.indexOf(out) + 1) % numClients;
        Socket nextClient = clients.get(nextClientId);
        PrintWriter nextOut = null;

        try {
            nextOut = new PrintWriter(nextClient.getOutputStream(), true);
            nextOut.println("CANDIDATE_" + winner);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void handleMessage(String data, PrintWriter out) {
        System.out.println("Received message: " + data);
        out.println(data);
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: Server <port> <numClients>");
            return;
        }

        int port = Integer.parseInt(args[0]);
        int numClients = Integer.parseInt(args[1]);

        Server server = new Server(port, numClients);
        server.start();
    }
}
