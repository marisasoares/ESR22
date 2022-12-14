import java.net.InetAddress;
import java.net.UnknownHostException;

public class Test {
    public static void main(String[] args) throws UnknownHostException {
        RoutingTable table = new RoutingTable();
        RoutingTableRow r1 = new RoutingTableRow(InetAddress.getByName("10.0.2.10"), InetAddress.getByName("10.0.2.10"), 1,false,(long) 3);
        RoutingTableRow r2 = new RoutingTableRow(InetAddress.getByName("10.0.1.1"), InetAddress.getByName("10.0.1.1"), 1,false,(long) 6);
        RoutingTableRow r3 = new RoutingTableRow(InetAddress.getByName("10.0.0.20"), InetAddress.getByName("10.0.1.1"), 2,true,(long) 24);
        RoutingTableRow r4 = new RoutingTableRow(InetAddress.getByName("10.0.3.20"), InetAddress.getByName("10.0.1.1"), 2,false,(long) 13);
        table.addRow(r1);
        table.addRow(r2);
        table.addRow(r3);
        table.addRow(r4);
        table.printTable();
    
    }
}
