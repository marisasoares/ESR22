import java.io.Serializable;
import java.net.InetAddress;

public class RoutingTableRow implements Serializable{
    private InetAddress nextHop;
    private InetAddress network;
    private int hopNumber;
    private boolean requestsStream;
    private long delay;


    public RoutingTableRow(InetAddress network, InetAddress nextHop, int hopNumber) {
        this.nextHop = nextHop;
        this.network = network;
        this.requestsStream = false;
        this.delay = 0;
        this.hopNumber = hopNumber;
    }

    public RoutingTableRow(InetAddress network, InetAddress nextHop, int hopNumber, boolean requestsStream, Long delay) {
        this.nextHop = nextHop;
        this.network = network;
        this.requestsStream = requestsStream;
        this.delay = delay;
        this.hopNumber = hopNumber;
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

    public boolean getRequestsStream() {
        return this.requestsStream;
    }

    public void setRequestStream(boolean requestsStream) {
        this.requestsStream = requestsStream;
    }

    public long getDelay() {
        return this.delay;
    }

    public void setDelay(Long delay) {
        this.delay = delay;
    }


    @Override
    public String toString() {
        return "{" +
            " nextHop='" + getNextHop() + "'" +
            ", network='" + getNetwork() + "'" +
            ", hopNumber='" + getHopNumber() + "'" +
            ", requestsStream='" + getRequestsStream() + "'" +
            ", delay='" + getDelay() + "'" +
            "}";
    }

}