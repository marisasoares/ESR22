import java.io.Serializable;
import java.net.InetAddress;

public class RoutingTableRow implements Serializable{
    private InetAddress nextHop;
    private InetAddress network;
    private int hopNumber;


    public RoutingTableRow(InetAddress network, InetAddress nextHop, int hopNumber) {
        this.nextHop = nextHop;
        this.network = network;
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

    public InetAddress getNetwork() {
        return this.network;
    }

    public void setNetwork(InetAddress network) {
        this.network = network;
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
            ", network='" + getNetwork() + "'" +
            ", hopNumber='" + getHopNumber() + "'" +
            "}";
    }
}