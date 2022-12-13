import java.io.Serializable;
import java.net.InetAddress;

public class RoutingTableRow implements Serializable{
    private InetAddress nextHop;
    private InetAddress address;
    private int hopNumber;
    private boolean requestStream;
    private long delay;


    public RoutingTableRow(InetAddress address, InetAddress nextHop, int hopNumber) {
        this.nextHop = nextHop;
        this.address = address;
        this.requestStream = false;
        this.delay = 0;
        this.hopNumber = hopNumber;
    }

    public RoutingTableRow(InetAddress address, InetAddress nextHop, int hopNumber, boolean requestStream, Long delay) {
        this.nextHop = nextHop;
        this.address = address;
        this.requestStream = requestStream;
        this.delay = delay;
        this.hopNumber = hopNumber;
    }

    public InetAddress getNextHop() {
        return this.nextHop;
    }

    public void setNextHop(InetAddress nextHop) {
        this.nextHop = nextHop;
    }

    public InetAddress getAddress() {
        return this.address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public int getHopNumber() {
        return this.hopNumber;
    }

    public void setHopNumber(int hopNumber) {
        this.hopNumber = hopNumber;
    }

    public boolean requestStream() {
        return this.requestStream;
    }

    public void setRequestStream(boolean requestStream) {
        this.requestStream = requestStream;
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
            ", address='" + getAddress() + "'" +
            ", hopNumber='" + getHopNumber() + "'" +
            ", requestStream='" + requestStream() + "'" +
            ", delay='" + getDelay() + "'" +
            "}";
    }

}