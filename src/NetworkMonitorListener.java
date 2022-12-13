import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class NetworkMonitorListener implements Runnable {

    /* The buffer size for receiving Datagram packets */
    public static int BUFFER_SIZE = 150000;

    /* The buffer array */
    public byte[] buffer = new byte[BUFFER_SIZE];

    public static List<InetAddress> tableRequesters = new ArrayList<>();

    /* All known nodes */
    private List<InetAddress> nodes = new ArrayList<>();

    /* Current node to add nodes to */
    private int currentNode = 0;

    /* Number of connections to the current node connections */
    private int currentNodeConnections = 0;

    @Override
    public void run() {
        System.out.println("[NETWORK MONITOR] Listening on port: " + NetworkMonitor.NETWORK_MONITOR_PORT);
        try (DatagramSocket socket = new DatagramSocket(NetworkMonitor.NETWORK_MONITOR_PORT)) {
            /* Adds "ourselves" to the list of all known nodes */
            nodes.add(InetAddress.getByName("127.0.0.0"));
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, BUFFER_SIZE);
                socket.receive(packet);
                long timeWhenReceived = System.currentTimeMillis();
                StatPacket statPacket;
                try {
                    statPacket = StatPacket.fromBytes(packet.getData());
                    System.out.println("Received packet: " + statPacket.toString());
                    long delay = timeWhenReceived - statPacket.getTimestamp();
                    /* Only respond to requests if we are a content provider */
                    if (statPacket.isRequest() && ONode.isContentProvider) {
                        if (NetworkMonitor.routingTable.entryExists(packet.getAddress())) {
                            NetworkMonitor.routingTable.getRow(packet.getAddress()).setDelay(delay);
                            NetworkMonitor.routingTable.getRow(packet.getAddress())
                                    .setRequestStream(statPacket.requestStream());
                        } else {
                           updateLocalTable(packet, statPacket, delay);
                        }
                        System.out.print("\033[H\033[2J");
                        NetworkMonitor.routingTable.printTable();

                    }
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }

            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLocalTable(DatagramPacket packet, StatPacket statPacket, long delay){
        nodes.add(packet.getAddress());
        System.out.println("Lista de nodos: ");
        for (InetAddress address : nodes) {
            System.out.println(address);
        }
        RoutingTableRow row;
        if (currentNodeConnections < 3) {
            row = new RoutingTableRow(packet.getAddress(), nodes.get(currentNodeConnections + 1),
                    NetworkMonitor.getHopNumber(nodes.get(currentNode)) + 1,
                    statPacket.requestStream(), delay);
            currentNodeConnections++;
        } else {
            currentNodeConnections = 0;
            currentNode++;
            row = new RoutingTableRow(packet.getAddress(), nodes.get(currentNodeConnections + 1),
                    NetworkMonitor.getHopNumber(nodes.get(currentNode)) + 1,
                    statPacket.requestStream(), delay);
        }
        NetworkMonitor.routingTable.addRow(row);
    }
}
