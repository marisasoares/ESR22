import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

public class NetworkMonitor implements Runnable {

    public static int NETWORK_MONITOR_PORT = 6060;
    public static int BUFFER_SIZE = 150000;
    public byte[] buffer = new byte[BUFFER_SIZE];
    public static RoutingTable routingTable = new RoutingTable();
    public static boolean receivingStream = false;

    public NetworkMonitor(List<InetAddress> vizinhos) throws UnknownHostException{
        InetAddress mask = InetAddress.getByName("255.255.255.0");
        for (InetAddress vizinho : vizinhos) {
            routingTable.addRow(IPUtils.computeNetworkAddress(vizinho, mask), vizinho, 1);
        }
    }

    @Override
    public void run() { 
        System.out.println("[NETWORK MONITOR] Listening on port: " + NetworkMonitorListener.NETWORK_MONITOR_PORT);

        try (DatagramSocket socket = new DatagramSocket()) {
            while (true) {
                for (RoutingTableRow row : routingTable.getTable()) {
                    RoutingTable tableToSend = routingTable.clone();
                    tableToSend.removeRow(row.getNetwork());
                    StatPacket statPacket = new StatPacket(tableToSend);
                    DatagramPacket packet = new DatagramPacket(buffer, statPacket.convertToBytes().length);
                    packet.setAddress(row.getNetwork());
                    packet.setPort(NETWORK_MONITOR_PORT);
                    packet.setData(statPacket.convertToBytes());
                    socket.send(packet);
                }
            }
            
        
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }    
}