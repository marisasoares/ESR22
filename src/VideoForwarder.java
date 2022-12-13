import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class VideoForwarder implements Runnable{

    /* The list of InetAddresses to forward the video to */
    public List<InetAddress> sendTo;
    /* The InetAddress of the ONode from which the video was received */
    public InetAddress receivedFromIP;
    /* The Datagram socket used */
    public DatagramSocket socket;
    /* The actual packet that was received and must be forwarded to */
    public DatagramPacket packet;

    /* Constructor */
    public VideoForwarder(List<InetAddress> sendTo, DatagramSocket socket, DatagramPacket packet){
        this.sendTo = sendTo;
        this.socket = socket;
        this.receivedFromIP = packet.getAddress();
        this.packet = packet;
        
    }

    @Override
    public void run() {
        for (InetAddress neighbour : sendTo) {
            if(!neighbour.equals(receivedFromIP)){
                packet.setAddress(neighbour);
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
