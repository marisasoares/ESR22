import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class NetworkMonitorListener implements Runnable {

    /* The buffer size for receiving Datagram packets */
    public static int BUFFER_SIZE = 150000;

    /* The buffer array */
    public byte[] buffer = new byte[BUFFER_SIZE];

    @Override
    public void run() {
        System.out.println("[NETWORK MONITOR] Listening on port: " + NetworkMonitor.NETWORK_MONITOR_PORT);
        try (DatagramSocket socket = new DatagramSocket(NetworkMonitor.NETWORK_MONITOR_PORT)) {
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, BUFFER_SIZE);
                socket.receive(packet);
                long timeWhenReceived = System.currentTimeMillis();
                StatPacket statPacket;
            try {
                statPacket = StatPacket.fromBytes(packet.getData());
                long delay = timeWhenReceived - statPacket.getTimestamp();
                boolean localTableChanged = NetworkMonitor.routingTable.updateTable(statPacket.getTable(), packet.getAddress(),statPacket.requestStream(), delay);
                if(localTableChanged){
                    NetworkMonitor.routingTable.printTable();
                    System.out.println("Time: " + System.currentTimeMillis());
                }
                boolean readyToSend = statPacket.updatePacket(NetworkMonitor.routingTable);

                /* Check if received table is diferent than local table */
                RoutingTable localTable = NetworkMonitor.routingTable.clone();
                localTable.removeRow(packet.getAddress());
                if(readyToSend &&  !localTable.equivalentTables(statPacket.getTable())){
                    for (RoutingTableRow row : NetworkMonitor.routingTable.getTable()) {
                        NetworkMonitor.sendTable(socket, row.getAddress(), NetworkMonitor.routingTable);
                    }
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
}
