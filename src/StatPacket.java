import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;

public class StatPacket implements Serializable{
    
    private LocalDateTime timestamp;
    private int timeToLive;
    private RoutingTable table;

    public StatPacket(RoutingTable table){
        this.timestamp = LocalDateTime.now();
        this.timeToLive = 60;
        this.table = table;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getTimeToLive() {
        return this.timeToLive;
    }

    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    public boolean updatePacket(RoutingTable table){
        boolean readyToSend = true;
        timestamp = LocalDateTime.now();
        timeToLive -= 1;
        this.table = table;
        if(timeToLive <= 0) readyToSend = false;
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
