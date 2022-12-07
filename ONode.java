import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
  
// Server class
public class ONode {

    public static List<InetAddress> vizinhos = new ArrayList<>();

    public static void main(String[] args) throws UnknownHostException
    {
        System.out.println("Número de vizinhos: " + args.length);
        int index = 1;
        for (String vizinho : args) {
            System.out.println(index + "º vizinho: " + vizinho);
            vizinhos.add(InetAddress.getByName(vizinho));
        }

        
        for (String vizinho : args) {
            Client client = new Client(vizinho);
            Thread clientThread = new Thread(client);
            clientThread.start();
        }

        ServerSocket server = null;
  
        try {
  
            // server is listening on port 6060
            server = new ServerSocket(6060);
            server.setReuseAddress(true);
            System.out.println("[Server] Listening on port: 6060");
  
            // running infinite loop for getting
            // client request
            while (true) {
  
                // socket object to receive incoming client
                // requests
                Socket client = server.accept();


  
                // Displaying that new client is connected
                // to server
                System.out.println("[Server] New client connected "
                                   + client.getInetAddress()
                                   .getHostAddress());
  
                // create a new thread object
                ClientHandler clientSock = new ClientHandler(client,vizinhos);
  
                // This thread will handle the client
                // separately
                new Thread(clientSock).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (server != null) {
                try {
                    server.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}