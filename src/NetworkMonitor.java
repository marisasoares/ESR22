import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NetworkMonitor implements Runnable {

    /* Port used to send statistics and network monitor packets */
    public static int NETWORK_MONITOR_PORT = 6060;

    /* The current routing table */
    public static RoutingTable routingTable = new RoutingTable();

    /* Tells if the current ONode is receiving the stream at RDP Port */
    public static boolean receivingStream = false;

    public NetworkMonitor(List<InetAddress> neighbours) throws UnknownHostException {
        for (InetAddress neighbour : neighbours) {
            RoutingTableRow row;
            if (ONode.isClient) {
                row = new RoutingTableRow(neighbour, neighbour, 1, true, (long) -1);
            } else {
                row = new RoutingTableRow(neighbour, neighbour, 1, false, (long) -1);
            }
            routingTable.addRow(row);
        }
        NetworkMonitor.routingTable.printTable();
    }

    @Override
    public void run() {
        System.out.println("[NETWORK MONITOR] Serving on port: " + NetworkMonitor.NETWORK_MONITOR_PORT);

        try (DatagramSocket socket = new DatagramSocket()) {
            sendTableToAllNeighbours(socket,StatPacket.Type.TABLEREQUEST);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Send a routing table to a specific destination address
     * 
     * @param socket             DatagramSocket from where the packet will be sent
     * @param destinationAddress The destination InetAddress
     * @param table              The table to send
     * @throws IOException
     */
    public static void sendTable(DatagramSocket socket, InetAddress destinationAddress, RoutingTable table,
            StatPacket.Type type) throws IOException {
        RoutingTable tableToSend = table.clone();
        tableToSend.removeRow(destinationAddress);
        StatPacket statPacket;
        if(ONode.isClient){
            statPacket = new StatPacket(tableToSend, type,true);
        } else {
            statPacket = new StatPacket(tableToSend, type);
        }
        DatagramPacket packet = new DatagramPacket(statPacket.convertToBytes(), statPacket.convertToBytes().length);
        packet.setAddress(destinationAddress);
        packet.setPort(NETWORK_MONITOR_PORT);
        socket.send(packet);

    }

    /**
     * Send a routing table to a all neighbour ONodes and requests their table
     * 
     * @param socket DatagramSocket from where the packet will be sent
     * @param table  The table to send
     * @throws IOException
     */
    public static void sendTableToAllNeighbours(DatagramSocket socket) throws IOException {
        for (RoutingTableRow row : NetworkMonitor.routingTable.getTable()) {
            sendTable(socket, row.getAddress(),NetworkMonitor.routingTable ,StatPacket.Type.TABLERESPONSE);
        }
    }
    
    /**
     * Send a routing table to a all neighbour ONodes and requests their table
     * 
     * @param socket DatagramSocket from where the packet will be sent
     * @param table  The table to send
     * @throws IOException
     */
    public static void sendTableToAllNeighbours(DatagramSocket socket,StatPacket.Type type) throws IOException {
        for (RoutingTableRow row : NetworkMonitor.routingTable.getTable()) {
            sendTable(socket, row.getAddress(),NetworkMonitor.routingTable ,type);
        }
    }

    /**
     * Get all neighbours that request the stream
     * @return
     */
    public static List<InetAddress> getNeighboursRequestStream(){
        List<InetAddress> returnList = new ArrayList<>();
        for (RoutingTableRow row : NetworkMonitor.routingTable.getTable()) {
            if(row.requestStream()){
                returnList.add(row.getNextHop());
            }
        }
        // Return the list without duplicates
        return returnList.stream().distinct().collect(Collectors.toList());
    }
}
