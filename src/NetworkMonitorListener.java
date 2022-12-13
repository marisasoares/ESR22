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
                Thread t = new Thread(new UpdateAndSendTable(socket,packet,timeWhenReceived));
                t.start();
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class UpdateAndSendTable implements Runnable {

        DatagramSocket socket;
        DatagramPacket packet;
        long timeWhenReceived;

        public UpdateAndSendTable(DatagramSocket socket, DatagramPacket packet,long timeWhenReceived) {
            this.socket = socket;
            this.packet = packet;
            this.timeWhenReceived = timeWhenReceived;
        }

        @Override
        public void run() {
            StatPacket statPacket;
            try {
                statPacket = StatPacket.fromBytes(packet.getData());
                long delay = this.timeWhenReceived - statPacket.getTimestamp();
                NetworkMonitor.routingTable.updateTable(statPacket.getTable(), packet.getAddress(),statPacket.requestStream(), delay);
                boolean readyToSend = statPacket.updatePacket(NetworkMonitor.routingTable);
                if(readyToSend){
                    for (RoutingTableRow row : NetworkMonitor.routingTable.getTable()) {
                        NetworkMonitor.sendTable(socket, row.getAddress(), NetworkMonitor.routingTable);
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
