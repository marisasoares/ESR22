import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private List<InetAddress> vizinhos;

    // Constructor
    public ClientHandler(Socket socket, List<InetAddress> vizinhos)
    {
        this.clientSocket = socket;
        this.vizinhos = vizinhos;
    }

    public void run()
    {
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            while (clientSocket.isConnected() && !clientSocket.isClosed()) {
                StatPacket packet = (StatPacket) new ObjectInputStream(clientSocket.getInputStream()).readObject();
                System.out.println("[SERVER] Received packet: " + packet.toString());
                if(packet.updatePacket(InetAddress.getLocalHost())){
                    for (InetAddress vizinho : vizinhos) {
                    if(!vizinho.equals(clientSocket.getInetAddress())){
                           try(Socket socket = new Socket(vizinho,6060)){
                                System.out.println("[SERVER] Sending to " + vizinho);
                                new ObjectOutputStream(socket.getOutputStream()).writeObject(packet);
                           }
                        }
                    }
                } else {
                    System.out.println("[SERVER] PACKET TTL EXCEDDED");
                }
            }
        }
        catch (IOException | ClassNotFoundException e) {
            System.out.println("[SERVER] Closed Connection");
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                    clientSocket.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
