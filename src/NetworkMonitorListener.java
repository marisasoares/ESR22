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
                System.out.print("\033[H\033[2J");  
                System.out.flush();
                System.out.println("Pacote recebido de " + packet.getAddress() + ": ");
                System.out.println(statPacket.toString());
                switch (statPacket.getType()) {
                    case TABLEREQUEST:
                        boolean localTableChanged = NetworkMonitor.routingTable.updateTable(statPacket.getTable(), packet.getAddress(),statPacket.requestStream(), delay);
                        NetworkMonitor.routingTable.printTable();
                        if(localTableChanged){
                            System.out.println("Nada mudado");
                        }
                        RoutingTable localTable = NetworkMonitor.routingTable.clone();
                        localTable.removeRow(packet.getAddress());
                        NetworkMonitor.sendTableToAllNeighbours(socket);
                        break;
                    case TABLERESPONSE:
                        localTableChanged = NetworkMonitor.routingTable.updateTable(statPacket.getTable(), packet.getAddress(),statPacket.requestStream(), delay);
                        NetworkMonitor.routingTable.printTable();
                        if(localTableChanged){
                            System.out.println("Nada mudado");
                        }
                        break;
                    case PING:
                        NetworkMonitor.pingRequests.remove(packet.getAddress());
                        NetworkMonitor.sendPong(socket, packet.getAddress(), statPacket);        
                        NetworkMonitor.routingTable.getRow(packet.getAddress()).setDelay(delay);
                        NetworkMonitor.routingTable.printTable();
                        break;
                    case PONG:
                        NetworkMonitor.pingRequests.remove(packet.getAddress());
                        NetworkMonitor.routingTable.getRow(packet.getAddress()).setDelay(delay);     
                        NetworkMonitor.routingTable.printTable();
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

