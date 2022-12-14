import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class VideoForwarder{

    public static void sendVideoPacketToClients(DatagramSocket socket, DatagramPacket packet) throws UnknownHostException{
        if(!packet.getAddress().equals(InetAddress.getByName("127.0.0.1")))
            System.out.println("Received Stream from: " + packet.getAddress());
        List<InetAddress> sendTo = NetworkMonitor.getNeighboursRequestStream(); 
        for (InetAddress node : sendTo) {
            if(!node.equals(packet.getAddress())){
                System.out.println("Sent stream to: " + node);
                packet.setAddress(node);
                packet.setPort(ONode.RTP_dest_port);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }    
        }      
    }
    
}
