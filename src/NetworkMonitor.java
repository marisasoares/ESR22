import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;

public class NetworkMonitor implements Runnable {

    public static int NETWORK_MONITOR_PORT = 6060;
    public static int BUFFER_SIZE = 15000;
    public byte[] buffer = new byte[BUFFER_SIZE];
    public static RoutingTable routingTable = new RoutingTable();

    public NetworkMonitor(List<InetAddress> vizinhos){
        for (InetAddress vizinho : vizinhos) {
            routingTable.addRow(vizinho, vizinho, 1);
        }
    }

    @Override
    public void run() { 
        System.out.println("[NETWORK MONITOR] Listening on port: " + NetworkMonitorListener.NETWORK_MONITOR_PORT);

        try (DatagramSocket socket = new DatagramSocket(NETWORK_MONITOR_PORT)) {
            DatagramPacket packet = new DatagramPacket(buffer, BUFFER_SIZE);
            for (RoutingTableRow row : routingTable.getTable()) {
                StatPacket statPacket = new StatPacket(routingTable);
                packet.setAddress(row.getVizinho());
                packet.setPort(NETWORK_MONITOR_PORT);
                packet.setData(statPacket.convertToBytes());
                socket.send(packet);
            }
        
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    
}