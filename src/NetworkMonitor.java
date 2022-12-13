import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

public class NetworkMonitor implements Runnable {

    /* Port used to send statistics and network monitor packets */
    public static int NETWORK_MONITOR_PORT = 6060;

    /* The current routing table */
    public static RoutingTable routingTable = new RoutingTable();

    /* Tells if the current ONode is receiving the stream at RDP Port */
    public static boolean receivingStream = false;

    public NetworkMonitor(List<InetAddress> neighbours) throws UnknownHostException{
        for (InetAddress neighbour : neighbours) {
            routingTable.addRow(neighbour, neighbour, 1);
        }
    }

    @Override
    public void run() { 
        System.out.println("[NETWORK MONITOR] Serving on port: " + NetworkMonitor.NETWORK_MONITOR_PORT);

        try (DatagramSocket socket = new DatagramSocket()) {
            // Send the current table to all neighbours
            for (RoutingTableRow row : routingTable.getTable()) {
                sendTable(socket, row.getAddress(), routingTable);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    /**
     * Send a routing table to a specific destination address
     * @param socket DatagramSocket from where the packet will be sent 
     * @param destinationAddress The destination InetAddress
     * @param table The table to send
     * @throws IOException
     */
    public static void sendTable(DatagramSocket socket, InetAddress destinationAddress, RoutingTable table) throws IOException{
        RoutingTable tableToSend = table.clone();
        tableToSend.removeRow(destinationAddress);
        StatPacket statPacket = new StatPacket(tableToSend);
        DatagramPacket packet = new DatagramPacket(statPacket.convertToBytes(), statPacket.convertToBytes().length);
        packet.setAddress(destinationAddress);
        packet.setPort(NETWORK_MONITOR_PORT);
        socket.send(packet);

    }
}