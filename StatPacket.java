import java.io.Serializable;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StatPacket implements Serializable{
    
    private LocalDateTime timestamp;
    private int timeToLive;
    private List<InetAddress> visited;

    public StatPacket(InetAddress ipAddress){
        timestamp = LocalDateTime.now();
        timeToLive = 60;
        visited = new ArrayList<>();
        visited.add(ipAddress);
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

    public boolean updatePacket(InetAddress ipAddress){
        boolean readyToSend = true;
        timestamp = LocalDateTime.now();
        timeToLive -= 1;
        visited.add(ipAddress);
        if(timeToLive <= 0) readyToSend = false;
        return readyToSend;
    }
    
    private List<InetAddress> getVisited() {
        return visited;
    }

    @Override
    public String toString() {
        return "{" +
            " timestamp='" + getTimestamp() + "'" +
            ", ttl='" + getTimeToLive() + "'" +
            ", visited='" + getVisited() + "'" +
            "}";
    }

    
    
    
}
