import java.net.DatagramPacket;
import java.net.DatagramSocket;

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
                statPacket = StatPacket.fromBytes(packet.getData());
                long delay = timeWhenReceived - statPacket.getTimestamp();
                switch (statPacket.getType()) {
                    case TABLEREQUEST:
                        System.out.println("Request - Recebida tabela:");
                        System.out.println(statPacket.getTable());
                        System.out.println("Request Stream: " + statPacket.requestStream());
                        boolean localTableChanged = NetworkMonitor.routingTable.updateTable(statPacket.getTable(), packet.getAddress(),statPacket.requestStream(), delay);
                        System.out.println("Tabela atual:");
                        NetworkMonitor.routingTable.printTable();
                        if(localTableChanged){
                            System.out.println("Nada mudado");
                        }
                        RoutingTable localTable = NetworkMonitor.routingTable.clone();
                        localTable.removeRow(packet.getAddress());
                        NetworkMonitor.sendTableToAllNeighbours(socket);
                        break;
                    case TABLERESPONSE:
                        System.out.println("Response - Recebida tabela:");
                        System.out.println(statPacket.getTable());
                        localTableChanged = NetworkMonitor.routingTable.updateTable(statPacket.getTable(), packet.getAddress(),statPacket.requestStream(), delay);
                        System.out.println("Tabela atual:");
                        NetworkMonitor.routingTable.printTable();
                        if(localTableChanged){
                            System.out.println("Nada mudado");
                        }
                        break;
                    case PING:
                        
                        break;
                    case PONG:
                        
                        break;
                    default:
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

