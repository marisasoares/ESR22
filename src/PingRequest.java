import java.net.InetAddress;

public class PingRequest {

    /* The origin of the ping */
    private InetAddress pingAddress;
    /* The sequence number associated */
    private int sequenceNumber;
    /* The timestamp of the ping */
    private long timestamp;
    /* The number of times timeout was received */
    private int timeout;

    public PingRequest(InetAddress pingAddress, int sequenceNumber) {
        this.pingAddress = pingAddress;
        this.sequenceNumber = sequenceNumber;
        this.timestamp = System.currentTimeMillis();
        this.timeout = 0;
    }

    public InetAddress getPingAddress() {
        return this.pingAddress;
    }

    public void setPingAddress(InetAddress pingAddress) {
        this.pingAddress = pingAddress;
    }

    public int getSequenceNumber() {
        return this.sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public PingRequest pingAddress(InetAddress pingAddress) {
        setPingAddress(pingAddress);
        return this;
    }

    public PingRequest sequenceNumber(int sequenceNumber) {
        setSequenceNumber(sequenceNumber);
        return this;
    }

    public PingRequest timestamp(long timestamp) {
        setTimestamp(timestamp);
        return this;
    }


    public int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }


    @Override
    public String toString() {
        return "{" +
            " pingAddress='" + getPingAddress() + "'" +
            ", sequenceNumber='" + getSequenceNumber() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", timeout='" + getTimeout() + "'" +
            "}";
    }


    
}
