import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class VideoForwarder implements Runnable{

    public List<InetAddress> sendTo;
    public InetAddress receivedFromIP;
    public DatagramSocket socket;
    public DatagramPacket packet;

    public VideoForwarder(List<InetAddress> sendTo, DatagramSocket socket, DatagramPacket packet){
        this.sendTo = sendTo;
        this.socket = socket;
        this.receivedFromIP = packet.getAddress();
        this.packet = packet;
        
    }

    @Override
    public void run() {
        for (InetAddress vizinho : sendTo) {
            if(!vizinho.equals(receivedFromIP)){
                packet.setAddress(vizinho);
                packet.setPort(25000);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }    
        }
    }
    
}
