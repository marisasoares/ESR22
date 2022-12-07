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
            
            // writing to server
            PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true);
  
            // reading from server
            BufferedReader in
                = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
  
            // object of scanner class
            Scanner sc = new Scanner(System.in);
            String line = null;
  
            while (!"exit".equalsIgnoreCase(line)) {
                
                // reading from user
                line = sc.nextLine();
  
                // sending the user input to server
                out.println(line);
                out.flush();
                StatPacket packet = (StatPacket) new ObjectInputStream(socket.getInputStream()).readObject();
                System.out.println("[CLIENT] Packet received: \n----------------------\n" + packet.toString());
                packet.updatePacket(InetAddress.getLocalHost());
                System.out.println("[CLIENT] Pacote atualizado: " + packet.toString());
                
            }
            
            // closing the scanner object
            sc.close();
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}