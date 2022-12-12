import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
                InetAddress originNetworkAddress = IPUtils.computeNetworkAddress(packet.getAddress(), InetAddress.getByName("255.255.255.0"));
                StatPacket statPacket = StatPacket.fromBytes(packet.getData());
                if(statPacket.getRequestVideo()){
                    System.out.println("Recebido request video!!");
                    NetworkMonitor.routingTable.setRequestVideo(originNetworkAddress, true);
                } else{
                    long delay = System.currentTimeMillis() - statPacket.getTimestamp();
                    NetworkMonitor.routingTable.updateTable(statPacket.getTable(), originNetworkAddress, delay);
                    boolean readyToSend = statPacket.updatePacket(NetworkMonitor.routingTable);
                    for (RoutingTableRow row : NetworkMonitor.routingTable.getTable()) {
                        if(readyToSend){
                            //Enviar tabela sem a linha do endereço destino
                            RoutingTable table = NetworkMonitor.routingTable.clone();
                            table.removeRow(row.getNetwork());
                            statPacket.setTable(table);
                            packet.setData(statPacket.convertToBytes());  
                            packet.setLength(statPacket.convertToBytes().length);
                            packet.setAddress(row.getNetwork());
                            socket.send(packet);
                        }           
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
