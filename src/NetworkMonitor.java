import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkMonitor implements Runnable {

    /* Port used to send statistics and network monitor packets */
    public static int NETWORK_MONITOR_PORT = 6060;

    /* The current routing table */
    public static RoutingTable routingTable = new RoutingTable();

    /* Tells if the current ONode is receiving the stream at RDP Port */
    public static boolean receivingStream = false;


    public NetworkMonitor(){
    }

    @Override
    public void run() { 
        System.out.println("[NETWORK MONITOR] Serving on port: " + NetworkMonitor.NETWORK_MONITOR_PORT);
        try {
            RoutingTableRow row = new RoutingTableRow(InetAddress.getByName("127.0.0.0"),InetAddress.getByName("127.0.0.0") , 0);
            routingTable.addRow(row);
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
        try (DatagramSocket socket = new DatagramSocket()) {
            StatPacket statPacket;
            if(ONode.isClient){
              statPacket = new StatPacket(true, true);
            } else{
              statPacket = new StatPacket(true, false);
            }
            if(!ONode.isContentProvider){
                DatagramPacket packet = new DatagramPacket(statPacket.convertToBytes(), statPacket.convertToBytes().length);
                packet.setAddress(ONode.bootstrap);
                packet.setPort(NETWORK_MONITOR_PORT);
                socket.send(packet);
                System.out.println("Sent packet to :" + ONode.bootstrap );
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }   
    }

    /**
     * Returns the hop number of an address already in the table
     * @param address The address that we want to get the hop number
     * @return the hop number
     */
    public static int getHopNumber(InetAddress address){
        return NetworkMonitor.routingTable.getRow(address).getHopNumber();
    }

    

    // /**
    //  * Send a routing table to a all neighbour ONodes and requests their table
    //  * @param socket DatagramSocket from where the packet will be sent 
    //  * @param table The table to send
    //  * @param originAddress The tables' address of origin
    //  * @return boolean true if there are neighbours to send the request to
    //  * @throws IOException
    //  */
    // public static boolean sendTableRequestToAllNeighbours(DatagramSocket socket, RoutingTable table , InetAddress originAddress) throws IOException{
    //     boolean thereAreMoreNeighboursToSend = false;
    //     InetAddress nextHop = null;
    //     if(!table.getTable().isEmpty()){
    //         nextHop = table.getTable().get(0).getNextHop();
    //     } 
    //     for (RoutingTableRow row : table.getTable()) {
    //         if(!row.getNextHop().equals(nextHop)) thereAreMoreNeighboursToSend = true;
    //         if(!row.getAddress().equals(originAddress)){
    //             sendTableRequest(socket, row.getAddress(), table);
    //         }
    //     } 
    //     return thereAreMoreNeighboursToSend;
    // }

    // /**
    //  * Send a routing table to a specific destination address and requests their table
    //  * @param socket DatagramSocket from where the packet will be sent 
    //  * @param destinationAddress The destination InetAddress
    //  * @param table The table to send
    //  * @throws IOException
    //  */
    // public static void sendTableRequest(DatagramSocket socket, InetAddress destinationAddress, RoutingTable table) throws IOException{
    //     RoutingTable tableToSend = table.clone();
    //     tableToSend.removeRow(destinationAddress);
    //     StatPacket statPacket = new StatPacket(tableToSend,true);
    //     DatagramPacket packet = new DatagramPacket(statPacket.convertToBytes(), statPacket.convertToBytes().length);
    //     packet.setAddress(destinationAddress);
    //     packet.setPort(NETWORK_MONITOR_PORT);
    //     socket.send(packet);

    // }

    // /**
    //  * Send a routing table to a specific destination address and requests their table
    //  * @param socket DatagramSocket from where the packet will be sent 
    //  * @param destinationAddress The destination InetAddress
    //  * @param table The table to send
    //  * @throws IOException
    //  */
    // public static void sendResponseTable(DatagramSocket socket, InetAddress destinationAddress, RoutingTable table) throws IOException{
    //     RoutingTable tableToSend = table.clone();
    //     tableToSend.removeRow(destinationAddress);
    //     StatPacket statPacket = new StatPacket(tableToSend,false);
    //     DatagramPacket packet = new DatagramPacket(statPacket.convertToBytes(), statPacket.convertToBytes().length);
    //     packet.setAddress(destinationAddress);
    //     packet.setPort(NETWORK_MONITOR_PORT);
    //     socket.send(packet);

    // }

    //  /**
    //  * Send a routing table to a all neighbour ONodes and requests their table
    //  * @param socket DatagramSocket from where the packet will be sent 
    //  * @param table The table to send
    //  * @throws IOException
    //  */
    // public static void sendResponseTableToAddresses(DatagramSocket socket, RoutingTable table, List<InetAddress> addresses) throws IOException{
    //     for (InetAddress inetAddress : addresses) {
    //         sendResponseTable(socket,inetAddress, table);
    //     }

    // }
}
