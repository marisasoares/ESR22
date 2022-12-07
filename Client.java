import java.io.*;
import java.net.*;
import java.util.*;
  
// Client class
public class Client implements Runnable{

    public static String serverToConnect;

    public Client(String serverToConnect){
        Client.serverToConnect = serverToConnect;
    }
    
    // driver code
    public void run()
    {
        // establish a connection by providing host and port
        // number
        System.out.println("[Client] Trying to connect to: " + serverToConnect + " [6060]");
        try (Socket socket = new Socket(serverToConnect, 6060)) {
  
            StatPacket packet = new StatPacket(InetAddress.getLocalHost());
            System.out.println("[CLIENT] Created Packet: " + packet.toString());
            new ObjectOutputStream(socket.getOutputStream()).writeObject(packet);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}