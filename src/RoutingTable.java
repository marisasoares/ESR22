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

    /* Given an address return the row associated with that adress  */
    public RoutingTableRow getRow(InetAddress address){
        RoutingTableRow row = null;
        if(this.entryExists(address)){
            row = this.getTable().get(this.getEntryIndex(address));
        }
        return row;
    }

    /* Changes the state of the requestStream for the specific address */
    public boolean requestStream(InetAddress address, boolean requestStream){
        boolean changed = false;
        RoutingTable currentTable = this.clone();
        RoutingTableRow row = currentTable.getRow(address);
        if(row != null){
            if(row.requestStream() != requestStream) {
                row.setRequestStream(requestStream);
                changed = true;
            }
        }
        this.table = currentTable.getTable();
        return changed;
    }

    public boolean updateTable(RoutingTable receivedTable, InetAddress originAddress, boolean requestStream, long delay){
        RoutingTable currentRoutingTable = this.clone();
        boolean changed = false;
        for (RoutingTableRow routingTableRow : receivedTable.getTable()) {
            RoutingTableRow localRow = currentRoutingTable.getRow(originAddress);
            if(localRow != null){
                localRow.setDelay(delay);
                changed = currentRoutingTable.requestStream(originAddress, requestStream);
            } else {
                RoutingTableRow row = new RoutingTableRow(routingTableRow.getAddress(), originAddress, routingTableRow.getHopNumber()+1,routingTableRow.requestStream(),routingTableRow.getDelay());
                currentRoutingTable.addRow(row);
                changed = true;
            }
        }
        this.table = currentRoutingTable.getTable();
        return changed;
    }


    public RoutingTable clone(){
        RoutingTable rt = new RoutingTable();
        rt.table = new ArrayList<>(table);
        return rt;
    }

    /* Pretty printing of the table */
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

    public boolean equivalentTables(RoutingTable t1){
        boolean equivalentTables = false;
        for (RoutingTableRow routingTableRow : this.getTable()) {
            RoutingTableRow localRow = t1.getRow(routingTableRow.getAddress());
            if(localRow != null){
                if(localRow.requestStream() == routingTableRow.requestStream() && localRow.getDelay() != -1 && routingTableRow.getDelay() != -1){
                    equivalentTables = true;
                }
            }
        }
        return equivalentTables;
    }


    @Override
    public String toString() {
        return "{" +
            " table='" + getTable() + "'" +
            "}";
    }
}
