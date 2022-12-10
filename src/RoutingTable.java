import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
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
        for (RoutingTableRow routingTableRow : table) {
            if(routingTableRow.getVizinho().equals(vizinho)){
                table.remove(routingTableRow);
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

    public boolean vizinhoExists(InetAddress vizinho){
        boolean contains = false;
        for (RoutingTableRow routingTableRow : table) {
            if(routingTableRow.equals(vizinho)) contains = true;
        }
        return contains;
    }

    public void updateTable(RoutingTable table,InetAddress vizinho){
        for (RoutingTableRow routingTableRow : table.getTable()) {
            if(!vizinhoExists(routingTableRow.getVizinho())){
                table.addRow(routingTableRow.getVizinho(),vizinho,getHopNumber(vizinho)+1);
            }
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ROUTING TABLE\n");
        sb.append("-------------------------------------------\n");
        sb.append("|     Vizinho     | Next Hop | Hop Number |\n");
        sb.append("-------------------------------------------\n");
        for (RoutingTableRow routingTableRow : table) {
            sb.append(routingTableRow.getVizinho() + " | " + routingTableRow.getNextHop() + " | " + routingTableRow.getHopNumber());
            sb.append("-------------------------------------------\n");
        }
        return sb.toString();
    }
}
