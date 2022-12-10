import java.net.InetAddress;

public class RoutingTableRow{
    private InetAddress nextHop;
    private InetAddress vizinho;
    private int hopNumber;


    public RoutingTableRow(InetAddress vizinho, InetAddress nextHop, int hopNumber) {
        this.nextHop = nextHop;
        this.vizinho = vizinho;
        this.hopNumber = hopNumber;
    }


    public RoutingTableRow() {
    }

    public InetAddress getNextHop() {
        return this.nextHop;
    }

    public void setNextHop(InetAddress nextHop) {
        this.nextHop = nextHop;
    }

    public InetAddress getVizinho() {
        return this.vizinho;
    }

    public void setVizinho(InetAddress vizinho) {
        this.vizinho = vizinho;
    }

    public int getHopNumber() {
        return this.hopNumber;
    }

    public void setHopNumber(int hopNumber) {
        this.hopNumber = hopNumber;
    }

    @Override
    public String toString() {
        return "{" +
            " nextHop='" + getNextHop() + "'" +
            ", vizinho='" + getVizinho() + "'" +
            ", hopNumber='" + getHopNumber() + "'" +
            "}";
    }

    
    

}