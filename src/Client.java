import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private final String serverAddress;
    private final int serverPort;
    private final int candidateId;

    public Client(String serverAddress, int serverPort, int candidateId) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.candidateId = candidateId;
    }

    public void start() {
        try {
            Socket clientSocket = new Socket(serverAddress, serverPort);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            out.println("ELECTION");
            out.println("CANDIDATE_" + candidateId);

            String data;
            while ((data = in.readLine()) != null) {
                if (data.startsWith("CANDIDATE_")) {
                    System.out.println("Received message: " + data);
                    out.println("ELECTION");
                } else if (data.startsWith("ELECTION_COMPLETE")) {
                    int winnerId = Integer.parseInt(data.split(" ")[1]);
                    System.out.println("Election complete! Winner: " + winnerId);
                    break;
                } else {
                    System.out.println("Received message: " + data);
                    out.println(data);
                }
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: Client <serverAddress> <serverPort> <candidateId>");
            return;
        }

        String serverAddress = args[0];
        int serverPort = Integer.parseInt(args[1]);
        int candidateId = Integer.parseInt(args[2]);

        Client client = new Client(serverAddress, serverPort, candidateId);
        client.start();
    }
}
