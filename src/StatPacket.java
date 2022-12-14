import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class StatPacket implements Serializable{

    public enum Type {TABLEREQUEST,TABLERESPONSE,PING,PONG};

    /* System time in milisseconds when this packet was created */
    private long timestamp;
    /* Number of jumps that the packet can take at maximum */
    private int timeToLive;
    /* The routing table to send */
    private RoutingTable table;
    /* Tells if the current ONode wants to receive the video stream */
    private boolean requestStream;
    
    /* Type of packet 
     * TABLEREQUEST to ask for table, 
     * TABLERESPONSE to send updated table without needing response
     * STREAMREQUEST to request the stream
     * PING to make a ping request,
     * PONG to answer ping.
     */
    private Type type;

    /* Init all fields and sets routing table */
    public StatPacket(RoutingTable table,Type type, boolean requestStream){
        this.timeToLive = 60;
        this.table = table;
        this.type = type;    
        this.requestStream = requestStream;
        this.timestamp = System.currentTimeMillis();
        
    }

    public StatPacket(RoutingTable table,Type type){
        this.timeToLive = 60;
        this.table = table;
        this.type = type;    
        this.requestStream = false;
        this.timestamp = System.currentTimeMillis();
        
    }

    /* Used to make ping pong */
    public StatPacket(Type type){
        this.timeToLive = 60;
        this.type = type;    
        this.table = null;
        this.timestamp = System.currentTimeMillis();
    }

    /* Getters and Setters */
    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public int getTimeToLive() {
        return this.timeToLive;
    }

    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public RoutingTable getTable() {
        return this.table;
    }

    public void setTable(RoutingTable table) {
        this.table = table;
    }

    public boolean requestStream(){
        return this.requestStream;
    }

    public void requestStream(boolean requestStream){
        this.requestStream = requestStream;
    }
    
    /* Usually run after receiving a packet, updates the ttl, timestamp and table */
    public boolean updatePacket(RoutingTable table){
        boolean readyToSend = true;
        timeToLive -= 1;
        this.table = table;
        if(timeToLive <= 0) readyToSend = false;
        timestamp = System.currentTimeMillis();
        return readyToSend;
    }
    

    @Override
    public String toString() {
        return "{" +
            " timestamp='" + getTimestamp() + "'" +
            ", timeToLive='" + getTimeToLive() + "'" +
            ", table='" + getTable() + "'" +
            ", type='" + getType() + "'" +
            ", requestStream='" + requestStream() + "'" +
            "}";
    }

    /* Converts the packet in a stream of bytes ready to be sent over a network */
    public byte[] convertToBytes() throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        oos.flush();
        return baos.toByteArray();
    }

    /* Reconstruct a packet from a stream of bytes */
    public static StatPacket fromBytes(byte[] bytes) throws IOException, ClassNotFoundException{
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (StatPacket) ois.readObject();
    }
    

    
    
    
}
