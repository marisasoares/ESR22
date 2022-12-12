import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class StatPacket implements Serializable{
    
    private long timestamp;
    private int timeToLive;
    private RoutingTable table;
    private boolean requestVideo;

    public StatPacket(RoutingTable table){
        this.timeToLive = 60;
        this.table = table;        
        this.timestamp = System.currentTimeMillis();
        this.requestVideo = false;
    }

    public StatPacket(Boolean requestVideo){
        this.timeToLive = 0;
        this.table = null;        
        this.timestamp = System.currentTimeMillis();
        this.requestVideo = true;
    }

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

    public boolean getRequestVideo() {
        return this.requestVideo;
    }

    public void setRequestVideo(boolean requestVideo) {
        this.requestVideo = requestVideo;
    }
    

    public boolean updatePacket(RoutingTable table){
        boolean readyToSend = true;
        timeToLive -= 1;
        this.table = table;
        if(timeToLive <= 0) readyToSend = false;
        timestamp = System.currentTimeMillis();
        return readyToSend;
    }
    

    public RoutingTable getTable() {
        return this.table;
    }

    public void setTable(RoutingTable table) {
        this.table = table;
    }

    @Override
    public String toString() {
        return "{" +
            " timestamp='" + getTimestamp() + "'" +
            ", timeToLive='" + getTimeToLive() + "'" +
            ", table='" + getTable() + "'" +
            ", requestVideo='" + getRequestVideo() + "'" +
            "}";
    }

    public byte[] convertToBytes() throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        oos.flush();
        return baos.toByteArray();
    }

    public static StatPacket fromBytes(byte[] bytes) throws IOException, ClassNotFoundException{
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (StatPacket) ois.readObject();
    }
    

    
    
    
}
