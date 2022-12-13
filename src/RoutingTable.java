import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
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

    public void addRow(InetAddress address, InetAddress nextHop, int hopNumber){
        RoutingTableRow row = new RoutingTableRow(address,nextHop,hopNumber);
        table.add(row);
    }

    public void addRow(RoutingTableRow row){
        table.add(row);
    }


    public void removeRow(InetAddress address){
        Iterator<RoutingTableRow> it = table.iterator();
        while (it.hasNext()) {
            if(it.next().getAddress().equals(address)){
                it.remove();
            }
        }
    }

    public InetAddress getNextHop(InetAddress address){
        InetAddress nextHop = null;
        for (RoutingTableRow routingTableRow : table) {
            if(routingTableRow.getAddress().equals(address)){
                nextHop = routingTableRow.getNextHop();
            }
        }
        return nextHop;
    }

    public int getHopNumber(InetAddress address){
        int hopNumber = -1;
        for (RoutingTableRow routingTableRow : table) {
            if(routingTableRow.getAddress().equals(address)){
                hopNumber = routingTableRow.getHopNumber();
            }
        }
        return hopNumber;
    }


    public List<RoutingTableRow> getTable() {
        return this.table;
    }

    public boolean entryExists(InetAddress address){
        boolean contains = false;
        for (RoutingTableRow routingTableRow : this.table) {
            if(routingTableRow.getAddress().equals(address)) contains = true;
        }
        return contains;
    }

    public int getEntryIndex(InetAddress address){
        int index = 0;
        for (RoutingTableRow routingTableRow : this.table) {
            if(routingTableRow.getAddress().equals(address)) return index;
            index++;
        }
        return -1;
    }

    public boolean setRequestVideo(InetAddress address, boolean requestVideo){
        boolean changed = false;
        RoutingTable currentTable = this.clone();
        if(currentTable.entryExists(address)){
            if(currentTable.getTable().get(currentTable.getEntryIndex(address)).requestStream() != requestVideo) {
                changed = true;
            }  
            currentTable.getTable().get(currentTable.getEntryIndex(address)).setRequestStream(requestVideo);
        } 
        this.table = currentTable.getTable();
        return changed;
    }

    public void updateTable(RoutingTable receivedTable, InetAddress originAddress, boolean requestStream, long delay){
        RoutingTable currentRoutingTable = this.clone();
        boolean changed = false;
        for (RoutingTableRow routingTableRow : receivedTable.getTable()) {
            if(!currentRoutingTable.entryExists(routingTableRow.getAddress())){
                RoutingTableRow row = new RoutingTableRow(routingTableRow.getAddress(), originAddress, routingTableRow.getHopNumber()+1,routingTableRow.requestStream(),routingTableRow.getDelay());
                currentRoutingTable.addRow(row);
                changed = true;
            }
        }
        this.table = currentRoutingTable.getTable();
        if(changed) this.printTable();
    }



    /*
    public void updateTable(RoutingTable newTable,InetAddress ipReceivedFromNetwork, boolean requestVideo ,long delay) throws SocketException{
            RoutingTable currentTable = this.clone();
            boolean changed = false;
            for (RoutingTableRow routingTableRow : newTable.getTable()) {
                if(currentTable.entryExists(ipReceivedFromNetwork)){
                    currentTable.table.get(currentTable.getEntryIndex(ipReceivedFromNetwork)).setDelay(delay);
                } 
                if(!currentTable.entryExists(routingTableRow.getNetwork()) && !getAllIPsInterfaces().contains(routingTableRow.getNetwork())){
                    RoutingTableRow row = new RoutingTableRow(routingTableRow.getNetwork(), ipReceivedFromNetwork, routingTableRow.getHopNumber()+1,routingTableRow.getRequestsStream(),routingTableRow.getDelay());
                    currentTable.table.add(row);
                    changed = true;
                } else if(currentTable.entryExists(routingTableRow.getNetwork())){
                    changed = currentTable.setRequestVideo(ipReceivedFromNetwork, requestVideo);
                }
            }
            this.table = currentTable.table;
            System.out.println("Tabela atualizada");
            // DEBUG -----------------------------------------------------------
            if(changed){
                this.printTable();
            }
            
        
    }*/


    public RoutingTable clone(){
        RoutingTable rt = new RoutingTable();
        rt.table = new ArrayList<>(table);
        return rt;
    }

    public void printTable() {
        System.out.print("\033[H\033[2J");  
        StringBuilder sb = new StringBuilder();
        sb.append("ROUTING TABLE\n");
        sb.append("┌──────────────────┬──────────────────┬────────────┬────────────┬────────────┐\n");
        sb.append("│      Address     │     Next Hop     │ Hop Number │  Requests  │  Delay ms  │\n");
        Iterator <RoutingTableRow> it = table.iterator();
        while (it.hasNext()) {
            RoutingTableRow row = it.next();
            sb.append("├──────────────────┼──────────────────┼────────────┼────────────┼────────────┤\n");
            sb.append("│ " + String.format("%-16s", row.getAddress()) + " ");
            sb.append("│ " + String .format("%-16s", row.getNextHop()) + " ");
            sb.append("│ " + String.format("%-10s", row.getHopNumber()) + " ");
            sb.append("│ " + String.format("%-10s", row.requestStream()) + " ");
            sb.append("│ " + String.format("%-10s", row.getDelay()) + " │\n");
        }   
            sb.append("└──────────────────┴──────────────────┴────────────┴────────────┴────────────┘\n");
        
        System.out.println(sb.toString());
        System.out.flush(); 
    }


    @Override
    public String toString() {
        return "{" +
            " table='" + getTable() + "'" +
            "}";
    }
}
