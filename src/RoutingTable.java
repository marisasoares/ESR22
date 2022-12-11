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

    public void addRow(InetAddress vizinho, InetAddress nextHop, int hopNumber){
        RoutingTableRow row = new RoutingTableRow(vizinho,nextHop,hopNumber);
        table.add(row);
    }

    public void addRow(RoutingTableRow row){
        table.add(row);
    }


    public void removeRow(InetAddress vizinho){
        Iterator<RoutingTableRow> it = table.iterator();
        while (it.hasNext()) {
            if(it.next().getVizinho().equals(vizinho)){
                it.remove();
            }
        }
    }

    public InetAddress getNextHop(InetAddress vizinho){
        InetAddress nextHop = null;
        for (RoutingTableRow routingTableRow : table) {
            if(routingTableRow.getVizinho().equals(vizinho)){
                nextHop = routingTableRow.getNextHop();
            }
        }
        return nextHop;
    }

    public int getHopNumber(InetAddress vizinho){
        int hopNumber = -1;
        for (RoutingTableRow routingTableRow : table) {
            if(routingTableRow.getVizinho().equals(vizinho)){
                hopNumber = routingTableRow.getHopNumber();
            }
        }
        return hopNumber;
    }


    public List<RoutingTableRow> getTable() {
        return this.table;
    }

    public boolean vizinhoExists(InetAddress vizinho, List<RoutingTableRow> table){
        boolean contains = false;
        for (RoutingTableRow routingTableRow : table) {
            if(routingTableRow.getVizinho().equals(vizinho)) contains = true;
        }
        return contains;
    }

    public void updateTable(RoutingTable table,InetAddress vizinho){
            List<RoutingTableRow> currentTable = new ArrayList<>(this.table);
            boolean changed = false;
            for (RoutingTableRow routingTableRow : table.getTable()) {
                if(!vizinhoExists(routingTableRow.getVizinho(),currentTable)){
                    RoutingTableRow row = new RoutingTableRow(routingTableRow.getVizinho(), vizinho, getHopNumber(vizinho)+1);
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
        sb.append("│      Vizinho     │     Next Hop     │ Hop Number │\n");
        Iterator <RoutingTableRow> it = table.iterator();
        while (it.hasNext()) {
            RoutingTableRow row = it.next();
            sb.append("├──────────────────┼──────────────────┼────────────┤\n");
            sb.append("│ " + String.format("%-16s", row.getVizinho()) + " ");
            sb.append("│ " + String.format("%-16s", row.getNextHop()) + " ");
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

}
