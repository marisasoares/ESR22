import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


public class NetworkMonitorListener implements Runnable {

    public static int NETWORK_MONITOR_PORT = 6060;
    public static int BUFFER_SIZE = 150000;
    public byte[] buffer = new byte[BUFFER_SIZE];

    @Override
    public void run() {
        System.out.println("[NETWORK MONITOR] Serving on port: " + NetworkMonitor.NETWORK_MONITOR_PORT);
        try (DatagramSocket socket = new DatagramSocket(NETWORK_MONITOR_PORT)) {
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, BUFFER_SIZE);
                socket.receive(packet);
                StatPacket statPacket = StatPacket.fromBytes(packet.getData());
                NetworkMonitor.routingTable.updateTable(statPacket.getTable(), packet.getAddress());
                boolean readyToSend = statPacket.updatePacket(NetworkMonitor.routingTable);
                for (RoutingTableRow row : NetworkMonitor.routingTable.getTable()) {
                    if(readyToSend){
                        packet.setData(statPacket.convertToBytes());  
                        packet.setLength(statPacket.convertToBytes().length);
                        packet.setAddress(row.getVizinho());
                        socket.send(packet);
                    }           
                }
            }
        
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
    }

    
}
