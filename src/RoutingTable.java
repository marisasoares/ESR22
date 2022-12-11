import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;


public class RoutingTable implements Serializable{
    
    private List<RoutingTableRow> table;


    public RoutingTable() {
        this.table = new ArrayList<>();
    }

    public RoutingTable(List<RoutingTableRow> table) {
        this.table = table;
    }

    public void addRow(InetAddress network, InetAddress nextHop, int hopNumber){
        RoutingTableRow row = new RoutingTableRow(network,nextHop,hopNumber);
        table.add(row);
    }

    public void addRow(RoutingTableRow row){
        table.add(row);
    }


    public void removeRow(InetAddress network){
        Iterator<RoutingTableRow> it = table.iterator();
        while (it.hasNext()) {
            if(it.next().getNetwork().equals(network)){
                it.remove();
            }
        }
    }

    public InetAddress getNextHop(InetAddress network){
        InetAddress nextHop = null;
        for (RoutingTableRow routingTableRow : table) {
            if(routingTableRow.getNetwork().equals(network)){
                nextHop = routingTableRow.getNextHop();
            }
        }
        return nextHop;
    }

    public int getHopNumber(InetAddress network){
        int hopNumber = -1;
        for (RoutingTableRow routingTableRow : table) {
            if(routingTableRow.getNetwork().equals(network)){
                hopNumber = routingTableRow.getHopNumber();
            }
        }
        return hopNumber;
    }


    public List<RoutingTableRow> getTable() {
        return this.table;
    }

    public boolean entryExists(InetAddress network, List<RoutingTableRow> table){
        boolean contains = false;
        for (RoutingTableRow routingTableRow : table) {
            if(routingTableRow.getNetwork().equals(network)) contains = true;
        }
        return contains;
    }

    public void updateTable(RoutingTable table,InetAddress network) throws SocketException{
            List<RoutingTableRow> currentTable = new ArrayList<>(this.table);
            boolean changed = false;
            for (RoutingTableRow routingTableRow : table.getTable()) {
                if(!entryExists(routingTableRow.getNetwork(),currentTable) && !getAllIPsInterfaces().contains(routingTableRow.getNetwork())){
                    RoutingTableRow row = new RoutingTableRow(routingTableRow.getNetwork(), network, getHopNumber(network)+1);
                    currentTable.add(row);
                    changed = true;
                }
            }
            this.table = currentTable;
            // DEBUG -----------------------------------------------------------
            if(changed){
                NetworkMonitor.routingTable.printTable();
            }
            
        
    }


    public RoutingTable clone(){
        RoutingTable rt = new RoutingTable();
        rt.table = new ArrayList<>(table);
        return rt;
    }

    public void printTable() {
        System.out.print("\033[H\033[2J");  
        StringBuilder sb = new StringBuilder();
        sb.append("ROUTING TABLE\n");
        sb.append("┌──────────────────┬──────────────────┬────────────┐\n");
        sb.append("│      Network     │     Next Hop     │ Hop Number │\n");
        Iterator <RoutingTableRow> it = table.iterator();
        while (it.hasNext()) {
            RoutingTableRow row = it.next();
            sb.append("├──────────────────┼──────────────────┼────────────┤\n");
            sb.append("│ " + String.format("%-16s", row.getNetwork()) + " ");
            sb.append("│ " + String .format("%-16s", row.getNextHop()) + " ");
            sb.append("│ " + String.format("%-10s", row.getHopNumber()) + " │\n");
        }   
            sb.append("└──────────────────┴──────────────────┴────────────┘\n");
        
        System.out.println(sb.toString());
        System.out.flush(); 
    }


    @Override
    public String toString() {
        return "{" +
            " table='" + getTable() + "'" +
            "}";
    }

    private List<InetAddress> getAllIPsInterfaces() throws SocketException{
        List<InetAddress> addrList = new ArrayList<InetAddress>();
        for(Enumeration<NetworkInterface> eni = NetworkInterface.getNetworkInterfaces(); eni.hasMoreElements(); ) {
            final NetworkInterface ifc = eni.nextElement();
            if(ifc.isUp()) {
                for(Enumeration<InetAddress> ena = ifc.getInetAddresses(); ena.hasMoreElements(); ) {
                    addrList.add(ena.nextElement());
                }
            }
        }
        return addrList;
    }

}
